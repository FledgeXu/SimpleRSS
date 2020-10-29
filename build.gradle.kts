import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version ("6.1.0")
}

group = "com.otakusaikou.simplerss"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("com.github.kittinunf.fuel:fuel:2.3.0")
    implementation("com.google.guava:guava:29.0-jre")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.ktor:ktor-client-core:1.4.1")
    implementation("com.uchuhimo:konf-toml:0.23.0")
    implementation("org.jetbrains.exposed", "exposed-core", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.24.1")
    implementation("org.xerial:sqlite-jdbc:3.32.3.2")
    implementation("commons-io:commons-io:2.6")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.slf4j:slf4j-simple:1.7.30")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
//We crete ShadowJar in here.
val shadowJar = tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set(project.name)
    manifest {
        attributes["Main-Class"] = "com.otakusaikou.simplerss.SimpleRSSKt" // fully qualified class name of default main class
    }
    with(tasks["jar"] as CopySpec)
}
tasks.getByPath("build").dependsOn(shadowJar)
