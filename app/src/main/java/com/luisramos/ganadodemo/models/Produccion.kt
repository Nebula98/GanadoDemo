package com.luisramos.ganadodemo.models

data class Produccion(
    val id: String = "",
    val fecha: String = "",
    val idAnimal: String = "",
    val nombreAnimal: String = "",
    val produccionLeche: Double = 0.0,
    val observaciones: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)