package no.hvl.tk.visual.debugger.server.endpoint.message

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.diagnostic.Logger

class DebuggingWSMessage @JvmOverloads constructor(
    private val type: DebuggingMessageType,
    val content: String,
    val fileName: String? = null,
    val line: Int? = null
) {
    // Getter needed for serialize
    fun getType(): String {
        return type.typeString
    }

    fun serialize(): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        try {
            return mapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            LOGGER.error(e)
            return "JsonProcessingException: $e"
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(
            DebuggingWSMessage::class.java
        )
    }
}
