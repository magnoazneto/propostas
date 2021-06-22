package br.zupedu.ot4.proposta

import br.zupedu.ot4.PropostaConsultaResponse
import br.zupedu.ot4.cartao.Cartao
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Proposta(
    val documento: String,
    val nome: String,
    val email: String,
    val salario: BigDecimal,
    @field:Enumerated(value = EnumType.STRING)
    var status: StatusRestricao,
    @field:OneToMany(cascade = [ CascadeType.PERSIST ], mappedBy = "proposta")
    @field:OnDelete(action = OnDeleteAction.CASCADE)
    val enderecos: Set<Endereco>,
    @field:OneToOne(cascade = [ CascadeType.MERGE ])
    @field:OnDelete(action = OnDeleteAction.CASCADE)
    var cartao: Cartao? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
    val criadaEm = LocalDateTime.now()

    fun toGrpcResponse() : PropostaConsultaResponse {
        return PropostaConsultaResponse.newBuilder()
            .setDocumento(documento)
            .setEmail(email)
            .setNome(nome)
            .setSalario(salario.toString())
            .setStatus(status.toGrpcType())
            .build()
    }
}