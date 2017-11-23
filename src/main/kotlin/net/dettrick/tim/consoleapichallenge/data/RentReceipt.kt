package net.dettrick.tim.consoleapichallenge.data

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.ManyToOne
import java.time.Instant
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Column

@Entity
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