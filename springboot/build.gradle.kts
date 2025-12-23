plugins {
    application
    id("com.google.protobuf") version "0.9.4"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.2.21"
}

dependencies {
    // Spring Boot specific dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Module specific dependencies
    implementation(libs.guava)

    // Protobuf dependencies
    implementation("com.google.protobuf:protobuf-java:3.25.5")
    implementation("com.google.protobuf:protobuf-java-util:3.25.1")

    // gRPC dependencies
    implementation("io.grpc:grpc-protobuf:1.60.0")
    implementation("io.grpc:grpc-stub:1.60.0")
    implementation("io.grpc:grpc-netty-shaded:1.60.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.api.grpc:grpc-google-common-protos:2.29.0")

    // temporal dependencies
    implementation("io.temporal:temporal-spring-boot-starter:${project.property("temporalVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.temporal:temporal-testing:${project.property("temporalVersion")}")
}

application {
    mainClass = "io.temporal.samples.springboot.SpringBootAppKt"
}
