package net.dettrick.tim.consoleapichallenge.data

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface TenantRepository : JpaRepository<Tenant, UUID>