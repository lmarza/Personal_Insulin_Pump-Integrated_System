package LD_InsulinPump;

import LD_InsulinPump_Mock.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
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
public class AppController
{
    private final int initialDelay = 5; //in seconds
    private final int measurementSchedule = 10; //in seconds
    private final int hardwareTestSchedule = 1; // in seconds
    private final int insulinMinDose = 1;

    @Autowired
    private SimpMessagingTemplate template;
    private ControllerState state;
    private List<Measurement> measurements;
    private Sensor sensor;
    private Pump pump;
    private NeedleAssembly needleAssembly;
    private ScheduledExecutorService executor;

    @Autowired
    public AppController(List<Measurement> measurements, ControllerState state, Sensor sensor, Pump pump, NeedleAssembly needleAssembly, SimpMessagingTemplate template) {
        this.measurements = measurements;
        this.state = state;
        this.sensor = sensor;
        this.pump = pump;
        this.needleAssembly = needleAssembly;
        this.template = template;
        executor = Executors.newScheduledThreadPool(1);
    }

    @RequestMapping("/")
    public String index()
    {
        runAndSendMeasurement(executor);
        runAndSendHardwareCheck(executor);

        return "insulinPump";
    }

    @RequestMapping("/rebootDevice")
    public String rebootDevice()
    {
        measurements.clear();
        state = ControllerState.RUNNING;
        executor.shutdownNow();
        executor = Executors.newScheduledThreadPool(1);

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
                    currentMeasurement = measurementFlow(measurements); //this also updates the state attribute

                // check again if the state has changed (eg: hardware test failure)
                if(currentMeasurement != null && state.equals(ControllerState.RUNNING))
                {
                    template.convertAndSend("/topic/measurements", currentMeasurement);

                }

            }
        };

        // Call measurement every 10 seconds
        executor.scheduleAtFixedRate(runMeasurement, initialDelay, measurementSchedule, TimeUnit.SECONDS);
    }


    //1 sec
    //@Scheduled(fixedDelay=1000)
    public void runAndSendHardwareCheck(ScheduledExecutorService executor)
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

    public Measurement measurementFlow(List<Measurement> measurements)
    {
        Float bloodSugarLevel;
        Integer compDose;

        try
        {
            if(state.equals(ControllerState.RUNNING))
            {
                bloodSugarLevel = measureBloodSugarLevel();
                updateMeasurement(bloodSugarLevel, measurements);
                compDose = computeInsulinToInject(measurements.get(measurements.size()-1));
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

    public void checkHardwareIssue() throws HardwareIssueException
    {
        if(!((SensorRandomImpl) sensor).isHardwareWorking())
            throwExceptionAndSend("Hardware issue: sensor issue");

        if(!((PumpRandomImpl) pump).isHardwareWorking())
            throwExceptionAndSend("Hardware issue: pump issue");

        if(!((NeedleRandomImpl) needleAssembly).isHardwareWorking())
            throwExceptionAndSend("Hardware issue: needle issue");
    }

    public Float measureBloodSugarLevel() throws HardwareIssueException
    {
        try
        {
           return sensor.runMeasurement();
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            this.template.convertAndSend("/topic/state", "Hardware issue: sensor issue");
            throw e;
        }
    }

    public void updateMeasurement(Float lastMeasurement, List<Measurement> measurementList){
        if(measurementList.isEmpty())
            measurementList.add(new Measurement(lastMeasurement));
        else if (measurementList.size() == 1)
            measurementList.add(new Measurement(measurementList.get(0).getR2(), lastMeasurement));
        else
            measurementList.add(new Measurement(measurementList.get(measurementList.size()-1).getR1(), measurementList.get(measurementList.size()-1).getR2(),lastMeasurement));
    }

    public int computeInsulinToInject(Measurement measurement)
    {
        int compDose = 0;

        if(measurement.hasExactlyOneMeasurement())
            return 0;

        // this is executed when we have at least two element in the ArrayList
        // Sugar level falling or stable
        if(measurement.getR2() <= measurement.getR1()){
            return 0;
        }

        // this is executed when we have at least three element in the ArrayList
        if(measurement.hasThreeMeasurements())
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
                    return insulinMinDose;
                }
            }
        }

        return compDose;
    }

    public void injectInsulin(Integer insulinToInject) throws HardwareIssueException
    {
        pump.collectInsulin(insulinToInject);
        needleAssembly.injectInsulin(insulinToInject);
    }

    public void throwExceptionAndSend(String message) throws HardwareIssueException {
        state = ControllerState.ERROR;
        template.convertAndSend("/topic/state", message);
        throw new HardwareIssueException(message);
    }

    public SimpMessagingTemplate getTemplate() {
        return template;
    }

    public ControllerState getState() {
        return state;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Pump getPump() {
        return pump;
    }

    public NeedleAssembly getNeedleAssembly() {
        return needleAssembly;
    }
}
