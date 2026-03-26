plugins {
    `java-library`
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

group = "io.github.dordor12"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(project(":hbase-orm-api"))
    implementation(libs.javapoet)
    implementation(libs.hbase.common)
}

// Disable annotation processing on the processor itself
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-proc:none")
}

mavenPublishing {
    coordinates("io.github.dordor12", "hbase-orm-processor", project.version.toString())

    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("hbase-orm-processor")
        description.set("HBase ORM annotation processor: compile-time mapper code generation")
        url.set("https://github.com/dordor12/hbase-orm")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        developers {
            developer {
                id.set("dordor12")
                name.set("Dor Amid")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/dordor12/hbase-orm.git")
            developerConnection.set("scm:git:ssh://github.com:dordor12/hbase-orm.git")
            url.set("https://github.com/dordor12/hbase-orm")
        }
    }
}
