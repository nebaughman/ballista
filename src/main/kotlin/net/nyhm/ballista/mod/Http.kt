package net.nyhm.ballista.mod

import io.javalin.apibuilder.ApiBuilder
import io.javalin.config.JavalinConfig
import io.javalin.http.Context
import net.nyhm.ballista.BallistaModule
import net.nyhm.ballista.info
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import java.net.URI

data class HttpSpec(
    val port: Int = 80
)

/**
 * Adds an HTTP server listening port
 */
class HttpModule(val spec: HttpSpec): BallistaModule {
  override fun config(server: Server) {
    info { "HTTP port ${spec.port}" }
    server.addConnector(
        ServerConnector(server).also { it.port = spec.port }
    )
  }
}

data class RedirectSpec(
    val predicate: (Context) -> Boolean,
    val targetPort: Int,
    val targetScheme: String = "http"
)

/**
 * Adds a Javalin redirect router, which will issue a redirect to the same url at different
 * port and scheme. Use this, for example, to redirect HTTP traffic to HTTPS port.
 */
class RedirectModule(val spec: RedirectSpec): BallistaModule {
  override fun config(config: JavalinConfig) {
    config.router.apiBuilder {
      ApiBuilder.before { ctx ->
        if (spec.predicate.invoke(ctx)) {
          val url = URI(ctx.url()).let {
            URI(
                spec.targetScheme,
                it.userInfo,
                it.host,
                spec.targetPort,
                it.path,
                it.query,
                it.fragment
            )
          }.toURL().toExternalForm()
          ctx.redirect(url)
        }
      }
    }
  }
}
