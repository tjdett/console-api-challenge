package net.dettrick.tim.consoleapichallenge.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.json.JacksonTester
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.UUID
import net.dettrick.tim.consoleapichallenge.data.RentReceipt as DataRentReceipt
import net.dettrick.tim.consoleapichallenge.data.Tenant as DataTenant
import javax.transaction.Transactional
import java.time.Instant
import java.time.Duration


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestEntityManager
class TenantsControllerTest {
	
    @Autowired
    private lateinit var context: ApplicationContext
	
    @Autowired
    private lateinit var entityManager: TestEntityManager
	
    @Autowired
    private lateinit var objectMapper: ObjectMapper
	
    private lateinit var json: JacksonTester<Tenant>
	
	@Test
	fun testTenantSerialize() {
		JacksonTester.initFields(this, objectMapper)
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
	
	@Test
	@Transactional
	fun listTenantsWithRentReceipt() {
		val client = WebTestClient.bindToApplicationContext(context).build()
		val now = Instant.now()
		val tenants = arrayOf(
				DataTenant(null, "Amelia", 300.00),
				DataTenant(null, "Beatrix", 250.00),
				DataTenant(null, "Clara", 320.00),
				DataTenant(null, "David", 320.00),
				DataTenant(null, "Elton", 320.00),
				DataTenant(null, "Frederick", 320.00)
		).map { entityManager.persistAndFlush(it) }
		arrayOf(
				DataRentReceipt(null, tenants[3], 100.0, now.minus(Duration.ofMinutes(10))),
				DataRentReceipt(null, tenants[5], 100.0, now.minus(Duration.ofMinutes(40))),
				DataRentReceipt(null, tenants[2], 100.0, now.minus(Duration.ofMinutes(90))),
				DataRentReceipt(null, tenants[0], 100.0, now.minus(Duration.ofMinutes(130))),
				DataRentReceipt(null, tenants[1], 100.0, now.minus(Duration.ofMinutes(140))),
				DataRentReceipt(null, tenants[5], 100.0, now.minus(Duration.ofMinutes(210))),
				DataRentReceipt(null, tenants[0], 100.0, now.minus(Duration.ofMinutes(340))),
				DataRentReceipt(null, tenants[3], 100.0, now.minus(Duration.ofMinutes(410)))
		).forEach { entityManager.persistAndFlush(it) }
		assertThat(
			client.get().uri("/tenants/with-rent-receipt?withinLast=PT2H")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Tenant::class.java)
				.returnResult()
				.responseBody
				.map { it.name }
				.toSet()
		).isEqualTo(setOf("David", "Frederick", "Clara"))
		
	}

}