package br.zupedu.ot4.proposta

import br.zupedu.ot4.PropostaRequest
import br.zupedu.ot4.shared.annotations.CPForCNPJ
import br.zupedu.ot4.shared.annotations.UniqueValue
import io.micronaut.core.annotation.Introspected
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import javax.persistence.Embeddable
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

@Introspected
data class PropostaRequestValidator(
    @field:NotBlank @field:CPForCNPJ @field:UniqueValue(fieldName = "documento", targetClass = Proposta::class)
    val documento: String,
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank val nome: String,
    val endereco: Endereco,
    @field:Positive val salario: BigDecimal
) {
    constructor(request: PropostaRequest) : this(
        documento = request.documento,
        email = request.email,
        nome = request.nome,
        endereco = Endereco(
            logradouro = request.endereco.logradouro,
            numero = request.endereco.numero,
            cidade = request.endereco.cidade,
            estado = request.endereco.estado,
            cep = request.endereco.cep
        ),
        salario = try {
            request.salario.toBigDecimal()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Salario deve ter formato valido")
        }
    )

    /**
     * retorna uma nova instância de Proposta e realiza o link entre o endereço existente e a nova
     * instancia de proposta
     * @return uma instância de Proposta a ser salva no banco de dados
     */
    fun toModel(): Proposta {
        val novaProposta = Proposta(
            documento = documento,
            nome = nome,
            email = email,
            status = StatusRestricao.NAO_ANALISADO,
            enderecos = mutableSetOf(endereco),
            salario = salario
        )
        endereco.proposta = novaProposta
        return novaProposta
    }
}
