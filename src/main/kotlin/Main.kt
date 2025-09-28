
/**
 * SISTEMA DE CARRITO DE COMPRAS - TECHSTORE EL SALVADOR
 * 
 * Proyecto desarrollado en Kotlin que implementa un sistema completo
 * de carrito de compras con las siguientes características:
 * 
 * ✅ Programación Orientada a Objetos
 * ✅ Manejo de colecciones
 * ✅ Interfaz de consola interactiva
 * ✅ Manejo de eventos del usuario
 * ✅ Validación de entradas
 * ✅ Sistema de logging en archivo
 * ✅ Gestión completa de inventario
 * ✅ Facturación con impuestos
 * ✅ Actualización automática de stock
 * 
 * @author Tu nombre aquí
 * @version 1.0
 * @since 2024
 */

/**
 * Función principal - Punto de entrada de la aplicación
 */
fun main() {
    try {
        // Inicializar el sistema de logging
        Logger.info("=".repeat(50))
        Logger.info("INICIO DEL SISTEMA DE CARRITO DE COMPRAS")
        Logger.info("=".repeat(50))
        Logger.info("Sistema iniciado correctamente")
        
        // Mostrar información del sistema
        mostrarInformacionSistema()
        
        // Verificar componentes críticos
        verificarComponentesSistema()
        
        // Iniciar la aplicación principal
        val menuConsola = MenuConsola()
        menuConsola.iniciar()
        
        // Log de finalización
        Logger.info("Sistema finalizado correctamente")
        Logger.info("=".repeat(50))
        
    } catch (e: Exception) {
        // Manejo de errores críticos
        Logger.error("Error crítico en la aplicación principal", e)
        println("\n❌ ERROR CRÍTICO: ${e.message}")
        println("Por favor, revisa el archivo carrito.log para más detalles.")
        
        // Información de contacto para soporte
        println("\nSi el problema persiste, contacta a:")
        println("📧 Email: soporte@techstore.sv")
        println("📱 WhatsApp: +503 7000-0000")
        
    } finally {
        // Limpieza final
        finalizarSistema()
    }
}

/**
 * Muestra información del sistema al inicio
 */
private fun mostrarInformacionSistema() {
    println()
    println("████████╗███████╗ ██████╗██╗  ██╗███████╗████████╗ ██████╗ ██████╗ ███████╗")
    println("╚══██╔══╝██╔════╝██╔════╝██║  ██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗██╔════╝")
    println("   ██║   █████╗  ██║     ███████║███████╗   ██║   ██║   ██║██████╔╝█████╗  ")
    println("   ██║   ██╔══╝  ██║     ██╔══██║╚════██║   ██║   ██║   ██║██╔══██╗██╔══╝  ")
    println("   ██║   ███████╗╚██████╗██║  ██║███████║   ██║   ╚██████╔╝██║  ██║███████╗")
    println("   ╚═╝   ╚══════╝ ╚═════╝╚═╝  ╚═╝╚══════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚══════╝")
    println()
    println("                    🇸🇻 EL SALVADOR - SISTEMA DE VENTAS 🇸🇻")
    println("                         Versión 1.0 - Desarrollado en Kotlin")
    println()
    
    // Información del sistema
    println("📋 INFORMACIÓN DEL SISTEMA:")
    println("   • Lenguaje: Kotlin")
    println("   • Características: POO, Colecciones, Validaciones, Logging")
    println("   • Base de datos: En memoria")
    println("   • Facturación: Con IVA (13%) y servicio opcional (10%)")
    println("   • Inventario: Actualización automática")
    println()
    
    Logger.info("Información del sistema mostrada al usuario")
}

/**
 * Verifica que todos los componentes críticos estén funcionando
 */
