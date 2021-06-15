package br.zupedu.ot4.shared.annotations

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@CPF
@CNPJ
@MustBeDocumented
@ConstraintComposition(CompositionType.OR)
@ReportAsSingleViolation
@Target(FIELD, TYPE_PARAMETER, VALUE_PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = [])
annotation class CPForCNPJ(
    val message: String = "Documento invalido: \${validatedValue}",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)
