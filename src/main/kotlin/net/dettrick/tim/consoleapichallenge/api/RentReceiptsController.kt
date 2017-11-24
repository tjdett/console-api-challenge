package net.dettrick.tim.consoleapichallenge.api

import net.dettrick.tim.consoleapichallenge.data.TenantRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Instant
import java.time.ZoneId
import java.util.Optional
import java.util.UUID
import net.dettrick.tim.consoleapichallenge.data.RentReceipt as DataRentReceipt
import net.dettrick.tim.consoleapichallenge.data.Tenant as DataTenant
import org.springframework.http.ResponseEntity
import net.dettrick.tim.consoleapichallenge.data.RentReceiptRepository
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Example

@RestController
@RequestMapping("/tenants/{tenantId}/receipts")
class RentReceiptsController(
		@Autowired val tenantRepository: TenantRepository,
		@Autowired val receiptRepository: RentReceiptRepository
) {
	
	@PostMapping("/")
	fun createRentReceipt(
			@PathVariable tenantId: UUID,
			@RequestBody newRentReceipt: NewRentReceipt): ResponseEntity<RentReceipt> {
		val optTenant: Optional<DataTenant> = tenantRepository.findById(tenantId)
		return optTenant
				.map(fun(tenant: DataTenant): ResponseEntity<RentReceipt> {
					val receipt: RentReceipt = receiptRepository.save(newRentReceipt.toData(tenant)).toApi()
					return ResponseEntity
							.created(URI.create("/tenants/${tenantId}/receipts/${receipt.id}"))
							.contentType(MediaType.APPLICATION_JSON_UTF8)
							.body(receipt)
				})
				.orElse(ResponseEntity.notFound().build())
	}
	
	@GetMapping("/{receiptId}")
	fun getRentReceipt(@PathVariable tenantId: UUID, @PathVariable receiptId: UUID): ResponseEntity<RentReceipt> {
		return receiptRepository.findById(receiptId)
				.filter { it.tenant?.id == tenantId }
				.map { ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(it.toApi()) }
				.orElse(ResponseEntity.notFound().build())
				
	}
	
	@GetMapping("/")
	fun listRentReceipts(@PathVariable tenantId: UUID): ResponseEntity<List<RentReceipt>> {
		val receipts = receiptRepository.findByTenantId(tenantId).map { it.toApi() }
		return ResponseEntity.ok().body(receipts)
	}
	
	fun DataRentReceipt.toApi(): RentReceipt {
		return RentReceipt(id!!, amount)
	}
	
	fun NewRentReceipt.toData(tenant: DataTenant): DataRentReceipt {
		return DataRentReceipt(tenant = tenant, amount = amount)
	}
	
	fun Instant.toLocalDate() = atZone(ZoneId.systemDefault()).toLocalDate()

}