plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.dordor12"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(libs.hbase.client)
    api(libs.hbase.common)
    implementation(libs.bundles.jackson)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
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
    }
}

signing {
    val signingKey = providers.environmentVariable("GPG_SIGNING_KEY")
    val signingPassphrase = providers.environmentVariable("GPG_SIGNING_PASSPHRASE")
    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        sign(publishing.publications["mavenJava"])
    }
}

