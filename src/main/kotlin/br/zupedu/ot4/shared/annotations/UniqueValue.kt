package br.zupedu.ot4.shared.annotations

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.transaction.annotation.TransactionalAdvice
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Target(FIELD, TYPE_PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueValueValidator::class])
annotation class UniqueValue(
    val message: String = "Valor informado já existe no banco de dados",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
    val fieldName: String,
    val targetClass: KClass<*>
)

@Singleton
@TransactionalAdvice
class UniqueValueValidator(@Inject val manager: EntityManager): ConstraintValidator<UniqueValue, Any> {

    lateinit var field: String
    lateinit var targetClass: KClass<*>

    override fun initialize(constraintAnnotation: UniqueValue) {
        field = constraintAnnotation.fieldName
        targetClass = constraintAnnotation.targetClass
    }

    override fun isValid(
        value: Any?,
        annotationMetadata: AnnotationValue<UniqueValue>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value == null) return true

        return manager
            .createQuery("select 1 from ${targetClass.simpleName} where $field =:pValue")
            .setParameter("pValue", value)
            .resultList
            .isEmpty()
    }
}
