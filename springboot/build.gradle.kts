plugins {
    application
    id("com.google.protobuf") version "0.9.4"
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.spring") version "1.9.22"
}

dependencies {
    // Spring Boot specific dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Module specific dependencies
    implementation(libs.guava)
    implementation(libs.junit.jupiter.engine)

    // Protobuf dependencies
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("com.google.protobuf:protobuf-java-util:3.25.1")

    // gRPC dependencies
    implementation("io.grpc:grpc-protobuf:1.60.0")
    implementation("io.grpc:grpc-stub:1.60.0")
    implementation("io.grpc:grpc-netty-shaded:1.60.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.api.grpc:grpc-google-common-protos:2.29.0")

    // temporal dependencies
    implementation("io.temporal:temporal-spring-boot-starter:1.32.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    mainClass = "io.temporal.samples.springboot.SpringBootAppKt"
}
