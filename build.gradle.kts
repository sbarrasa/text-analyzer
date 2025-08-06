plugins {
   kotlin("jvm") version "2.2.0"
}

group = "com.sbarrasa"
version = "1.0-SNAPSHOT"

repositories {
   mavenCentral()
}

dependencies {
   testImplementation(kotlin("test"))
   implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-M2.1")
   implementation("org.nd4j:nd4j-native-platform:1.0.0-M2.1")
}

tasks.test {
   useJUnitPlatform()
}
kotlin {
   jvmToolchain(24)
}