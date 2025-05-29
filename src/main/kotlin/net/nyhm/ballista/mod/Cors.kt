package net.nyhm.ballista.mod

import io.javalin.config.JavalinConfig
import io.javalin.plugin.bundled.CorsPluginConfig
import net.nyhm.ballista.BallistaModule
import net.nyhm.ballista.Log

/**
 * Beware: Not all of these can be used together
 */
data class CorsSpec(
  val origins: List<String> = emptyList(), // order important?
  val allOrigins: Boolean = false,
  val reflectClientOrigin: Boolean = false,
  val defaultScheme: String = "https",
  val allowCredentials: Boolean = false,
  val exposeHeaders: Set<String> = emptySet(),
)

// TODO: add to javalin
fun CorsPluginConfig.allowHosts(hosts: List<String>) {
  if (hosts.isEmpty()) return // ignore
  addRule {
    it.allowHost(hosts[0], *hosts.drop(1).toTypedArray())
  }
}

/**
 * Only a single Cors*Module can be used.
 * Use this one for access to a combination of settings.
 */
class CorsModule(val spec: CorsSpec): BallistaModule {
  override fun config(config: JavalinConfig) {
    Log.info(this) { spec }
    config.bundledPlugins.enableCors { cors ->
      cors.allowHosts(spec.origins)
      cors.addRule { corsConfig ->
        corsConfig.defaultScheme = spec.defaultScheme
        if (spec.allOrigins) corsConfig.anyHost()
        corsConfig.allowCredentials = spec.allowCredentials
        corsConfig.reflectClientOrigin = spec.reflectClientOrigin
        spec.exposeHeaders.forEach { corsConfig.exposeHeader(it) }
      }
    }
  }
}

object CorsAllOrigins: BallistaModule {
  override fun config(config: JavalinConfig) {
    config.bundledPlugins.enableCors { cors ->
      cors.addRule { corsConfig ->
        corsConfig.anyHost()
      }
    }
  }
}

class CorsReflectOrigin(
  private val allowCredentials: Boolean = false
): BallistaModule {
  override fun config(config: JavalinConfig) {
    config.bundledPlugins.enableCors { cors ->
      cors.addRule { corsConfig ->
        corsConfig.reflectClientOrigin = true // avoids credential restriction on "*" origin
        corsConfig.allowCredentials = allowCredentials
      }
    }
  }
}

class CorsExposeHeaders(
  private vararg val headers: String
): BallistaModule {
  override fun config(config: JavalinConfig) {
    config.bundledPlugins.enableCors { cors ->
      cors.addRule { corsConfig ->
        headers.forEach { corsConfig.exposeHeader(it) }
      }
    }
  }
}