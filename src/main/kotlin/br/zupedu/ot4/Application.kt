package br.zupedu.ot4

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.zupedu.ot4")
		.start()
}

