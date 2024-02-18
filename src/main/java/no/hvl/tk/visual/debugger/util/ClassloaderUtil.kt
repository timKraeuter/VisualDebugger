package no.hvl.tk.visual.debugger.util

object ClassloaderUtil {
    @JvmStatic
    fun <V> runWithContextClassloader(executable: Executable<V>): V {
        val current = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = executable.javaClass.classLoader
            return executable.run()
        } finally {
            Thread.currentThread().contextClassLoader = current
        }
    }
}
