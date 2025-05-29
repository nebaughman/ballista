package net.nyhm.ballista.mod

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.Context
import net.nyhm.ballista.BallistaModule
import net.nyhm.ballista.Log

interface RequestLogger {
  fun config(app: Javalin) {}
  fun log(ctx: Context, ms: Float)
}

/**
 * Javalin can have only one request logger. If more than one is needed, use this module
 * and register RequestLogger instances here. They'll each be given the chance to log.
 */
class RequestLog: BallistaModule {

  private val loggers = mutableListOf<RequestLogger>()

  fun add(logger: RequestLogger) = loggers.add(logger)

  fun addAll(loggers: List<RequestLogger>) = this.loggers.addAll(loggers)

  override fun config(config: JavalinConfig) {
    config.requestLogger.http { ctx, ms ->
      loggers.forEach { it.log(ctx, ms) }
    }
  }
}

/**
 * Log endpoint access
 */
object AccessLogger: RequestLogger {
  override fun log(ctx: Context, ms: Float) {
    val status = ctx.status()
    val msg = {
      val time = if (ms > 1) ms.toInt().toString() else "<1" // "%.2f".format(ms)
      "(${time}ms) [$status] ${ctx.method()} ${ctx.path()}"
    }
    if (status.code >= 400) Log.warn(this, msg = msg)
    else Log.info(this, msg)
  }
}