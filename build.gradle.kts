plugins {
  kotlin("jvm") version "2.1.20"
}

group = "net.nyhm"
version = "0.0.0"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}
kotlin {
  jvmToolchain(21)
}