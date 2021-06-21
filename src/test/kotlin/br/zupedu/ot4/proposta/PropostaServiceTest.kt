package br.zupedu.ot4.proposta

import br.zupedu.ot4.AnaliseRequest
import br.zupedu.ot4.AnaliseResponse
import br.zupedu.ot4.AnalisesServiceGrpc
import br.zupedu.ot4.integracoes.AnalisesClientFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class PropostaServiceTest(
    @Inject val propostaService: PropostaService,
    @Inject val propostaRepository: PropostaRepository
){
    @field:Inject
    private lateinit var analisesStub: AnalisesServiceGrpc.AnalisesServiceBlockingStub

    private lateinit var request: PropostaRequestValidator

    @BeforeEach
    internal fun setUp() {
        propostaRepository.deleteAll()
        request = PropostaRequestValidator(
            documento = "13605825176",
            email = "teste@teste.com.br",
            nome = "Magno Azevedo",
            endereco = Endereco("logradouro", "1422", "Fortaleza", "CE", "64000-000"),
            salario = BigDecimal("2500.00")
        )
    }

    @Test
    internal fun `deve salvar proposta como NAO_ANALISADA caso comunicacao com servico externo falhe`() {
        `when`(analisesStub.analisaRestricao(any(AnaliseRequest::class.java))).thenThrow(StatusRuntimeException(Status.UNAVAILABLE))

        val responseGrpc = propostaService.novaProposta(request)
        assertNotNull(responseGrpc)

        val propostaSalva = propostaRepository.findById(responseGrpc.idProposta).get()
        assertEquals(StatusRestricao.NAO_ANALISADO, propostaSalva.status)
    }

    @Test
    internal fun `deve salvar proposta como COM_RESTRICAO caso haja resposta equivalente do servico externo`() {
        `when`(analisesStub.analisaRestricao(any(AnaliseRequest::class.java))).thenReturn(AnaliseResponse.newBuilder()
            .setIdProposta(1L)
            .setResultadoAnalise(AnaliseResponse.ResultadoAnalise.COM_RESTRICAO)
            .build())

        val responseGrpc = propostaService.novaProposta(request)
        assertNotNull(responseGrpc)

        val propostaSalva = propostaRepository.findById(responseGrpc.idProposta).get()
        assertEquals(StatusRestricao.COM_RESTRICAO, propostaSalva.status)
    }

    @Test
    internal fun `deve salvar proposta como SEM_RESTRICAO caso haja resposta equivalente do servico externo`() {
        `when`(analisesStub.analisaRestricao(any(AnaliseRequest::class.java))).thenReturn(AnaliseResponse.newBuilder()
            .setIdProposta(1L)
            .setResultadoAnalise(AnaliseResponse.ResultadoAnalise.SEM_RESTRICAO)
            .build())

        val responseGrpc = propostaService.novaProposta(request)
        assertNotNull(responseGrpc)

        val propostaSalva = propostaRepository.findById(responseGrpc.idProposta).get()
        assertEquals(StatusRestricao.SEM_RESTRICAO, propostaSalva.status)
    }

    @Factory
    @Replaces(factory = AnalisesClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = mock(AnalisesServiceGrpc.AnalisesServiceBlockingStub::class.java)
    }
}