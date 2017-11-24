package net.dettrick.tim.consoleapichallenge.api

import java.time.LocalDate
import java.util.UUID

data class Tenant(
		val id: UUID,
		val name: String,
		val weeklyRentAmount: Double,
		val paidToDate: LocalDate,
		val credit: Double)

data class NewTenant(
		val name: String,
		val weeklyRentAmount: Double)