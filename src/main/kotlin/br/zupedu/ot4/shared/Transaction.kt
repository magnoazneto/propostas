package br.zupedu.ot4.shared

import io.micronaut.transaction.annotation.TransactionalAdvice
import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Singleton
@TransactionalAdvice
class Transaction(
    @Inject val manager: EntityManager
) {

    @Transactional
    fun saveAndCommit(obj: Any): Any{
        manager.persist(obj)
        return obj
    }

    @Transactional
    fun refreshAndCommit(obj: Any): Any{
        manager.merge(obj)
        return obj
    }

    @Transactional
    fun exec(f: () -> Any): Any {
        return f.invoke()
    }

}