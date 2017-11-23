package net.dettrick.tim.consoleapichallenge.data

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Duration
import java.time.Instant

@RunWith(JUnit4::class)
class RentPayerTest {
	val f = rentPayer(Duration.ofDays(1), 10.0)
	
	@Test
	fun noChangePayment() {
		assert.that(f(AccountState(Instant.EPOCH, 2.0), 5.0), equalTo(AccountState(Instant.EPOCH, 7.0)))
	}
	
	@Test
	fun singlePeriodPayment() {
		assert.that(
				f(AccountState(Instant.EPOCH, 2.0), 9.0),
				equalTo(AccountState(Instant.EPOCH.plus(Duration.ofDays(1)), 1.0)))
	}
	
	
	@Test
	fun multiplePeriodPayment() {
		assert.that(
				f(AccountState(Instant.EPOCH, 2.0), 23.0),
				equalTo(AccountState(Instant.EPOCH.plus(Duration.ofDays(2)), 5.0)))
	}
}