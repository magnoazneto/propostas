package br.zupedu.ot4.proposta

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PropostaRepository : JpaRepository<Proposta, Long> {
}