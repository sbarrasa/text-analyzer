plugins {
   kotlin("jvm") version "2.2.0"
}

group = "com.sbarrasa"
version = "1.0-SNAPSHOT"

repositories {
   mavenCentral()
}

dependencies {

   implementation("org.apache.opennlp:opennlp-tools:2.3.3")
   implementation("org.apache.commons:commons-text:1.11.0")
   implementation("org.apache.commons:commons-math3:3.6.1")


   // Logging - Kotlin logging (wraps SLF4J with Kotlin-friendly API)
   implementation("io.github.microutils:kotlin-logging:3.0.5")
   implementation("ch.qos.logback:logback-classic:1.4.14")
   
   testImplementation(kotlin("test"))
   implementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
   jvmToolchain(24)
}