package net.dettrick.tim.consoleapichallenge

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class Application {
	
	@Bean
	fun getObjectMapper(): ObjectMapper {
		val mapper = ObjectMapper()
		mapper.registerModule(JavaTimeModule())
		mapper.registerModule(KotlinModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		return mapper
	}
}