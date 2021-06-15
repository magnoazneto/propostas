package br.zupedu.ot4.proposta

import br.zupedu.ot4.AnaliseResponse.*

enum class StatusRestricao {
    COM_RESTRICAO, SEM_RESTRICAO, NAO_ANALISADO;

    companion object {
        fun of(status: ResultadoAnalise): StatusRestricao {
            return when(status){
                ResultadoAnalise.SEM_RESTRICAO -> SEM_RESTRICAO
                ResultadoAnalise.COM_RESTRICAO -> COM_RESTRICAO
                else -> NAO_ANALISADO
            }
        }
    }
}