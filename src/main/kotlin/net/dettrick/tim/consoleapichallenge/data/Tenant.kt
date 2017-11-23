package net.dettrick.tim.consoleapichallenge.data

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import org.hibernate.annotations.GenericGenerator
import javax.persistence.OneToMany
import javax.persistence.Temporal
import javax.persistence.TemporalType
import java.time.Instant
import javax.persistence.Column
import java.time.Duration

@Entity
data class Tenant(
		@Id
		@GeneratedValue(generator = "uuid2")
		@GenericGenerator(name = "uuid2", strategy = "uuid2")
		val id: UUID?,
		@Column
		val name: String,
		@Column
		val weeklyRentAmount: Dollars,
		@Column
		val epoch: Instant = Instant.now()) {
	
	@OneToMany(mappedBy="tenant")
	var receipts: Set<RentReceipt>? = null
	
	val account: AccountState
		get() {
			return AccountState(epoch, totalPaid()).payRent(Duration.ofDays(7), weeklyRentAmount)
		}
	
	fun totalPaid(): Double {
		return receipts?.sumByDouble { it.amount } ?: 0.0
	}
		
}