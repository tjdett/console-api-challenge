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
		val updatedTenant = entityManager.refresh(tenant)
		assert.that(updatedTenant.account.paidTo, equalTo(updatedTenant.epoch.plus(Duration.ofDays(14))))
		assert.that(updatedTenant.account.credit, equalTo(200.0))
		
	}

}
