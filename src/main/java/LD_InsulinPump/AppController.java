package LD_InsulinPump;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AppController {
    private ControllerState state = ControllerState.RUNNING;
    private List<Measurement> measurements = new ArrayList<Measurement>();
    private TestHardware testHardware = new TestHardware();
    private Sensor sensor = new Sensor();
    private Pump pump = new Pump();
    private NeedleAssembly needleAssembly = new NeedleAssembly();

    private final int insulinMinDose = 1;


    @RequestMapping("/")
    public String index(){
        System.out.println("PROVAAA");
        return "redirect:/insulinPump";
    }

    @RequestMapping("/insulinPump")
    public String insulinPump(Model model){
        Float bloodSugarLevel;
        Integer compDose;

        try
        {
            if(state.equals(ControllerState.RUNNING))
            {
                checkHardwareIssue();
                bloodSugarLevel = measureBloodSugarLevel();
                updateMeasurement(bloodSugarLevel);
                compDose = computeInsulineToInject();
                measurements.get(measurements.size()-1).setCompDose(compDose);
                injectInsulin(compDose);
                System.out.println(measurements.get(measurements.size()-1).toString());
                return "insulinPump";
            }
        }
        catch (HardwareIssueException e)
        {
            //display message of reboot
            System.out.println("HardwareIssue: reboot device!");
        }

        return "insulinPump";
    }



    private void checkHardwareIssue() throws HardwareIssueException {
        try
        {
            testHardware.testAllSystem();
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            // update display with error
            //display.showError("error message");
            System.out.println("HardwareIssue");
            throw e;
        }
    }

    private Float measureBloodSugarLevel()
    {
        //check sensor issue
        try
        {
            testHardware.testSensor(sensor);
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            System.out.println("SensorIssue");
            return null;
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

    private void injectInsulin(Integer insulinToInject)
    {
        // run test pump
        try
        {
            testHardware.testPump(pump);
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            System.out.println("PumpIssue");
            return;
        }

        pump.collectInsulin(insulinToInject);

        // run test needle
        try
        {
            testHardware.testNeedle(needleAssembly);
        }
        catch (HardwareIssueException e)
        {
            state = ControllerState.ERROR;
            System.out.println("NeedleIssue");
            return;
        }

        needleAssembly.injectInsulin(insulinToInject);
    }


}
