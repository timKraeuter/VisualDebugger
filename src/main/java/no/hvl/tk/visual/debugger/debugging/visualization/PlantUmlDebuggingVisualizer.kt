package no.hvl.tk.visual.debugger.debugging.visualization

import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import no.hvl.tk.visual.debugger.SharedState
import no.hvl.tk.visual.debugger.domain.*
import no.hvl.tk.visual.debugger.ui.CopyPlantUMLDialog

class PlantUmlDebuggingVisualizer(private val pluginUI: JPanel) : DebuggingInfoVisualizerBase() {
  private var imgLabel: JLabel? = null

  public override fun doVisualizationFurther(diagram: ObjectDiagram) {
    val plantUMLString = toPlantUMLString(diagram)
    SharedState.lastPlantUMLDiagram = plantUMLString
    try {
      val pngData = toImage(plantUMLString, FileFormat.PNG)
      this.addImageToUI(pngData)
    } catch (e: IOException) {
      LOGGER.error(e)
    }
  }

  override fun debuggingActivated() {
    val printDiagram = JButton("Copy diagram")
    printDiagram.addActionListener { CopyPlantUMLDialog().show() }
    pluginUI.add(printDiagram, BorderLayout.SOUTH)
  }

  override fun debuggingDeactivated() {
    // NOOP
  }

  @Throws(IOException::class)
  private fun addImageToUI(pngData: ByteArray) {
    val input = ByteArrayInputStream(pngData)
    val imageIcon = ImageIcon(ImageIO.read(input))

    if (this.imgLabel == null) {
      this.createImageAndAddToUI(imageIcon)
    } else {
      imgLabel!!.icon = imageIcon
    }
    pluginUI.revalidate()
  }

  private fun createImageAndAddToUI(imageIcon: ImageIcon) {
    this.imgLabel = JLabel(imageIcon)
    val scrollPane = JBScrollPane(this.imgLabel)
    pluginUI.add(scrollPane)
  }

