package net.dettrick.tim.consoleapichallenge.data

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Duration
import java.time.Instant

@RunWith(JUnit4::class)
class AccountStateTest {
	private fun payRent(account: AccountState): AccountState {
		return account.payRent(Duration.ofDays(1), 10.0)
	}
	
	@Test
	fun noChangePayment() {
		val account = AccountState(Instant.EPOCH, 7.0)
		assert.that(payRent(account), equalTo(account))
	}
	
	@Test
	fun singlePeriodPayment() {
		assert.that(
				payRent(AccountState(Instant.EPOCH, 11.0)),
				equalTo(AccountState(Instant.EPOCH.plus(Duration.ofDays(1)), 1.0)))
	}
	
	
	@Test
	fun multiplePeriodPayment() {
		assert.that(
				payRent(AccountState(Instant.EPOCH, 25.0)),
				equalTo(AccountState(Instant.EPOCH.plus(Duration.ofDays(2)), 5.0)))
	}
}