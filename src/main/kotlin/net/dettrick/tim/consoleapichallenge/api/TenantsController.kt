package net.dettrick.tim.consoleapichallenge.api

import net.dettrick.tim.consoleapichallenge.data.TenantRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.util.Optional
import java.util.UUID
import net.dettrick.tim.consoleapichallenge.data.Tenant as DataTenant

@RestController
@RequestMapping("/tenants")
class TenantsController(
		@Autowired val tenantRepository: TenantRepository
) {

	@PostMapping("/")
	fun createTenant(@RequestBody newTenant: NewTenant): ResponseEntity<Tenant> {
		val tenant: Tenant = tenantRepository.save(newTenant.toData()).toApi()
		return ResponseEntity
				.created(URI.create("/tenants/${tenant.id}"))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(tenant)
	}

	@GetMapping("/{id}")
	fun getTenant(@PathVariable id: UUID): ResponseEntity<Tenant> {
		val optTenant: Optional<Tenant> = tenantRepository.findById(id).map { it.toApi() }
		return optTenant
				.map(fun(tenant: Tenant): ResponseEntity<Tenant> {
					return ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON_UTF8)
							.body(tenant)
				})
				.orElse(ResponseEntity.notFound().build())

	}

	@GetMapping("/")
	fun listTenants(): List<Tenant> {
		return tenantRepository.findAll().map { it.toApi() }
	}

	@GetMapping("/with-rent-receipt")
	fun listTenantsWithReceipt(
			@RequestParam(value = "withinLast", required = true) withinLastStr: String): ResponseEntity<Any> {
		try {
			val d = Duration.parse(withinLastStr)
			val tenants = tenantRepository.findAllWithRentReceiptSince(Instant.now().minus(d)).map { it.toApi() }
			return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(tenants)
		} catch (e: DateTimeParseException) {
			return ResponseEntity.badRequest().body("\"${withinLastStr}\" is not a valid ISO8601 duration")
		}
	}

	fun DataTenant.toApi(): Tenant {
		return Tenant(id!!, name, weeklyRentAmount, account.paidTo.toLocalDate(), account.credit)
	}

	fun NewTenant.toData(): DataTenant {
		return DataTenant(name = name, weeklyRentAmount = weeklyRentAmount)
	}

	fun Instant.toLocalDate() = atZone(ZoneId.systemDefault()).toLocalDate()

}