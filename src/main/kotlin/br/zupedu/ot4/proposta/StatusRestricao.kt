package br.zupedu.ot4.proposta

import br.zupedu.ot4.AnaliseResponse.*

enum class StatusRestricao {
    COM_RESTRICAO {
        override fun toGrpcType(): br.zupedu.ot4.StatusRestricao {
            return br.zupedu.ot4.StatusRestricao.COM_RESTRICAO
        }
    }, SEM_RESTRICAO {
        override fun toGrpcType(): br.zupedu.ot4.StatusRestricao {
            return br.zupedu.ot4.StatusRestricao.SEM_RESTRICAO
        }
    }, NAO_ANALISADO {
        override fun toGrpcType(): br.zupedu.ot4.StatusRestricao {
            return br.zupedu.ot4.StatusRestricao.STATUS_DESCONHECIDO
        }
    };

    companion object {
        /**
         * Constrói um StatusRestricao a partir do resultado da analise em tipo gRPC
         * @param status um objeto do tipo ResultadoAnalise vindo do protobuf
         * @return um StatusRestricao no padrão do sistema
         */
        fun of(status: ResultadoAnalise): StatusRestricao {
            return when(status){
                ResultadoAnalise.SEM_RESTRICAO -> SEM_RESTRICAO
                ResultadoAnalise.COM_RESTRICAO -> COM_RESTRICAO
                else -> NAO_ANALISADO
            }
        }
    }

    abstract fun toGrpcType() : br.zupedu.ot4.StatusRestricao
}