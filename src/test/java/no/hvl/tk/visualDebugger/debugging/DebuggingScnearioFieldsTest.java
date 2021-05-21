package no.hvl.tk.visualDebugger.debugging;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

public class DebuggingScnearioFieldsTest {
    private CollectionHolder<String> stringCollection = new CollectionHolder<>(
            Lists.newArrayList("1", "2", "3"));

    @Test
    void someTest() {
        String test = "123";
        Integer wurst = 1;
        System.out.println(test);
        System.out.println(test);
        System.out.println(test);
        System.out.println(test);
    }
}
