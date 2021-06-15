package br.zupedu.ot4.proposta

import br.zupedu.ot4.AnaliseRequest
import br.zupedu.ot4.AnaliseResponse
import br.zupedu.ot4.AnalisesServiceGrpc
import br.zupedu.ot4.PropostaResponse
import br.zupedu.ot4.integracoes.AnalisesClientFactory
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class PropostaService(
    @Inject val analisesClient: AnalisesServiceGrpc.AnalisesServiceBlockingStub,
    @Inject val propostaRepository: PropostaRepository
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    fun novaProposta(@Valid request: PropostaRequestValidator) : PropostaResponse {
        LOGGER.info("Request de Proposta recebida: $request")
        val novaProposta = request.toModel()
        propostaRepository.save(novaProposta)

        val analiseResponse: AnaliseResponse = analisesClient.analisaRestricao(
            AnaliseRequest.newBuilder()
                .setDocumento(request.documento)
                .setNome(request.nome)
                .setIdProposta(novaProposta.id!!)
                .build()
        )
        novaProposta.status = StatusRestricao.of(analiseResponse.resultadoAnalise)
        LOGGER.info("Status de proposta ${novaProposta.id} atualizado para ${analiseResponse.resultadoAnalise}")
        return PropostaResponse.newBuilder()
            .setIdProposta(novaProposta.id)
            .build()
    }
}