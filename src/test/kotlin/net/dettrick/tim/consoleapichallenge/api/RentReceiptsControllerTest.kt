package net.dettrick.tim.consoleapichallenge.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import javax.transaction.Transactional
import net.dettrick.tim.consoleapichallenge.data.RentReceipt as DataRentReceipt
import net.dettrick.tim.consoleapichallenge.data.Tenant as DataTenant

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestEntityManager
class RentReceiptsControllerTest {
	
    @Autowired
    private lateinit var entityManager: TestEntityManager
	
    @Autowired
    private lateinit var context: ApplicationContext
	
	@Test
	@Transactional
	fun createRentReceipt() {
		val client = WebTestClient.bindToApplicationContext(context).build()
		val tenant: DataTenant = entityManager.persistAndFlush(DataTenant(null, "Tom Atkins", 300.0))
		val result: EntityExchangeResult<RentReceipt> = client.post().uri("/tenants/${tenant.id}/receipts/")
            .accept(MediaType.APPLICATION_JSON_UTF8)
			.body(Mono.just(NewRentReceipt(400.0)), NewRentReceipt::class.java)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(RentReceipt::class.java)
			.returnResult()
		val receipt: RentReceipt = result.responseBody
		assertThat(receipt.id).isNotNull()
		assertThat(receipt.amount).isEqualTo(400.0)
		assertThat(
			client.get().uri(result.responseHeaders?.location!!)
				.exchange()
				.expectStatus().isOk()
				.expectBody(RentReceipt::class.java)
				.returnResult().responseBody
		).isEqualTo(receipt)	
	}
	
	@Test
	@Transactional
	fun listRentReceipts() {
		val client = WebTestClient.bindToApplicationContext(context).build()
		val tenant: DataTenant = entityManager.persistAndFlush(DataTenant(null, "Tom Atkins", 300.0))
		val rentReceipts =
				arrayOf(
					DataRentReceipt(null, tenant, 200.0),
					DataRentReceipt(null, tenant, 250.0)
				).map { entityManager.persistAndFlush(it) }
		assertThat(
			client.get().uri("/tenants/${tenant.id}/receipts/")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(RentReceipt::class.java)
				.returnResult()
				.responseBody
				.map { it.id }
				.toSet()
		).isEqualTo(rentReceipts.map { it.id }.toSet())
	}

}