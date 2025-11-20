package com.luisramos.ganadodemo.models

data class User(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "usuario", // "admin" o "usuario"
    val fechaCreacion: Long = System.currentTimeMillis()
)
