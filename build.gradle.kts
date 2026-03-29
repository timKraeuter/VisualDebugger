import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.13.1"
    id("org.sonarqube") version "7.2.2.6593"
    jacoco
    id("net.ltgt.errorprone") version "5.1.0"
    id("com.diffplug.spotless") version "8.2.1"
}

group = "no.hvl.tk"
version = "2.4.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.1")
        bundledPlugin("com.intellij.java")
    }

    implementation(libs.plantuml)
    implementation(libs.commons.lang3)
    implementation(libs.tyrus.server)
    implementation(libs.tyrus.container.grizzly.server)

    compileOnly(libs.jakarta.websocket.api)

    testImplementation(libs.junit4) // Needed for test execution in IntelliJ Platform
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.core)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)

    errorprone(libs.errorprone.core)
}

intellijPlatform {
    pluginConfiguration {
        changeNotes.set("Update internal dependencies")
        ideaVersion {
            untilBuild.set("261.*")
        }
    }
}

tasks.test {
    useJUnitPlatform()
    extensions.configure<JacocoTaskExtension> {
        includes = mutableListOf()
        isIncludeNoLocationClasses = true
        excludes = mutableListOf("jdk.internal.*")
    }
}

tasks.runIde {
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
}

tasks.named("sonar") {
    dependsOn(tasks.jacocoTestReport)
}

spotless {
    java {
        googleJavaFormat()
        formatAnnotations()
    }
}

tasks.jacocoTestReport {
    classDirectories.setFrom(tasks.instrumentCode)
    reports {
        xml.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(tasks.instrumentCode)
}

sonar {
    properties {
        property("sonar.projectKey", "timKraeuter_VisualDebugger")
        property("sonar.organization", "timkraeuter")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.compileTestJava {
    // Ignore manual tests.
    options.errorprone.excludedPaths.set(".*/manueltests/.*")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-XDaddTypeAnnotationsToSymbol=true")
}
