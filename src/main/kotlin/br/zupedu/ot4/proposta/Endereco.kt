package br.zupedu.ot4.proposta

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Endereco(
    @field:NotBlank val logradouro: String,
    @field:NotBlank val numero: String,
    @field:NotBlank val cidade: String,
    @field:NotBlank val estado: String,
    @field:NotBlank val cep: String,
    @field:ManyToOne var proposta: Proposta? = null
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}