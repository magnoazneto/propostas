package br.zupedu.ot4.proposta

import br.zupedu.ot4.*
import br.zupedu.ot4.shared.exceptions.PropostaNaoEncontradaException
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class PropostaService(
    @Inject val analisesClient: AnalisesServiceGrpc.AnalisesServiceBlockingStub,
//    @Inject val manager: SynchronousTransactionManager<Any>,
    @Inject val propostaRepository: PropostaRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * cria uma nova proposta de acordo com o status de restricao retornado
     * do sistema de analises
     * @param request uma instância válida da request
     * @return tipo Grpc contendo o Id da proposta criada
     */
    fun novaProposta(@Valid request: PropostaRequestValidator) : PropostaResponse {
        logger.info("Request de Proposta recebida: $request")
        val novaProposta = request.toModel()
        propostaRepository.save(novaProposta)

        try{
            val analiseResponse: AnaliseResponse = analisesClient.analisaRestricao(
                AnaliseRequest.newBuilder()
                    .setDocumento(request.documento)
                    .setNome(request.nome)
                    .setIdProposta(novaProposta.id!!)
                    .build()
            )
            novaProposta.status = StatusRestricao.of(analiseResponse.resultadoAnalise)
            propostaRepository.update(novaProposta)
            logger.info("Status de proposta ${novaProposta.id} atualizado para ${novaProposta.status}")
        } catch (e: StatusRuntimeException){
            if(e.status.code == Status.UNAVAILABLE.code) logger.error("falha na conexão com o serviço de analises")
            else throw e
        }

        return PropostaResponse.newBuilder().setIdProposta(novaProposta.id!!).build()
    }

    /**
     * consulta uma proposta por seu Id ou lança uma exceção caso contrário
     * @param idProposta o id da proposta a ser buscado
     * @throws PropostaNaoEncontradaException
     * @return tipo gRPC contendo a proposta encontrada
     */
    fun consultaProposta(idProposta: Long) : PropostaConsultaResponse {
        return propostaRepository.findById(idProposta)
            .orElseThrow { PropostaNaoEncontradaException("Proposta não encontrada para o id $idProposta") }
            .toGrpcResponse()
    }
}