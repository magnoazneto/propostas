package br.zupedu.ot4.proposta

import br.zupedu.ot4.PropostaRequest
import br.zupedu.ot4.PropostaResponse
import br.zupedu.ot4.PropostasServiceGrpc
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
}