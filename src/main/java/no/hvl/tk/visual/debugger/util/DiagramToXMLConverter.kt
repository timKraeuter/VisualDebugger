package no.hvl.tk.visual.debugger.util

import com.intellij.openapi.diagnostic.Logger
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBException
import jakarta.xml.bind.Marshaller
import java.io.StringWriter
import kotlin.String
import no.hvl.tk.visual.debugger.domain.ObjectDiagram
import no.hvl.tk.visual.debugger.util.ClassloaderUtil.runWithContextClassloader

object DiagramToXMLConverter {
  private val LOGGER = Logger.getInstance(DiagramToXMLConverter::class.java)

  private lateinit var jaxbContext: JAXBContext
  private lateinit var jaxbMarshaller: Marshaller

  init {
    runWithContextClassloader {
      jaxbContext = JAXBContext.newInstance(ObjectDiagram::class.java)
      jaxbMarshaller = jaxbContext.createMarshaller()
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    }
  }

  @JvmStatic
  fun toXml(objectDiagram: ObjectDiagram): String {
    return runWithContextClassloader { marshallDiagram(objectDiagram) }
  }

  private fun marshallDiagram(objectDiagram: ObjectDiagram): String {
    val sw = StringWriter()
    try {
      jaxbMarshaller.marshal(objectDiagram, sw)
    } catch (e: JAXBException) {
      LOGGER.error(e)
    }
    return sw.toString()
  }
}
