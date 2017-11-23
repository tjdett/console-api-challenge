package net.dettrick.tim.consoleapichallenge.data

import java.time.Instant
import java.time.temporal.TemporalAmount

typealias Dollars = Double

data class AccountState(val paidTo: Instant, val credit: Dollars)

fun rentPayer(period: TemporalAmount, rent: Dollars): ((AccountState, Dollars) -> AccountState) {
	
	tailrec fun payRent(account: AccountState): AccountState {
		if (account.credit < rent) return account
		else return payRent(AccountState(account.paidTo.plus(period), account.credit - rent))
	}
	
	return fun (account: AccountState, payment: Dollars): AccountState {
		return payRent(account.copy(credit = account.credit + payment))
	}
}