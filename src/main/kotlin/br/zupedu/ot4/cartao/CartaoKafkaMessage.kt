package br.zupedu.ot4.cartao

import br.zupedu.ot4.shared.annotations.CPForCNPJ
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class CartaoKafkaMessage(
    @field:CPForCNPJ val documento: String,
    val idProposta: Long,
    val numero: String,
    val criadoEm: LocalDateTime,
    val titular: String
) {
}