package br.zupedu.ot4.proposta

import br.zupedu.ot4.cartao.Cartao
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Proposta(
    val documento: String,
    val nome: String,
    val email: String,
    @field:Enumerated(value = EnumType.STRING)
    var status: StatusRestricao,
    @field:OneToMany(cascade = [ CascadeType.PERSIST ], mappedBy = "proposta")
    val enderecos: Set<Endereco>,
    @field:OneToOne(cascade = [ CascadeType.MERGE ])
    var cartao: Cartao? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
    val criadaEm = LocalDateTime.now()
}