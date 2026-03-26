plugins {
    alias(libs.plugins.release)
    alias(libs.plugins.dokka)
}

group = "io.github.dordor12"

repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenCentral()
    }
}
