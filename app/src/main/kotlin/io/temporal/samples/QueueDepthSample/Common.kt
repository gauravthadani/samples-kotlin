package io.temporal.samples.QueueDepthSample

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import com.uber.m3.tally.RootScopeBuilder
import com.uber.m3.tally.Scope
import com.uber.m3.tally.StatsReporter
import com.uber.m3.util.Duration
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.temporal.authorization.AuthorizationGrpcMetadataProvider
import io.temporal.authorization.AuthorizationTokenSupplier
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.common.converter.KotlinObjectMapperFactory.Companion.new
import io.temporal.common.reporter.MicrometerClientStatsReporter
import io.temporal.serviceclient.GrpcMetadataProvider
import io.temporal.serviceclient.SimpleSslContextBuilder
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress


fun readFileAsInputStream(fileName: String): InputStream = object {}.javaClass.getResourceAsStream("/$fileName")!!


fun serviceStubs(
    useApiKey: Boolean = false,
    withMetrics: Boolean = false,
    localClient: Boolean = false
): WorkflowServiceStubs {

//    val NS_KEY =
//        Metadata.Key.of<String?>("temporal-namespace", Metadata.ASCII_STRING_MARSHALLER)
//    val metadata = Metadata()
//    metadata.put<String?>(NS_KEY, "gaurav-test.a2dd6")
    val newServiceStubs = WorkflowServiceStubs.newServiceStubs(
        WorkflowServiceStubsOptions.newBuilder().apply {
            if (!localClient) {
                setEnableHttps(true)
                setTarget(config.endpoint)
//                setChannelInitializer{
//                        ch -> ch.intercept(MetadataUtils.newAttachHeadersInterceptor(metadata))
//                }
//                addGrpcMetadataProvider(AuthorizationGrpcMetadataProvider { config.apiKey })

                // 2) Add Authorization: Bearer <API_KEY>
//                addGrpcMetadataProvider( ()->""))
//                     new AuthorizationGrpcMetadataProvider(


                when {
                    useApiKey ->
                        addApiKey { config.apiKey }

                    else -> {
                        val clientCert = readFileAsInputStream("ca.pem")
                        val clientKey = readFileAsInputStream("ca.key")
                        setSslContext(SimpleSslContextBuilder.forPKCS8(clientCert, clientKey).build())
                    }
                }
            }
            if (withMetrics) {
                val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
                val reporter: StatsReporter = MicrometerClientStatsReporter(registry)
                val scope: Scope = RootScopeBuilder()
                    .reporter(reporter)
                    .reportEvery(Duration.ofSeconds(1.0))

                setMetricsScope(scope)
                val scrapeEndpoint: HttpServer = startPrometheusScrapeEndpoint(registry, 8079)
                Runtime.getRuntime().addShutdownHook(Thread { scrapeEndpoint.stop(1) })
            }
        }.build()
    )
    return newServiceStubs
}

fun localClient(withMetrics: Boolean = false, namespace: String? = null) = WorkflowClient.newInstance(
    serviceStubs(withMetrics = withMetrics, localClient = true),
    WorkflowClientOptions.newBuilder().apply {
        if (!namespace.isNullOrEmpty())
            setNamespace("default")
    }.build()
)

fun client(withMetrics: Boolean = false, namespace: String? = null) = WorkflowClient.newInstance(
    serviceStubs(useApiKey = true, withMetrics = withMetrics),
    WorkflowClientOptions.newBuilder().apply {
        if (!namespace.isNullOrEmpty())
            setNamespace(namespace)
    }.build()
)

fun startPrometheusScrapeEndpoint(
    registry: PrometheusMeterRegistry, port: Int
): HttpServer {
    try {
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext(
            "/metrics"
        ) { httpExchange: HttpExchange ->
            val response = registry.scrape()
            httpExchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            httpExchange.responseBody.use { os ->
                os.write(response.toByteArray())
            }
        }

        server.start()
        return server
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

data class AppConfig(val namespace: String, val endpoint: String, val apiKey: String)

val config: AppConfig by lazy {
    ConfigLoaderBuilder.default()
        .addResourceSource("/config.json")
        .build()
        .loadConfigOrThrow<AppConfig>()
}