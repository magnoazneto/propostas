package br.zupedu.ot4.cartao

import br.zupedu.ot4.TestProducer
import br.zupedu.ot4.proposta.Endereco
import br.zupedu.ot4.proposta.Proposta
import br.zupedu.ot4.proposta.PropostaRepository
import br.zupedu.ot4.proposta.StatusRestricao
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.awaitility.Awaitility
import org.awaitility.Awaitility.*
import org.awaitility.core.ConditionTimeoutException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*
import javax.inject.Inject

@MicronautTest(transactional = false, environments = ["kafka"])
internal class CartoesKafkaConsumerTest(
    @Inject val propostaRepository: PropostaRepository,
    @Inject val cartaoRepository: CartaoRepository
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

    @AfterEach
    internal fun tearDown() {
        propostaRepository.deleteAll()
        cartaoRepository.deleteAll()
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
        await().atMost(3, SECONDS).until{ cartaoRepository.existsByNumero(numeroCartao) }
        val propostaAtualizada = propostaRepository.findById(propostaSalva.id!!).get()
        assertNotNull(propostaAtualizada.cartao)
        assertEquals(numeroCartao, propostaAtualizada.cartao?.numero)
    }

    @Test
    fun `nao deve associar novo cartao a proposta inexistente`() {
        val numeroCartao = "1234-5678-9876-5432"
        kafkaProducer.novoCartao(1L, CartaoKafkaMessage(
            documento = documentoValido,
            idProposta = 10L,
            numero = numeroCartao,
            criadoEm = LocalDateTime.now(),
            titular = "Magno Azevedo"
        ))
        assertThrows<ConditionTimeoutException> {
            await().atMost(2, SECONDS)
                .until{ cartaoRepository.existsByNumero(numeroCartao) }
        }
        assertFalse(cartaoRepository.existsByNumero(numeroCartao))
    }

    @ParameterizedTest
    @ValueSource(strings = ["12345", "", "00000000000"])
    fun `nao deve associar novo cartao se o documento for invalido`(documentoTest: String) {
        val numeroCartao = "1234-5678-9876-5432"
        kafkaProducer.novoCartao(1L, CartaoKafkaMessage(
            documento = documentoTest,
            idProposta = propostaSalva.id!!,
            numero = numeroCartao,
            criadoEm = LocalDateTime.now(),
            titular = "Magno Azevedo"
        ))
        assertThrows<ConditionTimeoutException> {
            await().atMost(2, SECONDS)
                .until{ cartaoRepository.existsByNumero(numeroCartao) }
        }
        val propostaAtualizada = propostaRepository.findById(propostaSalva.id!!).get()
        assertNull(propostaAtualizada.cartao)
    }
}