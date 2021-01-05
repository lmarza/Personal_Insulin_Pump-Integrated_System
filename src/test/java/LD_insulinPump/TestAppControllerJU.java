package LD_insulinPump;

import LD_InsulinPump.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.*;

public class TestAppControllerJU
{
    private List<Measurement> measurements;
    private ControllerState runningState;
    private ControllerState errorState;
    private Sensor normalSensor;
    private Sensor workingSensor;
    private Sensor notWorkingSensor;
    private Pump normalPump;
    private Pump workingPump;
    private Pump notWorkingPump;
    private NeedleAssembly normalNeedle;
    private NeedleAssembly workingNeedle;
    private NeedleAssembly notWorkingNeedle;
    private SimpMessagingTemplate template;

    @Before
    public void cleanAndSet()
    {
        measurements = new ArrayList<>();
        runningState = ControllerState.RUNNING;
        errorState = ControllerState.ERROR;

        normalSensor = new SensorRandomImpl(new Random(), new Random());
        workingSensor = new SensorRandomImpl(new Random(), new RandomHardwareFixTest(1));
        notWorkingSensor = new SensorRandomImpl(new Random(), new RandomHardwareFixTest(0));

        normalPump = new PumpRandomImpl(new Random());
        workingPump = new PumpRandomImpl(new RandomHardwareFixTest(1));
        notWorkingPump = new PumpRandomImpl(new RandomHardwareFixTest(0));

        normalNeedle = new NeedleRandomImpl(new Random());
        workingNeedle = new NeedleRandomImpl(new RandomHardwareFixTest(1));
        notWorkingNeedle = new NeedleRandomImpl(new RandomHardwareFixTest(0));

        template = new SimpMessagingTemplate((message, timeout) -> true);
    }

    @Test
    public void testEmptyAppController()
    {
        AppController appController = new AppController(measurements,
                runningState,
                normalSensor,
                normalPump,
                normalNeedle,
                template);
        assertEquals(measurements, appController.getMeasurements());
        assertEquals(runningState, appController.getState());
        assertEquals(normalSensor, appController.getSensor());
        assertEquals(normalPump, appController.getPump());
        assertEquals(normalNeedle, appController.getNeedleAssembly());
        assertEquals(template, appController.getTemplate());
    }

