package br.zupedu.ot4.integracoes

import br.zupedu.ot4.AnalisesServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class AnalisesClientFactory(@GrpcChannel("analises") val channel: ManagedChannel) {

    @Singleton
    fun analisesClientStub() = AnalisesServiceGrpc.newBlockingStub(channel)
}