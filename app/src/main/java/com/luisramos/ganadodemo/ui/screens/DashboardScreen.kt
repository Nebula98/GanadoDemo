package com.luisramos.ganadodemo.ui.screens.dashboard


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.luisramos.ganadodemo.models.*
import com.luisramos.ganadodemo.utils.ReportGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.FileDownload

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    user: User?,
    animales: List<Animal>,
    insumos: List<Insumo>,
    produccion: List<Produccion>,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showReportDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Animales", "Insumos", "Producción")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val reportGenerator = remember { ReportGenerator(context) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Hola, ${user?.nombre ?: "Usuario"}") },
            actions = {
                IconButton(onClick = { showReportDialog = true }) {
                    Icon(Icons.Default.Description, "Generar Reporte")
                }
                TextButton(onClick = onLogout) {
                    Text("Cerrar sesión")
                }
            }
        )

        // Resumen
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Resumen General", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Animales: ${animales.size}")
                Text("Total Insumos: ${insumos.size}")
                Text("Registros de Producción: ${produccion.size}")
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> AnimalesList(animales)
            1 -> InsumosList(insumos)
            2 -> ProduccionList(produccion)
        }
    }

    // Diálogo de reportes
    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onGeneratePDF = { tipo ->
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        when (tipo) {
                            "animales" -> reportGenerator.generateAnimalsPDF(animales)
                            "insumos" -> reportGenerator.generateInsumosPDF(insumos)
                            "produccion" -> reportGenerator.generateProduccionPDF(produccion)
                            else -> Result.failure(Exception("Tipo no válido"))
                        }
                    }

                    result.onSuccess { file ->
                        Toast.makeText(context, "PDF generado: ${file.name}", Toast.LENGTH_SHORT).show()
                        reportGenerator.shareFile(file)
                    }.onFailure { error ->
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                showReportDialog = false
            },
            onGenerateExcel = { tipo ->
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        when (tipo) {
                            "animales" -> reportGenerator.generateAnimalsExcel(animales)
                            "insumos" -> reportGenerator.generateInsumosExcel(insumos)
                            "produccion" -> reportGenerator.generateProduccionExcel(produccion)
                            else -> Result.failure(Exception("Tipo no válido"))
                        }
                    }

                    result.onSuccess { file ->
                        Toast.makeText(context, "Excel generado: ${file.name}", Toast.LENGTH_SHORT).show()
                        reportGenerator.shareFile(file)
                    }.onFailure { error ->
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                showReportDialog = false
            }
        )
    }
}

@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onGeneratePDF: (String) -> Unit,
    onGenerateExcel: (String) -> Unit
) {
    var selectedType by remember { mutableStateOf("animales") }
    var selectedFormat by remember { mutableStateOf("pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generar Reporte") },
        text = {
            Column {
                Text("Tipo de reporte:", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedType == "animales",
                        onClick = { selectedType = "animales" }
                    )
                    Text("Animales")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedType == "insumos",
                        onClick = { selectedType = "insumos" }
                    )
                    Text("Insumos")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedType == "produccion",
                        onClick = { selectedType = "produccion" }
                    )
                    Text("Producción")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Formato:", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "pdf",
                        onClick = { selectedFormat = "pdf" }
                    )
                    Icon(Icons.Default.Article, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "excel",
                        onClick = { selectedFormat = "excel" }
                    )
                    Icon(Icons.Default.TableRows, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Excel")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedFormat == "pdf") {
                        onGeneratePDF(selectedType)
                    } else {
                        onGenerateExcel(selectedType)
                    }
                }
            ) {
                Icon(Icons.Default.FileDownload, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Generar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AnimalesList(animales: List<Animal>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(animales) { animal ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(animal.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("Raza: ${animal.raza}")
                    Text("Sexo: ${animal.sexo}")
                    Text("Peso: ${animal.peso} kg")
                    Text("Producción: ${animal.produccionLeche} L")
                }
            }
        }
    }
}

@Composable
fun InsumosList(insumos: List<Insumo>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(insumos) { insumo ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(insumo.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("Cantidad: ${insumo.cantidad} ${insumo.unidadMedida}")
                    Text("Descripción: ${insumo.descripcion}")
                }
            }
        }
    }
}

@Composable
fun ProduccionList(produccion: List<Produccion>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(produccion) { prod ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Animal: ${prod.nombreAnimal}", style = MaterialTheme.typography.titleMedium)
                    Text("Fecha: ${prod.fecha}")
                    Text("Producción: ${prod.produccionLeche} L")
                    if (prod.observaciones.isNotEmpty()) {
                        Text("Observaciones: ${prod.observaciones}")
                    }
                }
            }
        }
    }
}
