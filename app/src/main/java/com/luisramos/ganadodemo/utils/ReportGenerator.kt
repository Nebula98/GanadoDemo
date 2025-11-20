package com.luisramos.ganadodemo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.luisramos.ganadodemo.models.Animal
import com.luisramos.ganadodemo.models.Insumo
import com.luisramos.ganadodemo.models.Produccion
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReportGenerator(private val context: Context) {

    // ===== GENERACIÓN DE PDF =====

    fun generateAnimalsPDF(animales: List<Animal>): Result<File> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Reporte_Animales_$timestamp.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)

            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Título
            val title = Paragraph("REPORTE DE ANIMALES")
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(title)

            // Fecha de generación
            val fecha = Paragraph("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.RIGHT)
            document.add(fecha)

            // Resumen
            val resumen = Paragraph("Total de animales registrados: ${animales.size}")
                .setFontSize(12f)
                .setBold()
                .setMarginTop(10f)
                .setMarginBottom(15f)
            document.add(resumen)

            // Tabla
            val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 15f, 10f, 10f, 15f, 15f, 20f)))
                .useAllAvailableWidth()

            // Encabezados
            val headerColor = DeviceRgb(52, 152, 219)
            val headers = listOf("Nombre", "Raza", "Sexo", "Edad", "Peso (kg)", "Producción (L)", "Observaciones")

            headers.forEach { header ->
                table.addHeaderCell(
                    Cell().add(Paragraph(header).setFontSize(10f).setBold())
                        .setBackgroundColor(headerColor)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            }

            // Datos
            animales.forEach { animal ->
                table.addCell(Cell().add(Paragraph(animal.nombre).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(animal.raza).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(animal.sexo).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(animal.edad.toString()).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(animal.peso.toString()).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(animal.produccionLeche.toString()).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(animal.observaciones).setFontSize(9f)))
            }

            document.add(table)

            // Estadísticas adicionales
            val promedioProduccion = animales.map { it.produccionLeche }.average()
            val promedioPeso = animales.map { it.peso }.average()

            val stats = Paragraph("\n\nESTADÍSTICAS")
                .setFontSize(14f)
                .setBold()
            document.add(stats)

            val statsContent = Paragraph()
                .add("Promedio de producción: ${String.format("%.2f", promedioProduccion)} L\n")
                .add("Promedio de peso: ${String.format("%.2f", promedioPeso)} kg\n")
                .add("Machos: ${animales.count { it.sexo == "Macho" }}\n")
                .add("Hembras: ${animales.count { it.sexo == "Hembra" }}")
                .setFontSize(10f)
            document.add(statsContent)

            document.close()
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateInsumosPDF(insumos: List<Insumo>): Result<File> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Reporte_Insumos_$timestamp.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)

            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            val title = Paragraph("REPORTE DE INSUMOS")
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(title)

            val fecha = Paragraph("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.RIGHT)
            document.add(fecha)

            val resumen = Paragraph("Total de insumos registrados: ${insumos.size}")
                .setFontSize(12f)
                .setBold()
                .setMarginTop(10f)
                .setMarginBottom(15f)
            document.add(resumen)

            val table = Table(UnitValue.createPercentArray(floatArrayOf(30f, 20f, 20f, 30f)))
                .useAllAvailableWidth()

            val headerColor = DeviceRgb(46, 204, 113)
            val headers = listOf("Nombre", "Cantidad", "Unidad", "Descripción")

            headers.forEach { header ->
                table.addHeaderCell(
                    Cell().add(Paragraph(header).setFontSize(10f).setBold())
                        .setBackgroundColor(headerColor)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            }

            insumos.forEach { insumo ->
                table.addCell(Cell().add(Paragraph(insumo.nombre).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(insumo.cantidad.toString()).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(insumo.unidadMedida).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(insumo.descripcion).setFontSize(9f)))
            }

            document.add(table)
            document.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateProduccionPDF(produccion: List<Produccion>): Result<File> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Reporte_Produccion_$timestamp.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)

            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            val title = Paragraph("REPORTE DE PRODUCCIÓN")
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(title)

            val fecha = Paragraph("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.RIGHT)
            document.add(fecha)

            val totalProduccion = produccion.sumOf { it.produccionLeche }
            val promedioProduccion = if (produccion.isNotEmpty()) totalProduccion / produccion.size else 0.0

            val resumen = Paragraph()
                .add("Total registros: ${produccion.size}\n")
                .add("Producción total: ${String.format("%.2f", totalProduccion)} L\n")
                .add("Promedio: ${String.format("%.2f", promedioProduccion)} L")
                .setFontSize(11f)
                .setBold()
                .setMarginTop(10f)
                .setMarginBottom(15f)
            document.add(resumen)

            val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 25f, 20f, 40f)))
                .useAllAvailableWidth()

            val headerColor = DeviceRgb(231, 76, 60)
            val headers = listOf("Fecha", "Animal", "Producción (L)", "Observaciones")

            headers.forEach { header ->
                table.addHeaderCell(
                    Cell().add(Paragraph(header).setFontSize(10f).setBold())
                        .setBackgroundColor(headerColor)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            }

            produccion.forEach { prod ->
                table.addCell(Cell().add(Paragraph(prod.fecha).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(prod.nombreAnimal).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(prod.produccionLeche.toString()).setFontSize(9f)))
                table.addCell(Cell().add(Paragraph(prod.observaciones).setFontSize(9f)))
            }

            document.add(table)
            document.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // ===== GENERACIÓN DE EXCEL =====

    fun generateAnimalsExcel(animales: List<Animal>): Result<File> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Reporte_Animales_$timestamp.xlsx"
            val file = File(context.getExternalFilesDir(null), fileName)

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Animales")

            // Estilo de encabezado
            val headerStyle = workbook.createCellStyle()
            val headerFont = workbook.createFont()
            headerFont.bold = true
            headerFont.color = IndexedColors.WHITE.index
            headerStyle.setFont(headerFont)
            headerStyle.fillForegroundColor = IndexedColors.BLUE.index
            headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

            // Encabezados
            val headerRow = sheet.createRow(0)
            val headers = listOf("Nombre", "Raza", "Sexo", "Fecha Nacimiento", "Edad",
                "Peso (kg)", "Estado Reproductivo", "Último Parto",
                "Producción (L)", "Vacunas", "Tratamientos", "Observaciones")

            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }

            // Datos
            animales.forEachIndexed { rowIndex, animal ->
                val row = sheet.createRow(rowIndex + 1)
                row.createCell(0).setCellValue(animal.nombre)
                row.createCell(1).setCellValue(animal.raza)
                row.createCell(2).setCellValue(animal.sexo)
                row.createCell(3).setCellValue(animal.fechaNacimiento)
                row.createCell(4).setCellValue(animal.edad.toDouble())
                row.createCell(5).setCellValue(animal.peso)
                row.createCell(6).setCellValue(animal.estadoReproductivo)
                row.createCell(7).setCellValue(animal.ultimoParto)
                row.createCell(8).setCellValue(animal.produccionLeche)
                row.createCell(9).setCellValue(animal.vacunas)
                row.createCell(10).setCellValue(animal.tratamientos)
                row.createCell(11).setCellValue(animal.observaciones)
            }

            // Autoajustar columnas
            headers.indices.forEach { sheet.autoSizeColumn(it) }

            // Guardar archivo
            val fileOut = FileOutputStream(file)
            workbook.write(fileOut)
            fileOut.close()
            workbook.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateInsumosExcel(insumos: List<Insumo>): Result<File> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Reporte_Insumos_$timestamp.xlsx"
            val file = File(context.getExternalFilesDir(null), fileName)

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Insumos")

            val headerStyle = workbook.createCellStyle()
            val headerFont = workbook.createFont()
            headerFont.bold = true
            headerFont.color = IndexedColors.WHITE.index
            headerStyle.setFont(headerFont)
            headerStyle.fillForegroundColor = IndexedColors.GREEN.index
            headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

            val headerRow = sheet.createRow(0)
            val headers = listOf("Nombre", "Cantidad", "Unidad de Medida", "Descripción")

            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }

            insumos.forEachIndexed { rowIndex, insumo ->
                val row = sheet.createRow(rowIndex + 1)
                row.createCell(0).setCellValue(insumo.nombre)
                row.createCell(1).setCellValue(insumo.cantidad)
                row.createCell(2).setCellValue(insumo.unidadMedida)
                row.createCell(3).setCellValue(insumo.descripcion)
            }

            headers.indices.forEach { sheet.autoSizeColumn(it) }

            val fileOut = FileOutputStream(file)
            workbook.write(fileOut)
            fileOut.close()
            workbook.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateProduccionExcel(produccion: List<Produccion>): Result<File> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Reporte_Produccion_$timestamp.xlsx"
            val file = File(context.getExternalFilesDir(null), fileName)

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Producción")

            val headerStyle = workbook.createCellStyle()
            val headerFont = workbook.createFont()
            headerFont.bold = true
            headerFont.color = IndexedColors.WHITE.index
            headerStyle.setFont(headerFont)
            headerStyle.fillForegroundColor = IndexedColors.RED.index
            headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

            val headerRow = sheet.createRow(0)
            val headers = listOf("Fecha", "ID Animal", "Nombre Animal", "Producción (L)", "Observaciones")

            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }

            produccion.forEachIndexed { rowIndex, prod ->
                val row = sheet.createRow(rowIndex + 1)
                row.createCell(0).setCellValue(prod.fecha)
                row.createCell(1).setCellValue(prod.idAnimal)
                row.createCell(2).setCellValue(prod.nombreAnimal)
                row.createCell(3).setCellValue(prod.produccionLeche)
                row.createCell(4).setCellValue(prod.observaciones)
            }

            headers.indices.forEach { sheet.autoSizeColumn(it) }

            val fileOut = FileOutputStream(file)
            workbook.write(fileOut)
            fileOut.close()
            workbook.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===== COMPARTIR ARCHIVOS =====

    fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (file.extension == "pdf") "application/pdf" else "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Compartir reporte"))
    }
}