package io.temporal.samples.hello

import io.temporal.samples.springboot.SpringBootApp
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import kotlin.test.assertEquals

@SpringBootTest(
    classes = [SpringBootApp::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
)
class HelloControllerTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `test POST hello endpoint`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity("Thadani", headers)

        val response = restTemplate.postForEntity(
            "/api/hello",
            request,
            String::class.java
        )

        println("Response: ${response.body}")
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
