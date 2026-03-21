plugins {
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.release)
}

group = "io.github.dordor12"

subprojects {
    repositories {
        mavenCentral()
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(providers.environmentVariable("OSSRH_USERNAME"))
            password.set(providers.environmentVariable("OSSRH_PASSWORD"))
        }
    }
}

// Ensure signing tasks run after publish tasks are configured
subprojects {
    tasks.withType<Sign>().configureEach {
        dependsOn(tasks.withType<PublishToMavenRepository>())
    }
}
