package no.hvl.tk.visualDebugger.sample;

import org.junit.jupiter.api.Test;

public class SampleTest {
    @Test
    void testPrimitiveVariables() {
        byte aByte1 = 0;
        Byte aByte2 = 0;
        short aShort1 = 1;
        Short aShort2 = 2;
        int anInt1 = 2;
        Integer anInt2 = 2;
        long aLong1 = 3L;
        Long aLong2 = 3L;
        float aFloat1 = 4.1F;
        Float aFloat2 = 4.1F;
        double aDouble1 = 5.1;
        Double aDouble2 = 5.1;
        char aChar = '6';
        Character aCharacter = '2';
        boolean aBoolean1 = true;
        Boolean aBoolean2 = true;
        String aString = "8";
        System.out.println("Put your breakpoint here");
    }

    @Test
    void testWithOneLayerObjectVariables() {
        Material aMaterial = Material.create("stringValue", 42);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
    }
}
