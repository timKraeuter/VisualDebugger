package no.hvl.tk.visual.debugger.util;

public class ClassloaderUtil {
    private ClassloaderUtil() {
        // only util methods
    }

    public static <V> V runWithContextClassloader(final Executable<V> executable) {
        final ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(executable.getClass().getClassLoader());
            // code working with ServiceLoader here
            return executable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }
}
