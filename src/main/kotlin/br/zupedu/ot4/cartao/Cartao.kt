package br.zupedu.ot4.cartao

import br.zupedu.ot4.proposta.Proposta
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Cartao(
    val documento: String,
    val titular: String,
    val criadoEm: LocalDateTime,
    val numero: String,
    @field:OneToOne(cascade = [ CascadeType.ALL ])
    @field:OnDelete(action = OnDeleteAction.CASCADE)
    val proposta: Proposta
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    constructor(mensagem: CartaoKafkaMessage, proposta: Proposta) : this(
        documento = mensagem.documento,
        titular = mensagem.titular,
        criadoEm = mensagem.criadoEm,
        numero = mensagem.numero,
        proposta = proposta
    )
}