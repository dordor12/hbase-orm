plugins {
    java
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
}

val intTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val intTestRuntimeOnly by configurations.getting {
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
}

// Unit tests: exclude integration tag
tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
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
