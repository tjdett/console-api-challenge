package net.dettrick.tim.consoleapichallenge.data

import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name="receipts")
data class RentReceipt(
		@Id
		@GeneratedValue(generator = "uuid2")
		@GenericGenerator(name = "uuid2", strategy = "uuid2")
		val id: UUID?,
		@ManyToOne
		val tenant: Tenant,
		@Column
		val amount: Double,
		@Column
		val timestamp: Instant = Instant.now())