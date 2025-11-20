package com.luisramos.ganadodemo.models

data class Animal(
    val id: String = "",
    val nombre: String = "",
    val raza: String = "",
    val sexo: String = "", // "Macho" o "Hembra"
    val fechaNacimiento: String = "",
    val edad: Int = 0,
    val peso: Double = 0.0,
    val estadoReproductivo: String = "",
    val ultimoParto: String = "",
    val produccionLeche: Double = 0.0,
    val vacunas: String = "",
    val tratamientos: String = "",
    val observaciones: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)
