package net.dettrick.tim.consoleapichallenge.api

import java.util.UUID

data class RentReceipt(val id: UUID, val amount: Double)

data class NewRentReceipt(val amount: Double)