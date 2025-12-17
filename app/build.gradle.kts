plugins {
    application
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    // Module specific dependencies
    implementation(libs.guava)
    implementation(libs.junit.jupiter.engine)

    // Logging (specific to app module)
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("org.slf4j:slf4j-api:1.7.32")

    // Protobuf dependencies
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("com.google.protobuf:protobuf-java-util:3.25.1")

    // gRPC dependencies
    implementation("io.grpc:grpc-protobuf:1.60.0")
    implementation("io.grpc:grpc-stub:1.60.0")
    implementation("io.grpc:grpc-netty-shaded:1.60.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.api.grpc:grpc-google-common-protos:2.29.0")
}

application {
    mainClass = "com.example.cert_rotation.AppKt"
}

// Configure protobuf plugin
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

// Configure source sets to include proto files from cloud-api directory
sourceSets {
    main {
        proto {
            srcDir("src/main/kotlin/io/temporal/samples/ops/api")
            srcDir("src/main/kotlin/io/temporal/samples/ops/cloud-api")
        }
    }
}
