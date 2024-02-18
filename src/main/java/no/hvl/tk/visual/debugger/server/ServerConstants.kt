package no.hvl.tk.visual.debugger.server

object ServerConstants {
  const val HOST_NAME: String = "localhost"
  const val VISUAL_DEBUGGING_API_SERVER_PORT: Int = 8071

  const val STATIC_RESOURCE_PATH: String = "/ui/"
  const val UI_SERVER_PORT: Int = 8070

  @JvmField val UI_SERVER_URL: String = String.format("http://%s:%s", HOST_NAME, UI_SERVER_PORT)
  @JvmField
  val UI_SERVER_URL_EMBEDDED: String =
      String.format("http://%s:%s?embedded=true", HOST_NAME, UI_SERVER_PORT)
}
