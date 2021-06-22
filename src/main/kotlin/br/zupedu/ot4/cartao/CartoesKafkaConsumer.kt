package br.zupedu.ot4.cartao

import br.zupedu.ot4.proposta.PropostaRepository
import br.zupedu.ot4.shared.exceptions.PropostaNaoEncontradaException
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid

@KafkaListener(offsetReset = OffsetReset.EARLIEST, groupId = "propostas")
@Validated
class CartoesKafkaConsumer(
    @Inject val propostaRepository: PropostaRepository
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Topic("novo-cartao")
    @Transactional
    fun receberCartoes(@KafkaKey idCartao: Long, @Valid cartao: CartaoKafkaMessage) {
        LOGGER.info("novo cartão escutado: $cartao")
        val propostaRelacionada = propostaRepository.findById(cartao.idProposta).orElseThrow {
            PropostaNaoEncontradaException("Proposta não encontrada para o id: ${cartao.idProposta}")
        }
        propostaRelacionada.cartao = Cartao(cartao, propostaRelacionada)
        propostaRepository.update(propostaRelacionada)
        LOGGER.info("novo cartão de numero ${cartao.numero} associado a proposta de id ${propostaRelacionada.id}")
    }
}