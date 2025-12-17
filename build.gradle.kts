plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        // Common Kotlin dependencies
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // Common test dependencies
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
        "testImplementation"("io.temporal:temporal-testing:1.32.1")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")

        // Temporal
        "implementation"("io.temporal:temporal-sdk:1.32.1")
        "implementation"("io.temporal:temporal-kotlin:1.32.1")

        // Jackson
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")

        // Configuration
        "implementation"("com.sksamuel.hoplite:hoplite-core:2.9.0")
        "implementation"("com.sksamuel.hoplite:hoplite-json:2.9.0")

        // CLI
        "implementation"("com.github.ajalt.clikt:clikt:5.0.1")

        // Monitoring
        "implementation"("io.micrometer:micrometer-registry-prometheus")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
