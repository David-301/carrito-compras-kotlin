import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * Servicio para generar facturas y manejar la facturación
 * Incluye cálculo de impuestos y generación de números de factura
 */
class FacturacionService {
    
    companion object {
        // Configuración de impuestos (puedes ajustar según tu país)
        const val TASA_IVA = 0.13 // 13% IVA (El Salvador)
        const val TASA_SERVICIO = 0.10 // 10% propina/servicio (opcional)
        
        // Prefijo para números de factura
        const val PREFIJO_FACTURA = "FACT"
    }
    
    /**
     * Datos de la factura generada
     */
    data class Factura(
        val numeroFactura: String,
        val fecha: LocalDateTime,
        val items: List<ItemCarrito>,
        val subtotal: Double,
        val impuestos: Map<String, Double>,
        val total: Double,
        val datosEmpresa: DatosEmpresa = DatosEmpresa()
    )
    
    /**
     * Información de la empresa (personalizable)
     */
    data class DatosEmpresa(
        val nombre: String = "TechStore El Salvador",
        val direccion: String = "San Salvador, El Salvador",
        val telefono: String = "+503 2234-5678",
        val email: String = "ventas@techstore.sv",
        val nit: String = "0614-123456-001-2"
    )
    
    /**
     * Genera un número de factura único
     * @return Número de factura en formato FACT-YYYYMMDD-XXXX
     */
    private fun generarNumeroFactura(): String {
        val fecha = LocalDateTime.now()
        val fechaString = fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val numeroAleatorio = Random.nextInt(1000, 9999)
        return "$PREFIJO_FACTURA-$fechaString-$numeroAleatorio"
    }
    
    /**
     * Calcula todos los impuestos aplicables
     * @param subtotal Subtotal antes de impuestos
     * @param aplicarServicio Si se debe aplicar cargo por servicio
     * @return Map con los diferentes impuestos
     */
    private fun calcularImpuestos(subtotal: Double, aplicarServicio: Boolean = false): Map<String, Double> {
        val impuestos = mutableMapOf<String, Double>()
        
        // IVA (obligatorio)
        val iva = subtotal * TASA_IVA
        impuestos["IVA (${(TASA_IVA * 100).toInt()}%)"] = iva
        
        // Cargo por servicio (opcional)
        if (aplicarServicio) {
            val servicio = subtotal * TASA_SERVICIO
            impuestos["Servicio (${(TASA_SERVICIO * 100).toInt()}%)"] = servicio
        }
        
        return impuestos
    }
    
    /**
     * Procesa la compra y genera la factura
     * @param carrito Carrito de compras a facturar
     * @param inventarioService Servicio de inventario para actualizar stock
     * @param aplicarServicio Si se debe aplicar cargo por servicio
     * @return Factura generada o null si hay error
     */
    fun procesarCompra(
        carrito: CarritoCompras, 
        inventarioService: InventarioService, 
        aplicarServicio: Boolean = false
    ): Factura? {
        
        try {
            Logger.inicioOperacion("Procesamiento de compra")
            
            // Validar que el carrito no esté vacío
            if (carrito.estaVacio()) {
                Logger.error("Intento de procesar compra con carrito vacío")
                println("❌ No se puede procesar una compra con el carrito vacío")
                return null
            }
            
            // Validar disponibilidad de productos
            val (esValido, mensajeError) = carrito.validarDisponibilidad()
            if (!esValido) {
                Logger.error("Validación de disponibilidad falló: $mensajeError")
                println("❌ $mensajeError")
                return null
            }
            
            // Calcular totales
            val subtotal = carrito.calcularSubtotal()
            val impuestos = calcularImpuestos(subtotal, aplicarServicio)
            val totalImpuestos = impuestos.values.sum()
            val total = subtotal + totalImpuestos
            
            // Reducir stock de productos
            val itemsComprados = carrito.obtenerItems()
            for (item in itemsComprados) {
                inventarioService.reducirStock(item.producto.id, item.cantidad)
            }
            
            // Generar factura
            val numeroFactura = generarNumeroFactura()
            val factura = Factura(
                numeroFactura = numeroFactura,
                fecha = LocalDateTime.now(),
                items = itemsComprados,
                subtotal = subtotal,
                impuestos = impuestos,
                total = total
            )
            
            Logger.registrarCompra(numeroFactura, total, itemsComprados.size)
            Logger.finOperacion("Procesamiento de compra exitoso")
            
            return factura
            
        } catch (e: Exception) {
            Logger.error("Error al procesar compra", e)
            println("❌ Error interno al procesar la compra")
            return null
        }
    }
    
