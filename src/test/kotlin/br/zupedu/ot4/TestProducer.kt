package br.zupedu.ot4

import br.zupedu.ot4.cartao.CartaoKafkaMessage
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient
interface TestProducer {

    @Topic("novo-cartao")
    fun novoCartao(@KafkaKey idCartao: Long, cartaoKafkaMessage: CartaoKafkaMessage)
}