package net.nyhm.ballista.mod

import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import net.nyhm.ballista.BallistaModule
import java.io.File

/**
 * Module designed to serve certbot created `.well-known/acme-challenge` files.
 * If the given data dir is `/path/to/data`, then tell certbot to use
 * `--webroot-path /path/to/data/certbot`.
 */
class CertbotModule(private val spec: CertbotSpec): BallistaModule {
  override fun config(config: JavalinConfig) {
    val path = File(spec.dataDir, "certbot")
    path.mkdirs()
    config.staticFiles.add(path.absolutePath, Location.EXTERNAL)
  }
}

// TODO: Should modules take the _dataDir_ or a specific path?
//  (Probably the latter; see ImagesSpec, which takes an _uploadPath_ rather than composing it)
//
data class CertbotSpec(
  val dataDir: File
)