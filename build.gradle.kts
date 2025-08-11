plugins {
   kotlin("jvm") version "2.2.0"
}

group = "com.sbarrasa"
version = "1.0-SNAPSHOT"

repositories {
   mavenCentral()
}

dependencies {
   
   // Apache Lucene for text analysis
   implementation("org.apache.lucene:lucene-core:9.8.0")
   implementation("org.apache.lucene:lucene-analysis-common:9.8.0")
   implementation("org.apache.lucene:lucene-queries:9.8.0")
   
   // Logging - Kotlin logging (wraps SLF4J with Kotlin-friendly API)
   implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
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