    /**
     * Muestra la factura en pantalla con formato profesional
     * @param factura Factura a mostrar
     */
    fun mostrarFactura(factura: Factura) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        
        println("\n" + "=".repeat(80))
        println("                              FACTURA DE VENTA")
        println("=".repeat(80))
        
        // Datos de la empresa
        println("${factura.datosEmpresa.nombre}")
        println("${factura.datosEmpresa.direccion}")
        println("Tel: ${factura.datosEmpresa.telefono} | Email: ${factura.datosEmpresa.email}")
        println("NIT: ${factura.datosEmpresa.nit}")
        
        println("\n" + "-".repeat(80))
        
        // Datos de la factura
        println("Factura No: ${factura.numeroFactura}")
        println("Fecha: ${factura.fecha.format(formatter)}")
        
        println("\n" + "-".repeat(80))
        println("DETALLE DE PRODUCTOS")
        println("-".repeat(80))
        printf("%-35s %6s %12s %15s\n", "PRODUCTO", "CANT.", "PRECIO UNIT.", "SUBTOTAL")
        println("-".repeat(80))
        
        // Items comprados
        factura.items.forEach { item ->
            printf("%-35s %6d %12s %15s\n", 
                item.producto.nombre.take(34),
                item.cantidad,
                "$${String.format("%.2f", item.producto.precio)}",
                "$${String.format("%.2f", item.calcularSubtotal())}"
            )
        }
        
        println("-".repeat(80))
        
        // Totales
        printf("%-54s %15s\n", "SUBTOTAL:", "$${String.format("%.2f", factura.subtotal)}")
        
        // Impuestos
        factura.impuestos.forEach { (concepto, monto) ->
            printf("%-54s %15s\n", concepto, "$${String.format("%.2f", monto)}")
        }
        
        println("-".repeat(80))
        printf("%-54s %15s\n", "TOTAL A PAGAR:", "$${String.format("%.2f", factura.total)}")
        println("=".repeat(80))
        
        println("\n            ¡GRACIAS POR SU COMPRA!")
        println("        Conserve esta factura como comprobante")
        println("=".repeat(80))
        
        Logger.info("Factura mostrada: ${factura.numeroFactura}")
    }
    
    /**
     * Genera un resumen de la factura (versión corta)
     * @param factura Factura a resumir
     * @return String con el resumen
     */
    fun generarResumenFactura(factura: Factura): String {
        return buildString {
            appendLine("Factura: ${factura.numeroFactura}")
            appendLine("Fecha: ${factura.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}")
            appendLine("Items: ${factura.items.size} productos")
            appendLine("Total: $${String.format("%.2f", factura.total)}")
        }
    }
    
    /**
     * Calcula estadísticas de una factura
     * @param factura Factura a analizar
     * @return Map con estadísticas
     */
    fun obtenerEstadisticasFactura(factura: Factura): Map<String, Any?> {
        return mapOf(
            "numeroItems" to factura.items.size,
            "cantidadTotalUnidades" to factura.items.sumOf { it.cantidad },
            "subtotal" to factura.subtotal,
            "totalImpuestos" to factura.impuestos.values.sum(),
            "total" to factura.total,
            "promedioPrecionUnidad" to (factura.subtotal / factura.items.sumOf { it.cantidad }),
            "productoMasCaro" to factura.items.maxByOrNull { it.producto.precio }?.producto?.nombre,
            "productoMasBarato" to factura.items.minByOrNull { it.producto.precio }?.producto?.nombre
        )
    }
    
    /**
     * Función auxiliar para printf en Kotlin
     */
    private fun printf(format: String, vararg args: Any?) {
        print(String.format(format, *args))
    }
}