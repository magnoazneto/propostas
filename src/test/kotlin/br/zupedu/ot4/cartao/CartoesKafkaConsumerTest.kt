package br.zupedu.ot4.cartao

import br.zupedu.ot4.TestProducer
import br.zupedu.ot4.proposta.Endereco
import br.zupedu.ot4.proposta.Proposta
import br.zupedu.ot4.proposta.PropostaRepository
import br.zupedu.ot4.proposta.StatusRestricao
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.inject.Inject

@MicronautTest(transactional = false, environments = ["kafka"])
internal class CartoesKafkaConsumerTest(
    @Inject val propostaRepository: PropostaRepository
) {

    @field:Inject
    lateinit var kafkaProducer: TestProducer

    private val documentoValido = "13605825176"
    private lateinit var proposta: Proposta
    private lateinit var propostaSalva: Proposta

    @BeforeEach
    internal fun setUp() {
        proposta = Proposta(
            documento = documentoValido,
            nome = "Magno Azevedo",
            email = "teste@teste.com.br",
            salario = BigDecimal("2500.0"),
            status = StatusRestricao.SEM_RESTRICAO,
            enderecos = mutableSetOf(Endereco("l", "1", "THE", "PI", "00000-000"))
        )
        propostaSalva = propostaRepository.save(proposta)
    }

    @Test
    fun `deve associar novo cartao a proposta`() {
        val numeroCartao = "1234-5678-9876-5432"
        kafkaProducer.novoCartao(1L, CartaoKafkaMessage(
            documento = documentoValido,
            idProposta = propostaSalva.id!!,
            numero = numeroCartao,
            criadoEm = LocalDateTime.now(),
            titular = "Magno Azevedo"
        ))
        Thread.sleep(1000)
        val propostaAtualizada = propostaRepository.findById(propostaSalva.id!!).get()
        assertNotNull(propostaAtualizada.cartao)
        assertEquals(numeroCartao, propostaAtualizada.cartao?.numero)
    }
}