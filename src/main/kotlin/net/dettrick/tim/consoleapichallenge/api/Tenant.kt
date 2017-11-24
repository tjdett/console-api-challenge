package net.dettrick.tim.consoleapichallenge.api

import java.util.UUID
import java.time.LocalDate
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer

data class Tenant(
		val id: UUID,
		val name: String,
		val weeklyRentAmount: Double,
		val paidToDate: LocalDate,
		val credit: Double)

data class NewTenant(
		val name: String,
		val weeklyRentAmount: Double)