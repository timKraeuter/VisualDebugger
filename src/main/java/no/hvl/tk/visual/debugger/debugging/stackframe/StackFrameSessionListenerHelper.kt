package no.hvl.tk.visual.debugger.debugging.stackframe

import com.sun.jdi.*
import java.util.*

object StackFrameSessionListenerHelper {
  private val INTERNAL_PACKAGES =
      arrayOf(
          "java.",
          "javax.",
          "sun.",
          "jdk.",
          "com.sun.",
          "com.intellij.",
          "org.junit.",
          "jh61b.junit.",
          "jh61b.",
      )

  @JvmStatic
  fun getIterator(thread: ThreadReference?, obj: ObjectReference?): Iterator<Value?> {
    val i = invokeSimple(thread, obj, "iterator") as ObjectReference?
    return object : MutableIterator<Value?> {
      override fun hasNext(): Boolean {
        val hasNext = invokeSimple(thread, i, "hasNext") as BooleanValue? ?: return false
        return hasNext.value()
      }

      override fun next(): Value? {
        return invokeSimple(thread, i, "next")!!
      }

      override fun remove() {
        throw UnsupportedOperationException("Should not be invoked!")
      }
    }
  }

  @JvmStatic
  fun invokeSimple(thread: ThreadReference?, r: ObjectReference?, name: String?): Value? {
    return try {
      r!!.invokeMethod(thread, r.referenceType().methodsByName(name)[0], emptyList(), 0)
    } catch (e: Exception) {
      null
    }
  }

  @JvmStatic
  fun implementsInterface(obj: ObjectReference, iface: String): Boolean {
    if (obj.referenceType() is ClassType) {
      val classType = obj.referenceType() as ClassType
      val queue: Queue<InterfaceType> = ArrayDeque<InterfaceType>(classType.interfaces())
      while (!queue.isEmpty()) {
        val t = queue.poll()
        if (t.name() == iface) {
          return true
        }
        queue.addAll(t.superinterfaces())
      }
    }
    return false
  }

  // input format: [package.]ClassName:lineno or [package.]ClassName
  @JvmStatic
  fun isInternalPackage(name: String): Boolean {
    return Arrays.stream(INTERNAL_PACKAGES).anyMatch { prefix: String? ->
      name.startsWith(prefix!!)
    }
  }
}
