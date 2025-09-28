import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Sistema de logging para registrar eventos y errores del sistema
 */
object Logger {
    
    private val archivoLog = File("carrito.log")
    private val formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    /**
     * Niveles de log disponibles
     */
    enum class Nivel {
        INFO, WARNING, ERROR, DEBUG
    }
    
    init {
        // Crear el archivo de log si no existe
        try {
            if (!archivoLog.exists()) {
                archivoLog.createNewFile()
                escribirLog("Sistema iniciado - Archivo de log creado", Nivel.INFO)
            }
        } catch (e: IOException) {
            println("Error al crear el archivo de log: ${e.message}")
        }
    }
    
    /**
     * Escribe un mensaje en el archivo de log
     * @param mensaje Mensaje a registrar
     * @param nivel Nivel del log (INFO, WARNING, ERROR, DEBUG)
     */
    fun escribirLog(mensaje: String, nivel: Nivel = Nivel.INFO) {
        try {
            val timestamp = LocalDateTime.now().format(formatoFecha)
            val entradaLog = "[$timestamp] [${nivel.name}] $mensaje\n"
            
            FileWriter(archivoLog, true).use { writer ->
                writer.append(entradaLog)
            }
            
            // También mostrar en consola si es un error
            if (nivel == Nivel.ERROR) {
                println("ERROR: $mensaje")
            }
            
        } catch (e: IOException) {
            println("Error al escribir en el log: ${e.message}")
        }
    }
    
    /**
     * Registra información general
     */
    fun info(mensaje: String) {
        escribirLog(mensaje, Nivel.INFO)
    }
    
    /**
     * Registra advertencias
     */
    fun warning(mensaje: String) {
        escribirLog(mensaje, Nivel.WARNING)
    }
    
    /**
     * Registra errores
     */
    fun error(mensaje: String) {
        escribirLog(mensaje, Nivel.ERROR)
    }
    
    /**
     * Registra información de debug
     */
    fun debug(mensaje: String) {
        escribirLog(mensaje, Nivel.DEBUG)
    }
    
    /**
     * Registra errores con excepción
     * @param mensaje Mensaje descriptivo del error
     * @param excepcion La excepción que se produjo
     */
    fun error(mensaje: String, excepcion: Exception) {
        val mensajeCompleto = "$mensaje - Excepción: ${excepcion.javaClass.simpleName}: ${excepcion.message}"
        escribirLog(mensajeCompleto, Nivel.ERROR)
    }
    
    /**
     * Registra el inicio de una operación
     */
    fun inicioOperacion(operacion: String) {
        info("INICIO: $operacion")
    }
    
    /**
     * Registra el fin de una operación
     */
    fun finOperacion(operacion: String) {
        info("FIN: $operacion")
    }
    
    /**
     * Registra una compra realizada
     */
    fun registrarCompra(numeroFactura: String, total: Double, cantidadItems: Int) {
        info("COMPRA REALIZADA - Factura: $numeroFactura, Total: $${"%.2f".format(total)}, Items: $cantidadItems")
    }
    
    /**
     * Registra cambios en el inventario
     */
    fun registrarCambioInventario(productoId: Int, nombreProducto: String, cantidadAnterior: Int, cantidadNueva: Int) {
        info("INVENTARIO - Producto ID:$productoId '$nombreProducto': $cantidadAnterior -> $cantidadNueva")
    }
    
    /**
     * Limpia el archivo de log (útil para testing)
     */
    fun limpiarLog() {
        try {
            FileWriter(archivoLog, false).use { writer ->
                writer.write("")
            }
            info("Log limpiado")
        } catch (e: IOException) {
            println("Error al limpiar el log: ${e.message}")
        }
    }
}