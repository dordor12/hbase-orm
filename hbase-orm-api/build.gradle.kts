plugins {
    `java-library`
    alias(libs.plugins.maven.publish)
}

group = "io.github.dordor12"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(libs.hbase.client)
    api(libs.hbase.common)
    implementation(libs.bundles.jackson)
}

mavenPublishing {
    coordinates("io.github.dordor12", "hbase-orm-api", project.version.toString())

    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("hbase-orm-api")
        description.set("HBase ORM API: annotations, codec, DAO, and mapper interfaces")
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
