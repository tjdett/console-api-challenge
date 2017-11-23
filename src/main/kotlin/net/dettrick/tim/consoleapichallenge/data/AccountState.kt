package net.dettrick.tim.consoleapichallenge.data

import java.time.Instant
import java.time.temporal.TemporalAmount

data class AccountState(val paidTo: Instant, val credit: Dollars) {
	fun payRent(period: TemporalAmount, rent: Dollars): AccountState {
		tailrec fun recursePayRent(account: AccountState): AccountState {
			if (account.credit < rent) return account
			else return recursePayRent(AccountState(account.paidTo.plus(period), account.credit - rent))
		}
		return recursePayRent(this)
	}
}