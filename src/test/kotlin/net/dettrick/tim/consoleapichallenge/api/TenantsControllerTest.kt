package net.dettrick.tim.consoleapichallenge.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.util.UUID
import org.springframework.context.ApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.EntityExchangeResult


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
class TenantsControllerTest {
	
    @Autowired
    private lateinit var context: ApplicationContext
	
    @Autowired
    private lateinit var json: JacksonTester<Tenant>
	
	@Test
	fun testTenantSerialize() {
		val tenant = Tenant(UUID.randomUUID(), "Tom Atkins", 300.0, LocalDate.parse("2017-02-07"), 25.0)
		val output = json.write(tenant)
		assertThat(output).hasJsonPathStringValue("@.id")
		assertThat(output).hasJsonPathStringValue("@.name")
		assertThat(output).hasJsonPathNumberValue("@.weeklyRentAmount")
		assertThat(output).hasJsonPathStringValue("@.paidToDate")
		assertThat(output).hasJsonPathNumberValue("@.credit")
	}
	
	@Test
	fun createTenant() {
		val client = WebTestClient.bindToApplicationContext(context).build()
		val newTenant = NewTenant("Tom Atkins", 300.0)
		val result: EntityExchangeResult<Tenant> = client.post().uri("/tenants/")
            .accept(MediaType.APPLICATION_JSON_UTF8)
			.body(Mono.just(newTenant), NewTenant::class.java)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(Tenant::class.java)
			.returnResult()
		val tenant: Tenant = result.responseBody
		assertThat(tenant.id).isNotNull()
		assertThat(tenant.name).isEqualTo("Tom Atkins")
		assertThat(tenant.weeklyRentAmount).isEqualTo(300.0)
		assertThat(tenant.paidToDate).isEqualTo(LocalDate.now())
		assertThat(tenant.credit).isEqualTo(0.0)
		assertThat(
			client.get().uri(result.responseHeaders?.location!!)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Tenant::class.java)
				.returnResult().responseBody
		).isEqualTo(tenant)	
	}

}