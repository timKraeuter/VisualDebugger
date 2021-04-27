package sample;

import org.junit.jupiter.api.Test;

public class SampleTest {
    @Test
    void sampleTest() {
        Material aMaterial = Material.create("stringValue", 42);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
        System.out.println(aMaterial);
    }
}
