import java.time.Duration

plugins {
    java
    alias(libs.plugins.jmh)
}

group = "io.github.dordor12"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Integration test source set
sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    create("perfTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val intTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val intTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}
val perfTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val perfTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

dependencies {
    implementation(project(":hbase-orm-api"))
    implementation(libs.hbase.client)
    implementation(libs.hbase.common)

    // Annotation processor wiring
    annotationProcessor(project(":hbase-orm-processor"))
    annotationProcessor(project(":hbase-orm-api"))

    // Unit test dependencies
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)

    // Integration test dependencies
    intTestImplementation(platform(libs.junit.bom))
    intTestImplementation(libs.junit.jupiter)
    intTestImplementation(libs.bundles.testcontainers.junit)
    intTestRuntimeOnly(libs.junit.platform)

    // Performance test dependencies
    perfTestImplementation(platform(libs.junit.bom))
    perfTestImplementation(libs.junit.jupiter)
    perfTestImplementation(libs.bundles.testcontainers.junit)
    perfTestRuntimeOnly(libs.junit.platform)
}

// JMH configuration
jmh {
    fork = 1
    warmupIterations = 2
    iterations = 3
    resultFormat = "JSON"
    resultsFile = project.file("build/reports/jmh/results.json")
}

// Unit tests: exclude integration and perf tags
tasks.test {
    useJUnitPlatform {
        excludeTags("integration", "perf")
    }
}

// Integration test task
val intTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter(tasks.test)
    useJUnitPlatform {
        includeTags("integration")
    }
    systemProperty("project.dir", project.projectDir.absolutePath)
}

// Performance test task
val perfTest by tasks.registering(Test::class) {
    description = "Runs performance tests against HBase Docker."
    group = "verification"
    testClassesDirs = sourceSets["perfTest"].output.classesDirs
    classpath = sourceSets["perfTest"].runtimeClasspath
    shouldRunAfter(tasks.test)
    useJUnitPlatform {
        includeTags("perf")
    }
    systemProperty("project.dir", project.projectDir.absolutePath)
    timeout = Duration.ofMinutes(10)
}
