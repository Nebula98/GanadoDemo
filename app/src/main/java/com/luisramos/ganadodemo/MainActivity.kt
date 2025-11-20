package com.luisramos.ganadodemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisramos.ganadodemo.ui.screens.*
import com.luisramos.ganadodemo.viewmodels.*

import com.luisramos.ganadodemo.ui.screens.dashboard.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GanaderoApp()
            }
        }
    }
}

@Composable
fun GanaderoApp() {
    val authViewModel: AuthViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val animales by dashboardViewModel.animales.collectAsState()
    val insumos by dashboardViewModel.insumos.collectAsState()
    val produccion by dashboardViewModel.produccion.collectAsState()

    when (authState) {
        is AuthState.Authenticated -> {
            DashboardScreen(
                user = currentUser,
                animales = animales,
                insumos = insumos,
                produccion = produccion,
                onLogout = { authViewModel.logout() }
            )
        }
        else -> {
            LoginScreen(
                authState = authState,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onNavigateToRegister = { /* Implementar */ }
            )
        }
    }
}