package net.dettrick.tim.consoleapichallenge.data

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface RentReceiptRepository : JpaRepository<RentReceipt, UUID> {
	fun findByTenantId(tenantId: UUID): List<RentReceipt>
}