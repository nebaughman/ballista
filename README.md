# Ballista

Helps launch a [Javalin](https://javalin.io/) server - get it?

Augments the excellent Javalin web framework with:
- **Modules** to encapsulate configuration, logic, APIs, etc
- **Dependency injection** to promote decoupled design
- **Request processor** leveraging dependency injection to reduce boilerplate and ease API implementation

> **Important:** Ballista might not be suitable for production purposes. The primary motivation is to experiment and learn a thing or two.

## Tech

- [Kotlin](https://kotlinlang.org/) language
- [Gradle](https://gradle.org/) build system
- [Javalin](https://javalin.io/) web/api framework
- [Pick](https://github.com/nebaughman/pick) dependency injection
- [SLF4J](https://www.slf4j.org/) logging
- [Exposed](https://www.jetbrains.com/exposed/) database ORM/DAO/DSL
- [PostgreSQL](https://www.postgresql.org/) & [SQLite](https://www.sqlite.org/) database drivers

## History

Ballista is an extraction and refinement of [Katapult](https://github.com/nebaughman/Katapult), an experimental full-stack (kitchen sink) project. Ballista includes only the Javalin web framework augmentation portion of Katapult.

## Usage

The following is a rough (pseudocode) guide to give the gist of using Ballista with dependency injection request handling.

```kt
class InfoApi: BallistaModule {
  override fun config(config: JavalinConfig) {
    config.router.apiBuilder {
      get("/info/{id}") { it.process(::handleInfo) }
    }
  }

  /**
   * Ballista Context processor will provide the request Context, consume the request body 
   * and deserialize into requested @Body type, and inject other configured dependencies
   * (InfoService in this case). Return type is serialized into json response body.
   */
  fun handleInfo(ctx: Context, @Body request: InfoRequest, infoService: InfoService): Info {
    val id = ctx.pathParam("id") // TODO: @Param
    if (!validId(id)) throw BadRequestResponse()
    return infoService.lookup(id) ?: throw NotFoundResponse()
  }
}

/**
 * Very rudimentary incomplete example of configuring dependency injection and launching Ballista
 */
class YourApp {
  fun main() {
    DiBuilder()
      .also { di ->
        when (dbConf) { // config parsed from command-line, etc
          is SqliteConfig -> di.registerSingleton(DbDriver::class) { SqliteDriver(dbConf) }
          is PostgresConfig -> di.registerSingleton(DbDriver::class) { PostgresDriver(dbConf) }
        }
      }
      .registerSingleton { ExposedDb(it.get()) } // depends on driver
      .registerSingleton { InfoDao(it.get()) } // depends on db
      .registerSingleton { InfoService(it.get()) } // depends on InfoDao
      .registerSingleton { InfoApi() } // InfoService injected upon http request processing
      .registerSingleton { HttpModule(it.get()) } // for example
      .registerSingleton(Processor::class) { DiProcessor(it) } // injection-based request processor
      .registerSingleton { Ballista(it.getAll(), it.get()) } // Ballista service
      .get(Ballista::class).start() // dependencies injected
  }
}
```

## Development

**Versioning:** 
- Intention is to follow [semver](https://semver.org/)
- ... but anything goes for the initial 0.X.X releases

**Branch strategy:**
- Work is done in `develop` branch
- Feature development may branch from (and back into) `develop`
- Merging to `master` implies a release, which must be tagged, and may trigger CI/CD

**Release process:**
- Merge any ready feature branches into `develop`
- Update `version` in `build.gradle.kt` to `X.Y.Z`
- `git commit -am "vX.Y.Z"`
- `git checkout master && git merge develop && git tag X.Y.Z` (no `v`)
- `git push --all && git push --tags`
- `git checkout develop` # back to work

## TODO

- Clean up initial (rough) copy from external project
- Documentation and example usage
- Unit tests
- Bundle into a library jar
- Publish library (eg, Maven Central)
- Separate modules (with their underlying dependencies) into optional bundles
- Remove Log class from this library

## License

[MIT License](LICENSE.txt) &copy; Nathaniel Baughman
