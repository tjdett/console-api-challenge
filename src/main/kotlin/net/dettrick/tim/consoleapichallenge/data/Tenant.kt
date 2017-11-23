package net.dettrick.tim.consoleapichallenge.data

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import java.time.Duration
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="tenants")
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
		val epoch: Instant = Instant.now(),
		@Formula("select sum(r.amount) from receipts r where r.tenant_id = id")
		val totalPaid: Dollars = 0.0
		) {
	
	val account: AccountState
		get() {
			return AccountState(epoch, totalPaid).payRent(Duration.ofDays(7), weeklyRentAmount)
		}
		
}