package net.nyhm.ballista

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import org.eclipse.jetty.server.Server

/**
 * A module has the opportunity to configure Javalin.
 * Each of the [config] methods is called once per module.
 * Each has a no-op default implementation, if not needed.
 */
interface BallistaModule {
  /**
   * Configure the [JavalinConfig].
   * Do not use [JavalinConfig.server]; instead use the `config(Server)` method.
   */
  fun config(config: JavalinConfig) {}

  /**
   * Configure the underlying Jetty [Server].
   */
  fun config(server: Server) {}

  /**
   * Configure the [Javalin] app instance.
   * This is where to add routes, for example.
   */
  fun config(app: Javalin) {}
}

/**
 * Construct a Ballista server instance with a set of modules to start.
 * The injector should be configured with any external dependencies.
 */
class Ballista(
  private val modules: List<BallistaModule>,
  private val processor: Processor
) {
  /**
   * The active Javalin server instance
   */
  private var app: Javalin? = null

  /**
   * Ballista can be started only once
   */
  fun start() = apply {
    if (app != null) throw IllegalStateException("Already started") // only once
    //val mods = di.getAll(BallistaModule::class)
    val app = Javalin.create { config ->
      config.showJavalinBanner = false
      config.appData(ProcessorKey, processor) // processor stored as app data
      modules.forEach { it.config(config) } // modules can config config
      config.jetty.modifyServer { server ->
        modules.forEach { it.config(server) } // modules can config server
      }
    }
    modules.forEach { it.config(app) } // modules can config Javalin app
    app.start() // http(s) connector(s) specif(y|ies) port(s)
    this.app = app // prior exception will not set app
  }

  /**
   * Stop the Javalin server
   */
  fun stop() {
    app?.stop()
    //app = null // TODO: Define whether ok for modules to be re-initialized (if start() called again)
  }
}

class BallistaException(message: String, cause: Throwable? = null): Exception(message, cause)
