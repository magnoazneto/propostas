package br.zupedu.ot4.proposta

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Proposta(
    val documento: String,
    val nome: String,
    val email: String,
    var status: StatusRestricao,
    @field:Embedded val endereco: Endereco
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
    val criadaEm = LocalDateTime.now()
}