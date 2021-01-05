package LD_insulinPump;

import LD_InsulinPump.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestMeasurementJU {
    @Test
    public void testMeasurementEmptyConstructor() {
        Measurement measurement = new Measurement();
        assertNull(measurement.getCompDose());
        assertNull(measurement.getR0());
        assertNull(measurement.getR1());
        assertNull(measurement.getR2());
    }

    @Test
    public void testMeasurementConstructorOneParam() {
        Measurement measurement = new Measurement(5f);
        assertEquals(Integer.valueOf(0), measurement.getCompDose());
        assertNull(measurement.getR0());
        assertNull(measurement.getR1());
        assertEquals(Float.valueOf(5f), measurement.getR2());
    }

    @Test
    public void testMeasurementConstructorTwoParam() {
        Measurement measurement = new Measurement(21.9f, 5f);
        assertEquals(Integer.valueOf(0), measurement.getCompDose());
        assertNull(measurement.getR0());
        assertEquals(Float.valueOf(21.9f), measurement.getR1());
        assertEquals(Float.valueOf(5f), measurement.getR2());
    }

    @Test
    public void testMeasurementConstructorThreeParam() {
        Measurement measurement = new Measurement(7.61f, 21.9f, 5f);
        assertEquals(Integer.valueOf(0), measurement.getCompDose());
        assertEquals(Float.valueOf(7.61f), measurement.getR0());
        assertEquals(Float.valueOf(21.9f), measurement.getR1());
        assertEquals(Float.valueOf(5f), measurement.getR2());
    }

    @Test
    public void testMeasurementCompDose() {
        Measurement measurement = new Measurement();
        measurement.setCompDose(5);
        assertEquals(Integer.valueOf(5), measurement.getCompDose());
    }

    @Test
    public void testMeasurementR0() {
        Measurement measurement = new Measurement();
        measurement.setR0(5f);
        assertEquals(Float.valueOf(5f), measurement.getR0());
    }

    @Test
    public void testMeasurementR1() {
        Measurement measurement = new Measurement();
        measurement.setR1(5f);
        assertEquals(Float.valueOf(5f), measurement.getR1());
    }

    @Test
    public void testMeasurementR2() {
        Measurement measurement = new Measurement();
        measurement.setR2(5f);
        assertEquals(Float.valueOf(5f), measurement.getR2());
    }

    @Test
    public void testToString() {
        Measurement measurement = new Measurement(5.212f, 16.2344f, 27.364f);
        assertEquals("Measurement[compDose=0, r0=5,21, r1=16,23, r2=27,36]", measurement.toString());
    }

    @Test
    public void testHasExactlyOneMeasurement() {
        Measurement m;

        // empty
        m = new Measurement();
        assertFalse(m.hasExactlyOneMeasurement());

        // only r2
        m = new Measurement(2.12f);
        assertTrue(m.hasExactlyOneMeasurement());

        // only r1 r2
        m = new Measurement(2.12f, 9.51f);
        assertFalse(m.hasExactlyOneMeasurement());

        // only r0 r1 r2
        m = new Measurement(2.12f, 9.51f, 31.64f);
        assertFalse(m.hasExactlyOneMeasurement());
    }

    @Test
    public void testHasThreeMeasurements() {
        Measurement m;

        // empty
        m = new Measurement();
        assertFalse(m.hasThreeMeasurements());

        // only r2
        m = new Measurement(2.12f);
        assertFalse(m.hasThreeMeasurements());

        // only r1 r2
        m = new Measurement(2.12f, 9.51f);
        assertFalse(m.hasThreeMeasurements());

        // only r0 r1 r2
        m = new Measurement(2.12f, 9.51f, 31.64f);
        assertTrue(m.hasThreeMeasurements());

        // only r1
        m = new Measurement(9.51f, null);
        assertFalse(m.hasThreeMeasurements());

        // only r0
        m = new Measurement(9.51f, null, null);
        assertFalse(m.hasThreeMeasurements());

        // only r1, r0
        m = new Measurement(9.51f, 6.35f, null);
        assertFalse(m.hasThreeMeasurements());

        // only r2, r0
        m = new Measurement(9.51f, null, 6.35f);
        assertFalse(m.hasThreeMeasurements());
    }

    @Test
    public void testEquals()
    {
        assertTrue(new Measurement(5f, 4.23f, 18.92f).equals(new Measurement(5f, 4.23f, 18.92f)));
        assertTrue(new Measurement(4.23f, 18.92f).equals(new Measurement(4.23f, 18.92f)));
        assertTrue(new Measurement(18.92f).equals(new Measurement(18.92f)));
    }

    @Test
    public void testHashcode()
    {
        assertEquals(new Measurement(5f, 4.23f, 18.92f).hashCode(), new Measurement(5f, 4.23f, 18.92f).hashCode());
    }
}
