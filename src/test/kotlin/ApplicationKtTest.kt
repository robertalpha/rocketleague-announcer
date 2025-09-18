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
            val mappedPort = mosquitto.getMappedPort(1883)

            //FIXME put val broker = "tcp://127.0.0.1:mappedPort" in system env

            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}