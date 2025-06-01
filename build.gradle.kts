plugins {
  kotlin("jvm") version "2.1.20"
}

group = "net.nyhm"
version = "0.0.2"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("io.javalin:javalin:6.6.0")
  implementation("org.slf4j:slf4j-simple:2.0.1")

  implementation("org.jetbrains.exposed:exposed-core:0.39.2")
  implementation("org.jetbrains.exposed:exposed-dao:0.39.2")
  implementation("org.jetbrains.exposed:exposed-jdbc:0.39.2")
  implementation("org.jetbrains.exposed:exposed-java-time:0.39.2")
  //implementation("org.jetbrains.exposed:exposed-jodatime:0.39.2")

  implementation("org.xerial:sqlite-jdbc:3.41.2.2")
  implementation("org.postgresql:postgresql:42.7.2")

  implementation("net.nyhm:pick:0.0.2") // see settings.gradle.kts (github repo)

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}
kotlin {
  jvmToolchain(21)
}