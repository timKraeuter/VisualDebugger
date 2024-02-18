package no.hvl.tk.visual.debugger.server.endpoint

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.diagnostic.Logger

@JvmRecord
data class UIConfig(val savedDebugSteps: Int, val coloredDiff: Boolean) {
    fun serialize(): String {
        val mapper = ObjectMapper()
        try {
            return mapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            LOGGER.error(e)
            return "JsonProcessingException: $e"
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(
            UIConfig::class.java
        )
    }
}
