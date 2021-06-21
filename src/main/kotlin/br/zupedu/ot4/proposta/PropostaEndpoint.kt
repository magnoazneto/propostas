package br.zupedu.ot4.proposta

import br.zupedu.ot4.*
import br.zupedu.ot4.shared.errors.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class PropostaEndpoint (
    @Inject val propostaService: PropostaService
): PropostasServiceGrpc.PropostasServiceImplBase() {

    override fun criarProposta(
        request: PropostaRequest,
        responseObserver: StreamObserver<PropostaResponse>
    ) {
        val response = propostaService.novaProposta(PropostaRequestValidator(request))
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun consultarProposta(
        request: PropostaConsultaRequest,
        responseObserver: StreamObserver<PropostaConsultaResponse>
    ) {
        val responseGrpc = propostaService.consultaProposta(request.idProposta)
        responseObserver.onNext(responseGrpc)
        responseObserver.onCompleted()
    }
}