package net.dettrick.tim.consoleapichallenge.data

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit4.SpringRunner
import java.time.Duration
import java.time.Instant
import org.springframework.data.domain.PageRequest

@RunWith(SpringRunner::class)
@DataJpaTest
class TenantRepositoryTest {
	
    @Autowired
    lateinit var entityManager: TestEntityManager
	
	@Autowired
	lateinit var repository: TenantRepository

	@Test
	fun emptyRepository() {
		assert.that(repository.findAll(), Matcher(List<Tenant>::isEmpty))
	}
	
	@Test
	fun paidToToday() {
		val tenant = Tenant(null, "Tom Atkins", 300.00)
		repository.save(tenant)
		assert.that(tenant.account.paidTo, equalTo(tenant.epoch))
		assert.that(tenant.account.credit, equalTo(0.0))
		entityManager.persist(RentReceipt(null, tenant, 350.00))
		entityManager.persist(RentReceipt(null, tenant, 450.00))
		entityManager.flush()
		entityManager.clear()
		val updatedTenant = repository.findById(tenant.id).get()
		println(updatedTenant)
		assert.that(updatedTenant.account.paidTo, equalTo(updatedTenant.epoch.plus(Duration.ofDays(14))))
		assert.that(updatedTenant.account.credit, equalTo(200.0))
	}
	
	@Test
	fun recentPayers() {
		val now = Instant.now()
		val tenants = arrayOf(
				Tenant(null, "Amelia", 300.00),
				Tenant(null, "Beatrix", 250.00),
				Tenant(null, "Clara", 320.00),
				Tenant(null, "David", 320.00),
				Tenant(null, "Elton", 320.00),
				Tenant(null, "Frederick", 320.00)
		).map { repository.save(it) }
		arrayOf(
				RentReceipt(null, tenants[3], 100.0, now.minusSeconds(10)),
				RentReceipt(null, tenants[5], 100.0, now.minusSeconds(11)),
				RentReceipt(null, tenants[2], 100.0, now.minusSeconds(12)),
				RentReceipt(null, tenants[0], 100.0, now.minusSeconds(13)),
				RentReceipt(null, tenants[1], 100.0, now.minusSeconds(14)),
				RentReceipt(null, tenants[5], 100.0, now.minusSeconds(15)),
				RentReceipt(null, tenants[0], 100.0, now.minusSeconds(16)),
				RentReceipt(null, tenants[3], 100.0, now.minusSeconds(17))
		).forEach { entityManager.persistAndFlush(it) }
		assert.that(
				repository.findAllWithRentReceiptSince(now.minusSeconds(12)).map { it.name }.toSet(),
				equalTo(setOf("David", "Frederick", "Clara"))
		)
	}

}