    @Test
    public void testMeasurementConstructorOneParam()
    {
        Measurement measurement = new Measurement(5f);
        assertEquals(Integer.valueOf(0), measurement.getCompDose());
        assertNull(measurement.getR0());
        assertNull(measurement.getR1());
        assertEquals(Float.valueOf(5f), measurement.getR2());
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testSuccessMeasureBloodSugarLevel() throws HardwareIssueException {
        AppController appController = new AppController(measurements,
                runningState,
                workingSensor,
                workingPump,
                workingNeedle,
                template);
        appController.measureBloodSugarLevel();
    }

    @Test
    public void testFailMeasureBloodSugarLevel()
    {
        AppController appController = new AppController(measurements,
                runningState,
                notWorkingSensor,
                notWorkingPump,
                notWorkingNeedle,
                template);
        assertThrows(HardwareIssueException.class, () -> {
            appController.measureBloodSugarLevel();
        });
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testSuccessInjectInsulin() throws HardwareIssueException {
        AppController appController = new AppController(measurements,
                runningState,
                workingSensor,
                workingPump,
                workingNeedle,
                template);
        appController.injectInsulin(5);
    }

    @Test
    public void testFailInjectInsulin() {
        AppController appController = new AppController(measurements,
                runningState,
                notWorkingSensor,
                notWorkingPump,
                notWorkingNeedle,
                template);
        assertThrows(HardwareIssueException.class, () -> {
            appController.injectInsulin(5);
        });
    }

    @Test
    public void testUpdateMeasurementBloodSugarLevel()
    {
        AppController appController = new AppController(measurements, runningState, normalSensor, normalPump, normalNeedle, template);
        List<Measurement> measurementList = new ArrayList<>();

        //empty list
        appController.updateMeasurement(21.53f, measurementList);
        assertEquals(1, measurementList.size());
        assertEquals(measurementList.get(0).getR2(), Float.valueOf(21.53f));

        //1 element in list
        appController.updateMeasurement(16.74f, measurementList);
        assertEquals(2, measurementList.size());
        assertEquals(measurementList.get(1).getR2(), Float.valueOf(16.74f));
        assertEquals(measurementList.get(1).getR1(), Float.valueOf(21.53f));

        //2 elements in list
        appController.updateMeasurement(3.79f, measurementList);
        assertEquals(3, measurementList.size());
        assertEquals(measurementList.get(2).getR2(), Float.valueOf(3.79f));
        assertEquals(measurementList.get(2).getR1(), Float.valueOf(16.74f));
        assertEquals(measurementList.get(2).getR0(), Float.valueOf(21.53f));

        //3 or more elements in list
        appController.updateMeasurement(5.47f, measurementList);
        assertTrue(measurementList.size() > 3);
        assertEquals(measurementList.get(measurementList.size()-1).getR2(), Float.valueOf(5.47f));
        assertEquals(measurementList.get(measurementList.size()-1).getR1(), Float.valueOf(3.79f));
        assertEquals(measurementList.get(measurementList.size()-1).getR0(), Float.valueOf(16.74f));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level falling (r2 < r1)
     */
    @Test
    public void testComputeInsulinToInjectC1()
    {
        Measurement measurement = new Measurement(15.84f, 10.35f);
        assertTrue(measurement.getR2() < measurement.getR1());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController(measurements, runningState, normalSensor, normalPump, normalNeedle, template).computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level stable (r2 = r1)
     */
    @Test
    public void testComputeInsulinToInjectC2()
    {
        Measurement measurement = new Measurement(15.84f, 15.84f);
        assertEquals(measurement.getR2(), measurement.getR1());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController(measurements,
                runningState,
                normalSensor,
                normalPump,
                normalNeedle,
                template).computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level increasing (r2 > r1)
     * and the rate of increase decreasing ((r2 - r1) < (r1 - r0))
     */
    @Test
    public void testComputeInsulinToInjectC3()
    {
        Measurement measurement = new Measurement(15.34f, 20.84f, 21.84f);
        assertTrue(measurement.getR2() > measurement.getR1());
        assertTrue(measurement.getR2() - measurement.getR1() < measurement.getR1()- measurement.getR0());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController(measurements,
                runningState,
                normalSensor,
                normalPump,
                normalNeedle,
                template).computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level increasing (r2 > r1)
     * and the rate of increase stable or increasing ((r2 - r1) >= (r1 - r0))
     */
    @Test
    public void testComputeInsulinToInjectC4()
    {
        Measurement measurement = new Measurement(15.20f, 20.80f, 34.90f);
        assertTrue(measurement.getR2() > measurement.getR1());
        assertTrue(measurement.getR2() - measurement.getR1() >= measurement.getR1()- measurement.getR0());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController(measurements,
                runningState,
                normalSensor,
                normalPump,
                normalNeedle,
                template).computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(4));
    }

    /**
     * This method tests the computation of insulin to inject in the case of blood sugar level increasing (r2 > r1)
     * and the rate of increase stable or increasing ((r2 - r1) >= (r1 - r0)) and result of rounded division not equal to zero
     * In this case we also test the minDose of insulin injection
     */
    @Test
    public void testComputeInsulinToInjectC5()
    {
        Measurement measurement = new Measurement(33.0f, 34.0f, 35.0f);
        assertTrue(measurement.getR2() > measurement.getR1());
        assertTrue(measurement.getR2() - measurement.getR1() >= measurement.getR1()- measurement.getR0());
        assertEquals(measurement.getCompDose(), Integer.valueOf(0));
        measurement.setCompDose(new AppController(measurements,
                runningState,
                normalSensor,
                normalPump,
                normalNeedle,
                template).computeInsulinToInject(measurement));
        assertEquals(measurement.getCompDose(), Integer.valueOf(1));
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testSuccessRunAndMeasurement() throws InterruptedException
    {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // we create an appController to run test on runAndSendMeasurement method. In particular we create appController
        // with Sensor,Pump and Needle not affected by issues (injection of correct random for test)
        AppController appController = new AppController(measurements,
                runningState,
                workingSensor,
                workingPump,
                workingNeedle,
                template);
        appController.runAndSendMeasurement(executor);
        Thread.sleep(5000);
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testFailRunAndMeasurement() throws InterruptedException
    {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // we create an appController to run test on runAndSendMeasurement method. In particular we create appController
        // with Sensor,Pump and Needle affected by issues (injection of correct random for test)
        AppController appController = new AppController(measurements,
                errorState,
                notWorkingSensor,
                notWorkingPump,
                notWorkingNeedle,
                template);
        appController.runAndSendMeasurement(executor);
        Thread.sleep(5000);
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testSuccessRunAndHardwareCheck() throws InterruptedException
    {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // we create an appController to run test on runAndSendMeasurement method. In particular we create appController
        // with Sensor,Pump and Needle not affected by issues (injection of correct random for test)
        AppController appController = new AppController(measurements,
                runningState,
                workingSensor,
                workingPump,
                workingNeedle,
                template);
        appController.runAndSendHardwareCheck(executor);
        Thread.sleep(5000);
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testFailRunAndHardwareCheck() throws InterruptedException
    {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // we create an appController to run test on runAndSendMeasurement method. In particular we create appController
        // with Sensor,Pump and Needle affected by issues (injection of correct random for test)
        AppController appController = new AppController(measurements,
                runningState,
                notWorkingSensor,
                notWorkingPump,
                notWorkingNeedle,
                template);
        appController.runAndSendHardwareCheck(executor);
        Thread.sleep(5000);
    }


    @Test
    public void testSuccessMeasurementFlow()
    {
        Measurement measurement;
        AppController appController = new AppController(measurements,
                runningState,
                workingSensor,
                workingPump,
                workingNeedle,
                template);

        measurement = appController.measurementFlow(new ArrayList<Measurement>());
        assertNotNull(measurement);
    }

    @Test
    public void testFailMeasurementFlow()
    {
        Measurement measurement;
        AppController appController;

        appController = new AppController(measurements,
                runningState,
                notWorkingSensor,
                notWorkingPump,
                notWorkingNeedle,
                template);
        measurement = appController.measurementFlow(new ArrayList<Measurement>());
        assertNull(measurement);

        appController = new AppController(measurements,
                errorState,
                notWorkingSensor,
                notWorkingPump,
                notWorkingNeedle,
                template);
        measurement = appController.measurementFlow(new ArrayList<Measurement>());
        assertNull(measurement);
    }

    @Test
    public void testCheckHardwareIssue() throws HardwareIssueException
    {
        // fail only sensor
        AppController appController = new AppController(measurements,
                runningState,
                notWorkingSensor,
                workingPump,
                workingNeedle,
                template);

        assertThrows(HardwareIssueException.class, () -> {
            appController.checkHardwareIssue();
        });

        // fail only pump
        AppController appController2 = new AppController(measurements,
                runningState,
                workingSensor,
                notWorkingPump,
                workingNeedle,
                template);

        assertThrows(HardwareIssueException.class, () -> {
            appController2.checkHardwareIssue();
        });

        // fail only needle
        AppController appController3 = new AppController(measurements,
            runningState,
            workingSensor,
            workingPump,
            notWorkingNeedle,
            template);

        assertThrows(HardwareIssueException.class, () -> {
            appController3.checkHardwareIssue();
        });
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void testSuccessCheckHardwareIssue() throws HardwareIssueException
    {
        AppController appController = new AppController(measurements,
                runningState,
                workingSensor,
                workingPump,
                workingNeedle,
                template);
        appController.checkHardwareIssue();
    }

    @Test
    public void testRebootDevice()
    {
        AppController appController = new AppController(measurements,
                runningState,
                normalSensor,
                normalPump,
                normalNeedle,
                template);
        assertEquals("redirect:/", appController.rebootDevice());
    }

    @Test
    public void testIndex()
    {
        AppController appController = new AppController(measurements,
                runningState,
                normalSensor,
                normalPump,
                normalNeedle,
                template);
        assertEquals("insulinPump", appController.index());
    }
}
