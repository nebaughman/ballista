package net.nyhm.ballista.mod

import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import net.nyhm.ballista.BallistaModule
import net.nyhm.ballista.info

data class SpaSpec(
    val subpages: List<String> = emptyList()
)

/**
 * Also see [AppModule] for serving a bundled Vue app.
 *
 * SpaModule makes some assumptions about the Vue app's layout
 * (in SPA and/or multi-page mode).
 *
 * If the Vue app is configured for multi-page mode, and Vue Router is being
 * used by any of those pages (for intra-page client-side SPA-style routing),
 * then those sub-pages must be listed here. They will be handled as SPA page
 * roots.
 *
 * If Vue Router is configured for history mode, additional care must be taken
 * when adding links. Intra-page (SPA-style) links must be handled with the
 * `<router-link>` component, while links to other pages (server-handled,
 * traditional page reload) must be standard `<a href="..">` links.
 *
 * See the project README for more details (and caveats) of this configuration.
 */
class SpaModule(val spec: SpaSpec): BallistaModule {
  override fun config(config: JavalinConfig) {
    // eg, config.addSinglePageRoot("/admin", "/app/admin/index.html")
    spec.subpages.forEach {
      register(config, "/$it", "/app/$it/index.html", Location.CLASSPATH)
    }
    register(config, "/", "/app/index.html", Location.CLASSPATH)
  }

  private fun register(config: JavalinConfig, hostPath: String, filePath: String, location: Location) {
    info { "SPA $hostPath => $filePath $location" }
    config.spaRoot.addFile(
      hostedPath = hostPath,
      filePath = filePath,
      location = location,
    )
  }
}
//
// TODO: Since Vue-Cli production build includes versioned bundles, consider serving
// from 'immutable' path, so Javalin sets long cache expiration:
// https://javalin.io/documentation#static-files
