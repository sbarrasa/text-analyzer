plugins {
   kotlin("jvm") version "2.2.0"
}

group = "com.sbarrasa"
version = "1.0-SNAPSHOT"

repositories {
   mavenCentral()
}

dependencies {
   implementation("org.apache.commons:commons-math3:3.6.1")
   implementation("org.apache.lucene:lucene-core:9.9.1")
   implementation("org.apache.lucene:lucene-analysis-common:9.9.1")
   implementation("org.apache.lucene:lucene-queryparser:9.9.1")
   implementation("org.apache.lucene:lucene-analyzers-common:8.11.2")
   
   // Smile (Statistical Machine Intelligence and Learning Engine)
   implementation("com.github.haifengl:smile-core:3.0.2")
   implementation("com.github.haifengl:smile-nlp:3.0.2")
   
   testImplementation(kotlin("test"))
   testImplementation("io.kotest:kotest-runner-junit5:5.9.0")
   testImplementation("io.kotest:kotest-assertions-core:5.9.0")
}

   tasks.test {
   useJUnitPlatform()
}
kotlin {
   jvmToolchain(24)
}