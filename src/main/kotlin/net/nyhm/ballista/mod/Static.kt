package net.nyhm.ballista.mod

import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import net.nyhm.ballista.BallistaModule

data class StaticFilesSpec(
    val paths: Iterable<String>
)

/**
 * This module enables static file serving
 */
open class StaticFilesModule(val spec: StaticFilesSpec, val location: Location): BallistaModule {
  override fun config(config: JavalinConfig) {
    spec.paths.forEach { config.staticFiles.add(it, location) }
  }
}