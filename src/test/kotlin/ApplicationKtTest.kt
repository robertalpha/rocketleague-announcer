import integrationTests.AbstractMessagingTest
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import nl.vanalphenict.module

class ApplicationTest : AbstractMessagingTest() {
    @Test
    fun testRoot() = testApplication {
        application {
            module(mosquitto.getMappedPort(1883))
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}