# Ballista

Helps launch a [Javalin](https://javalin.io/) server - get it?

Augments the excellent Javalin web framework with:
- **Modules** to encapsulate configuration, logic, APIs, etc
- **Dependency injection** to promote decoupled design
- **Request handling** processor leveraging dependency injection to reduce boilerplate and ease API implementation

## Tech

- [Kotlin](https://kotlinlang.org/) language
- [Javalin](https://javalin.io/) web/api framework
- [pick](https://github.com/nebaughman/pick) dependency injection
- [slf4j](https://www.slf4j.org/) logging
- [Exposed](https://www.jetbrains.com/exposed/) database ORM/DAO/DSL
- [PostgreSQL](https://www.postgresql.org/) & [sqlite](https://www.sqlite.org/) database drivers

## History

Ballista is an extraction and refinement of [Katapult](https://github.com/nebaughman/Katapult), an experimental kitchen sink project. Ballista includes only the Javalin framework augmentation portion of Katapult.

## Disclaimer

Ballista is an experimental/learning project. It has been used for a few small web api projects, but it's not especially well tested or optimally performant. Use at your own risk!

## Development

Work is done in `develop` branch. Merging to `master` implies a release, which must be tagged, and may trigger CI/CD.

**Release process:**
- Merge any feature branches into `develop`
- Update `version` in `build.gradle.kt` to `X.Y.Z`
- `git checkout master && git merge develop && git tag X.Y.Z` # no 'v'
- `git push --all && git push --tags`
- `git checkout develop` # back to work

# Wish List

- The code is currently a rough copy from another project and needs unit tests
- Modules (and their underlying dependencies) are all part of this project, and it would be best to separate them into optional bundles
- Build process does not yet bundle this into a library jar
- Distribution is not published anywhere (eg, Maven Central)

## License

[MIT License](LICENSE.txt) &copy; Nathaniel Baughman
