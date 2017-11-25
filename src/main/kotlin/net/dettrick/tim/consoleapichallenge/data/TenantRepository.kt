package net.dettrick.tim.consoleapichallenge.data

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import org.springframework.data.repository.query.Param

interface TenantRepository : JpaRepository<Tenant, UUID> {
	
	@Query("SELECT DISTINCT t FROM Tenant t WHERE t.id IN (SELECT r.tenant FROM RentReceipt r WHERE r.timestamp >= :timestamp)")
	fun findAllWithRentReceiptSince(@Param("timestamp") timestamp: Instant): List<Tenant>
}