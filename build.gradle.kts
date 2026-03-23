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

// Break circular dependency: publish depends on sign, so sign must not dependOn publish.
// Use mustRunAfter to ensure correct ordering without creating a cycle.
subprojects {
    tasks.withType<PublishToMavenRepository>().configureEach {
        mustRunAfter(tasks.withType<Sign>())
    }
}
