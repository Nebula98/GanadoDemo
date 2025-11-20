package com.luisramos.ganadodemo.models

data class Insumo(
    val id: String = "",
    val nombre: String = "",
    val cantidad: Double = 0.0,
    val unidadMedida: String = "",
    val descripcion: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)