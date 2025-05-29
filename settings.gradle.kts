import java.net.URI

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ballista"

// https://blog.gradle.org/introducing-source-dependencies
sourceControl {
  gitRepository(URI("https://github.com/nebaughman/pick.git")) {
    producesModule("net.nyhm:pick")
  }
}