  companion object {
    private val LOGGER = Logger.getInstance(PlantUmlDebuggingVisualizer::class.java)
    private const val NULL = "null"
    private const val KEY = "key"
    private const val VALUE = "value"

    @JvmStatic
    fun toPlantUMLString(objectDiagram: ObjectDiagram): String {
      val stringBuilder = StringBuilder()
      stringBuilder.append("@startuml\n")
      // Use this so we are not dependent on a Graphviz/Dot installation on the host machine.
      stringBuilder.append("!pragma layout smetana\n")

      // Sort objects so the visualisation does not change when there are no objects changes.
      val sortedObjects = objectDiagram.objects.stream().sorted().toList()

      val mapLinks: MutableSet<ODLink> = HashSet()
      // Add objects with attributes and collect links. They have to be added after objects.
      addObjectsToDiagramAndCollectLinks(stringBuilder, sortedObjects, mapLinks)

      // Add links.
      addLinksToDiagram(stringBuilder, objectDiagram.links, mapLinks)

      // Add primitive root values if there are any.
      if (!objectDiagram.primitiveRootValues.isEmpty()) {
        addPrimitiveRootValuesToDiagram(objectDiagram, stringBuilder)
      }

      stringBuilder.append("@enduml\n")
      return stringBuilder.toString()
    }

    private fun addPrimitiveRootValuesToDiagram(
        objectDiagram: ObjectDiagram,
        stringBuilder: StringBuilder
    ) {
      stringBuilder.append(
          String.format("object \"%s\" as %s", "PrimitiveVariables", "primitiveVariables"))
      stringBuilder.append(" {\n")
      objectDiagram.primitiveRootValues.stream().sorted().forEach {
          primitiveRootValue: ODPrimitiveRootValue ->
        stringBuilder.append(
            String.format("%s=%s%n", primitiveRootValue.variableName, primitiveRootValue.value))
      }
      stringBuilder.append("}\n")
    }

    private fun addLinksToDiagram(
        stringBuilder: StringBuilder,
        links: Set<ODLink>,
        mapLinks: Set<ODLink>
    ) {
      links
          .stream()
          .sorted() // Ignore links already visualized in maps.
          .filter { odLink: ODLink -> !mapLinks.contains(odLink) }
          .forEach { link: ODLink ->
            stringBuilder.append(
                String.format(
                    "%s --> %s : %s%n", link.from.hashCode(), link.to.hashCode(), link.type))
          }
    }

    private fun addObjectsToDiagramAndCollectLinks(
        stringBuilder: StringBuilder,
        sortedObjects: List<ODObject>,
        mapLinks: MutableSet<ODLink>
    ) {
      val ignoredObjects = HashSet<ODObject>()
      for (`object` in sortedObjects) {
        if (ignoredObjects.contains(`object`)) {
          continue
        }
        // Primitive maps are visualised differently
        if (isPrimitiveJavaMap(`object`)) {
          doPrimitiveMapVisualisation(stringBuilder, ignoredObjects, `object`)
          // Links are not allowed to be shown later on.
          mapLinks.addAll(`object`.links)
          continue
        }

        // Add the object
        stringBuilder.append(
            String.format(
                "object \"%s:%s\" as %s",
                `object`.variableName,
                shortenTypeName(`object`.type),
                `object`.hashCode()))

        // Add object attributes
        if (!`object`.attributeValues.isEmpty()) {
          stringBuilder.append(" {\n")
          `object`.attributeValues
              .stream() // Sort so that objects with the same type have the same order of attributes
              .sorted(Comparator.comparing { obj: ODAttributeValue -> obj.name })
              .forEach { odAttributeValue: ODAttributeValue ->
                stringBuilder.append(
                    String.format("%s=%s%n", odAttributeValue.name, odAttributeValue.value))
              }
          stringBuilder.append("}\n")
        } else {
          stringBuilder.append("\n")
        }
      }
    }

    private fun doPrimitiveMapVisualisation(
        stringBuilder: StringBuilder,
        ignoredObjects: HashSet<ODObject>,
        `object`: ODObject
    ) {
      stringBuilder.append(
          String.format(
              "map \"%s:%s\" as %s",
              `object`.variableName,
              shortenTypeName(`object`.type),
              `object`.hashCode()))
      stringBuilder.append(" {\n")

      `object`.links.stream().sorted().forEach { odLink: ODLink ->
        val mapNode = odLink.to
        ignoredObjects.add(mapNode) // Dont visualize the node as an object anymore!

        val key = mapNode.getAttributeByName(KEY)
        val value = mapNode.getAttributeByName(VALUE)
        if (key.isPresent || value.isPresent) {
          stringBuilder.append(
              String.format(
                  "%s => %s%n",
                  if (key.isPresent) key.get().value else NULL,
                  if (value.isPresent) value.get().value else NULL))
        }
      }

      stringBuilder.append("}\n")
    }

    private fun isPrimitiveJavaMap(`object`: ODObject): Boolean {
      return (isMap(`object`) && isPrimitive(`object`))
    }

    private fun isPrimitive(`object`: ODObject): Boolean {
      // Nodes attached to the link must not have any more links.
      // Key and value are then attributes i.e. primitive.
      return (!`object`.links.isEmpty() &&
          `object`.links.stream().anyMatch { odLink: ODLink -> odLink.to.links.isEmpty() })
    }

    private fun isMap(`object`: ODObject): Boolean {
      return `object`.type.startsWith("java.util") && `object`.type.endsWith("Map")
    }

    private fun shortenTypeName(type: String): Any {
      return type.substring(type.lastIndexOf(".") + 1)
    }

    @Throws(IOException::class)
    fun toImage(plantUMLDescription: String?, format: FileFormat?): ByteArray {
      val reader = SourceStringReader(plantUMLDescription)
      ByteArrayOutputStream().use { outputStream ->
        reader.outputImage(outputStream, FileFormatOption(format))
        return outputStream.toByteArray()
      }
    }
  }
}
