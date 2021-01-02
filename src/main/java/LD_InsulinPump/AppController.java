package LD_InsulinPump;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
* return "list"; : torna la vista html di nome 'list.html' ma NON chiama la funzione attaccata al routing (cioè non chiama /list)
* return "redirect:/list"; : torna la vista html di nome 'list.html' E chiama anche la funzione attaccata al routing (chiama anche /list)
* */

@Controller
@EnableScheduling
public class AppController {
    private ControllerState state = ControllerState.RUNNING;
    private List<Measurement> measurements = new ArrayList<Measurement>();
    private TestHardware testHardware = new TestHardware();
    private Sensor sensor = new Sensor();
    private Pump pump = new Pump();
    private NeedleAssembly needleAssembly = new NeedleAssembly();
    private ScheduledExecutorService executor;

    private final int initialDelay = 5; //in seconds
    private final int measurementSchedule = 10; //in seconds
    private final int hardwareTestSchedule = 1; // in seconds
    private final int insulinMinDose = 1;

    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping("/")
    public String index(){
        executor = Executors.newScheduledThreadPool(1);

        runAndSendMeasurement(executor);
        runAndSendHardwareTest(executor);

        return "insulinPump";
    }

    @RequestMapping("/rebootDevice")
    public String rebootDevice()
    {
        measurements.clear();
        state = ControllerState.RUNNING;
        executor.shutdownNow();

        return "redirect:/";
    }

    //10 sec
    //@Scheduled(fixedDelay=10000)
    public void runAndSendMeasurement(ScheduledExecutorService executor)
    {
        Runnable runMeasurement = new Runnable() {
            public void run()
            {
                Measurement currentMeasurement = null;

                if(state.equals(ControllerState.RUNNING))
                    currentMeasurement = measurementFlow(); //this also updates the state attribute

                // check again if the state has changed (eg: hardware test failure)
                if(currentMeasurement != null && state.equals(ControllerState.RUNNING))
                {
                    template.convertAndSend("/topic/measurements", currentMeasurement);
                    System.out.println(currentMeasurement);
                }
            }
        };

        // Call measurement every 10 seconds
        executor.scheduleAtFixedRate(runMeasurement, initialDelay, measurementSchedule, TimeUnit.SECONDS);
    }

    //1 sec
    //@Scheduled(fixedDelay=1000)
    public void runAndSendHardwareTest(ScheduledExecutorService executor)
    {
        Runnable runMeasurement = new Runnable() {
            public void run()
            {
                try
                {
                    if (state.equals(ControllerState.RUNNING)) {
                        checkHardwareIssue();
                    }
                }
                catch (HardwareIssueException e)
                {
                    template.convertAndSend("/topic/state", "Hardware issue: reboot device");
                }
            }
        };

        // Call measurement every 1 second
        executor.scheduleAtFixedRate(runMeasurement, initialDelay, hardwareTestSchedule, TimeUnit.SECONDS);
    }

    public Measurement measurementFlow()
    {
        Float bloodSugarLevel;
        Integer compDose;

        try
        {
            if(state.equals(ControllerState.RUNNING))
            {
                bloodSugarLevel = measureBloodSugarLevel();
                updateMeasurement(bloodSugarLevel);
                compDose = computeInsulineToInject();
                measurements.get(measurements.size()-1).setCompDose(compDose);
                injectInsulin(compDose);
                return measurements.get(measurements.size()-1);
            }
        }
        catch (HardwareIssueException e)
        {
            this.template.convertAndSend("/topic/state", "Hardware Issue: reboot device!");
        }
        return null;
    }


    private void checkHardwareIssue() throws HardwareIssueException
    {
        try
        {
            testHardware.testAllSystem();
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            this.template.convertAndSend("/topic/state", "Hardware issue: general system failure");
            throw e;
        }
    }

    private Float measureBloodSugarLevel() throws HardwareIssueException {
        try
        {
            testHardware.testSensor(sensor);
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            this.template.convertAndSend("/topic/state", "Hardware issue: sensor issue");
            throw e;
        }
        return sensor.runMeasurement();
    }

    private void updateMeasurement(Float lastMeasurement){
        if(measurements.isEmpty())
            measurements.add(new Measurement(lastMeasurement));
        else if (measurements.size() == 1)
            measurements.add(new Measurement(measurements.get(0).getR2(), lastMeasurement));
        else
            measurements.add(new Measurement(measurements.get(measurements.size()-1).getR1(), measurements.get(measurements.size()-1).getR2(),lastMeasurement));

    }

    private int computeInsulineToInject(){
        Measurement measurement = measurements.get(measurements.size()-1);
        int compDose = 0;

        if(oneMeasurement())
            return 0;

        // this is executed when we have at least two element in the ArrayList
        // Sugar level falling or stable
        if(measurement.getR2() <= measurement.getR1()){
            return 0;
        }

        // this is executed when we have at least three element in the ArrayList
        if(atLeastThreeMeasurements())
        {
            // Sugar level increasing and rate of increase decreasing
            if(measurement.getR2() > measurement.getR1())
            {
                if((measurement.getR2() - measurement.getR1()) < (measurement.getR1() - measurement.getR0()))
                    return 0;
                else
                    compDose =  Math.round((measurement.getR2() - measurement.getR1())/4);

                if(compDose == 0)
                {
                    //measurements.get(measurements.size()-1).setCompDose(insulinMinDose);
                    return insulinMinDose;
                }
            }
        }

        //measurements.get(measurements.size()-1).setCompDose(compDose);
        return compDose;
    }

    private boolean oneMeasurement() {
        return measurements.size() == 1;
    }

    private boolean atLeastThreeMeasurements() {
        return measurements.size() > 2;
    }

    private void injectInsulin(Integer insulinToInject) throws HardwareIssueException
    {
        try
        {
            testHardware.testPump(pump);
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            this.template.convertAndSend("/topic/state", "Hardware issue: pump issue");
            throw e;
        }

        pump.collectInsulin(insulinToInject);

        try
        {
            testHardware.testNeedle(needleAssembly);
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            this.template.convertAndSend("/topic/state", "Hardware issue: needle issue");
            throw e;
        }

        needleAssembly.injectInsulin(insulinToInject);
    }
}
