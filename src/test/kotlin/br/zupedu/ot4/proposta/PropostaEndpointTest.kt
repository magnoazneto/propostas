package br.zupedu.ot4.proposta

import br.zupedu.ot4.*
import br.zupedu.ot4.Endereco
import br.zupedu.ot4.integracoes.AnalisesClientFactory
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class PropostaEndpointTest(
    @Inject val grpcClient: PropostasServiceGrpc.PropostasServiceBlockingStub,
    @Inject val propostaRepository: PropostaRepository
){

    @field:Inject
    private lateinit var analisesStub: AnalisesServiceGrpc.AnalisesServiceBlockingStub

    private lateinit var propostaRequest: PropostaRequest.Builder
    private var documentoValido = "13605825176"

    @BeforeEach
    internal fun setUp() {
        propostaRepository.deleteAll()

        propostaRequest = PropostaRequest.newBuilder()
            .setDocumento(documentoValido)
            .setEmail("teste@teste.com")
            .setNome("Tester")
            .setEndereco(
                Endereco.newBuilder()
                    .setCep("00000-000")
                    .setLogradouro("Logradouro")
                    .setNumero("12")
                    .setCidade("Terehell")
                    .setEstado("PI")
                    .build()
            )
            .setSalario("2500.0")

        `when`(analisesStub.analisaRestricao(any(AnaliseRequest::class.java)))
            .thenReturn(AnaliseResponse.newBuilder()
                .setResultadoAnalise(AnaliseResponse.ResultadoAnalise.SEM_RESTRICAO)
                .setIdProposta(1L)
                .build()
            )
    }

    @Test
    fun `deve criar proposta com dados validos`() {
        val response: PropostaResponse = grpcClient.criarProposta(propostaRequest.build())

        val propostaSalva = propostaRepository.findById(response.idProposta).get()
        assertEquals(documentoValido, propostaSalva.documento)
    }

    @ParameterizedTest
    @ValueSource(strings = ["12345678901", ""])
    fun `nao deve criar proposta com documento invalido`(documento: String) {
        assertThrows<StatusRuntimeException> {
            grpcClient.criarProposta(propostaRequest
            .setDocumento(documento).build())
        }.let { e ->
            assertEquals(Status.INVALID_ARGUMENT.code, e.status.code)
        }
    }

    @Test
    fun `nao deve criar proposta com nome do titular em branco`() {
        assertThrows<StatusRuntimeException> {
            grpcClient.criarProposta(propostaRequest
                .setNome("").build())
        }.let { e ->
            assertEquals(Status.INVALID_ARGUMENT.code, e.status.code)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["teste", ""])
    fun `nao deve criar proposta com email invalido`(email: String) {
        assertThrows<StatusRuntimeException> {
            grpcClient.criarProposta(propostaRequest
                .setEmail(email).build())
        }.let { e ->
            assertEquals(Status.INVALID_ARGUMENT.code, e.status.code)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["-300", "-1", "teste"])
    fun `nao deve criar proposta com salario invalido`(salario: String) {
        assertThrows<StatusRuntimeException> {
            grpcClient.criarProposta(propostaRequest
                .setSalario(salario).build())
        }.let { e ->
            assertEquals(Status.INVALID_ARGUMENT.code, e.status.code)
        }
    }

    @Test
    fun `deve retornar proposta para ID valido`() {
        val request = propostaRequest.build()
        val response: PropostaResponse = grpcClient.criarProposta(request)

        val propostaEncontrada: PropostaConsultaResponse = grpcClient.consultarProposta(
            PropostaConsultaRequest.newBuilder()
                .setIdProposta(response.idProposta)
                .build()
        )
        assertEquals(request.documento, propostaEncontrada.documento)
        assertEquals(request.email, propostaEncontrada.email)
    }

    @Test
    fun `deve retornar NOT_FOUND para proposta nao existente`() {
        assertThrows<StatusRuntimeException> {
            grpcClient.consultarProposta(
                PropostaConsultaRequest.newBuilder()
                    .setIdProposta(1L)
                    .build()
            )
        }.let { e ->
            assertEquals(Status.NOT_FOUND.code, e.status.code)
        }
    }

    @Factory
    class Clients(@GrpcChannel(GrpcServerChannel.NAME) val channel: ManagedChannel) {
        @Bean
        fun blockingStub() = PropostasServiceGrpc.newBlockingStub(channel)
    }

    @Factory
    @Replaces(factory = AnalisesClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = mock(AnalisesServiceGrpc.AnalisesServiceBlockingStub::class.java)
    }
}