private fun verificarComponentesSistema() {
    println("🔧 VERIFICANDO COMPONENTES DEL SISTEMA...")
    
    try {
        // Verificar sistema de logging
        print("   ✓ Sistema de logging... ")
        Logger.debug("Test de logging durante verificación de componentes")
        println("OK")
        
        // Verificar inventario
        print("   ✓ Servicio de inventario... ")
        val inventario = InventarioService()
        val productos = inventario.obtenerTodosLosProductos()
        println("OK (${productos.size} productos cargados)")
        
        // Verificar validador
        print("   ✓ Validador de entradas... ")
        val testValidacion = ValidadorEntrada.validarEntero("123", 1, 200)
        if (testValidacion.esValido) println("OK") else println("ERROR")
        
        // Verificar carrito
        print("   ✓ Sistema de carrito... ")
        val carritoTest = CarritoCompras()
        println("OK")
        
        // Verificar facturación
        print("   ✓ Sistema de facturación... ")
        val facturacion = FacturacionService()
        println("OK")
        
        println("✅ Todos los componentes verificados correctamente\n")
        Logger.info("Verificación de componentes completada exitosamente")
        
    } catch (e: Exception) {
        println("❌ ERROR en verificación de componentes")
        Logger.error("Error durante verificación de componentes", e)
        throw e
    }
}

/**
 * Realiza la limpieza final del sistema
 */
private fun finalizarSistema() {
    try {
        Logger.info("Finalizando sistema...")
        
        // Aquí podrías agregar limpieza adicional si fuera necesaria
        // Por ejemplo: cerrar conexiones de base de datos, guardar configuraciones, etc.
        
        println("\n💾 Sistema finalizado correctamente")
        Logger.info("Limpieza final completada")
        
    } catch (e: Exception) {
        Logger.error("Error durante finalización del sistema", e)
        println("⚠️  Advertencia: Error durante la finalización del sistema")
    }
}

/**
 * Función de utilidad para mostrar el estado del sistema (debug)
 * Solo se usa en modo de desarrollo
 */
private fun mostrarEstadoSistema() {
    if (System.getProperty("debug") == "true") {
        println("\n🔍 ESTADO DEL SISTEMA (DEBUG):")
        
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        val totalMemory = runtime.totalMemory() / 1024 / 1024
        val freeMemory = runtime.freeMemory() / 1024 / 1024
        val usedMemory = totalMemory - freeMemory
        
        println("   • Memoria máxima: ${maxMemory}MB")
        println("   • Memoria total: ${totalMemory}MB")
        println("   • Memoria usada: ${usedMemory}MB")
        println("   • Memoria libre: ${freeMemory}MB")
        
        Logger.debug("Estado del sistema - Memoria usada: ${usedMemory}MB")
    }
}

/**
 * Función para mostrar ayuda sobre el uso del sistema
 */
private fun mostrarAyuda() {
    println("\n📖 GUÍA RÁPIDA DE USO:")
    println("   1. Selecciona '1' para ver el catálogo de productos")
    println("   2. Usa '2' para agregar productos al carrito")
    println("   3. Revisa tu carrito con '3'")
    println("   4. Modifica cantidades con '4'")
    println("   5. Procesa tu compra con '5'")
    println("   6. Ve estadísticas con '6'")
    println("   0. Sal del sistema con '0'")
    println()
    println("💡 CONSEJOS:")
    println("   • Puedes buscar productos por nombre")
    println("   • Las facturas se generan automáticamente")
    println("   • El inventario se actualiza en tiempo real")
    println("   • Todas las acciones se registran en carrito.log")
    println()
}

/**
 * Función para manejar argumentos de línea de comandos (opcional)
 */
private fun procesarArgumentos(args: Array<String>) {
    if (args.contains("--help") || args.contains("-h")) {
        mostrarAyuda()
    }
    
    if (args.contains("--debug")) {
        System.setProperty("debug", "true")
        println("🐛 Modo debug activado")
        Logger.info("Modo debug activado por parámetro de línea de comandos")
    }
    
    if (args.contains("--version") || args.contains("-v")) {
        println("TechStore El Salvador - Sistema de Carrito de Compras")
        println("Versión: 1.0")
        println("Desarrollado en: Kotlin")
        println("Autor: Tu nombre aquí")
    }
}