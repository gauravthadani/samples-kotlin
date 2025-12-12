package io.temporal.samples.ops

import io.grpc.CallCredentials
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.Status
import io.temporal.api.cloud.cloudservice.v1.CloudServiceGrpc
import io.temporal.api.cloud.cloudservice.v1.CreateNamespaceRequest
import io.temporal.api.cloud.namespace.v1.ApiKeyAuthSpec
import io.temporal.api.cloud.namespace.v1.NamespaceSpec
import java.util.concurrent.Executor

fun main(args: Array<String>) {
    val cloudEndpoint = "saas-api.tmprl.cloud:443"
    val apiKey = System.getenv("TEMPORAL_CLOUD_API_KEY")
        ?: throw Exception("Temporal Cloud API key not defined")

    val channel = ManagedChannelBuilder.forTarget(cloudEndpoint)
        .useTransportSecurity()
        .build()

    val client = channel.let {
        CloudServiceGrpc.newBlockingStub(it)
            .withCallCredentials(credentials(apiKey))
    }

    val response = client.createNamespace(
        CreateNamespaceRequest.newBuilder()
            .setSpec(
                NamespaceSpec.newBuilder()
                    .setName("my-sample-namespace-gt")
                    .addRegions("aws-us-east-1")
                    .setApiKeyAuth(ApiKeyAuthSpec.newBuilder().build())
                    .setRetentionDays(10)
                    .build()
            )
            .build()
    )
    println("Cloud API client initialized successfully")
    println("Connected to: $cloudEndpoint")
    println(response)
    channel.shutdown()
}

private fun credentials(apiKey: String): CallCredentials = object : CallCredentials() {
    override fun applyRequestMetadata(
        requestInfo: RequestInfo,
        executor: Executor,
        applier: MetadataApplier
    ) {
        executor.execute {
            try {
                Metadata().apply {
                    val authKey = Metadata.Key.of<String>("Authorization", Metadata.ASCII_STRING_MARSHALLER)
                    put(authKey, "Bearer $apiKey")
                    val apiVersion = Metadata.Key.of("temporal-cloud-api-version", Metadata.ASCII_STRING_MARSHALLER)
                    put(apiVersion, "2024-05-13-00")
                }.also {
                    applier.apply(it)
                }
            } catch (e: Exception) {
                applier.fail(Status.UNAUTHENTICATED.withCause(e))
            }
        }
    }
}

