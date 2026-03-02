import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.11.0"
    id("org.sonarqube") version "7.2.2.6593"
    jacoco
    id("net.ltgt.errorprone") version "5.1.0"
    id("com.diffplug.spotless") version "8.2.1"
}

group = "no.hvl.tk"
version = "2.3.8"

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
        intellijIdea("2025.2")
        bundledPlugin("com.intellij.java")
    }

    // https://mvnrepository.com/artifact/net.sourceforge.plantuml/plantuml
    implementation("net.sourceforge.plantuml:plantuml:1.2026.1")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.20.0")

    // Websocket server and server container dependencies
    // https://mvnrepository.com/artifact/jakarta.websocket/jakarta.websocket-api
    compileOnly("jakarta.websocket:jakarta.websocket-api:2.2.0")
    // https://mvnrepository.com/artifact/org.glassfish.tyrus/tyrus-server
    implementation("org.glassfish.tyrus:tyrus-server:2.2.2")
    // https://mvnrepository.com/artifact/org.glassfish.tyrus/tyrus-container-grizzly-server
    implementation("org.glassfish.tyrus:tyrus-container-grizzly-server:2.2.2")

    testImplementation("junit:junit:4.13.2") // Needed for test execution in IntelliJ Platform
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.3")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("org.mockito:mockito-core:5.22.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")

    // https://mvnrepository.com/artifact/com.google.errorprone/error_prone_core
    errorprone("com.google.errorprone:error_prone_core:2.48.0")
}

intellijPlatform {
    pluginConfiguration {
        changeNotes.set("Update internal dependencies")
        ideaVersion {
            untilBuild.set("253.*")
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
