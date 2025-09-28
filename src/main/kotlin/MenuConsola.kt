import java.util.Scanner

/**
 * Clase que maneja toda la interfaz de usuario en consola
 * Proporciona menús interactivos y manejo de entrada del usuario
 */
class MenuConsola {
    
    private val scanner = Scanner(System.`in`)
    private val inventarioService = InventarioService()
    private val carrito = CarritoCompras()
    private val facturacionService = FacturacionService()
    
    init {
        Logger.info("MenuConsola inicializado")
    }
    
    /**
     * Inicia la aplicación y muestra el menú principal
     */
    fun iniciar() {
        mostrarBienvenida()
        
        var continuar = true
        while (continuar) {
            mostrarMenuPrincipal()
            val opcion = leerEntradaUsuario("Selecciona una opción: ")
            
            continuar = when (opcion?.trim()?.lowercase()) {
                "1" -> { mostrarCatalogo(); true }
                "2" -> { agregarProductoAlCarrito(); true }
                "3" -> { verCarrito(); true }
                "4" -> { modificarCarrito(); true }
                "5" -> { procesarCompra(); preguntarContinuar() }
                "6" -> { mostrarEstadisticas(); true }
                "0", "salir" -> false
                else -> { 
                    println("❌ Opción inválida. Por favor, intenta de nuevo.")
                    Logger.warning("Opción de menú inválida: $opcion")
                    true 
                }
            }
        }
        
        despedida()
    }
    
    /**
     * Muestra el mensaje de bienvenida
     */
    private fun mostrarBienvenida() {
        println("\n" + "=".repeat(60))
        println("    🛒 BIENVENIDO A TECHSTORE EL SALVADOR 🛒")
        println("        Tu tienda de tecnología de confianza")
        println("=".repeat(60))
        Logger.info("Usuario inició sesión en la aplicación")
    }
    
    /**
     * Muestra el menú principal
     */
    private fun mostrarMenuPrincipal() {
        val resumenCarrito = carrito.obtenerResumen()
        
        println("\n" + "╔" + "═".repeat(50) + "╗")
        println("║" + "               MENÚ PRINCIPAL".padEnd(50) + "║")
        println("╠" + "═".repeat(50) + "╣")
        println("║ 1. Ver catálogo de productos".padEnd(51) + "║")
        println("║ 2. Agregar producto al carrito".padEnd(51) + "║")
        println("║ 3. Ver carrito de compras".padEnd(51) + "║")
        println("║ 4. Modificar carrito".padEnd(51) + "║")
        println("║ 5. Procesar compra".padEnd(51) + "║")
        println("║ 6. Ver estadísticas".padEnd(51) + "║")
        println("║ 0. Salir".padEnd(51) + "║")
        println("╠" + "═".repeat(50) + "╣")
        println("║ Carrito: $resumenCarrito".padEnd(51).take(51) + "║")
        println("╚" + "═".repeat(50) + "╝")
    }
    
    /**
     * Muestra el catálogo de productos
     */
    private fun mostrarCatalogo() {
        Logger.inicioOperacion("Mostrar catálogo")
        println("\n📦 CATÁLOGO DE PRODUCTOS")
        inventarioService.mostrarCatalogo()
        
        // Opciones adicionales
        println("\n¿Qué deseas hacer?")
        println("1. Agregar producto al carrito")
        println("2. Buscar producto")
        println("3. Volver al menú principal")
        
        val opcion = leerEntradaUsuario("Opción: ")
        when (opcion?.trim()) {
            "1" -> agregarProductoAlCarrito()
            "2" -> buscarProductos()
            "3" -> return
        }
    }
    
    /**
     * Permite buscar productos por nombre
     */
    private fun buscarProductos() {
        val termino = leerEntradaUsuario("🔍 Ingresa el término de búsqueda: ")
        if (termino.isNullOrBlank()) {
            println("❌ Término de búsqueda vacío")
            return
        }
        
        val productos = inventarioService.buscarProductosPorNombre(termino)
        if (productos.isEmpty()) {
            println("❌ No se encontraron productos con el término '$termino'")
        } else {
            println("\n🔍 RESULTADOS DE BÚSQUEDA para '$termino':")
            println("-".repeat(80))
            productos.forEach { producto ->
                println(producto.toString())
            }
            println("-".repeat(80))
            println("Se encontraron ${productos.size} productos")
        }
        
        pausar()
    }
    
    /**
     * Permite agregar uno o múltiples productos al carrito
     */
    private fun agregarProductoAlCarrito() {
        Logger.inicioOperacion("Agregar producto al carrito")
        
        // Mostrar productos disponibles
        val productosDisponibles = inventarioService.obtenerProductosDisponibles()
        if (productosDisponibles.isEmpty()) {
            println("❌ No hay productos disponibles en este momento")
            return
        }
        
        println("\n🛒 AGREGAR PRODUCTO(S) AL CARRITO")
        println("Productos disponibles:")
        println("-".repeat(80))
        productosDisponibles.forEach { producto ->
            println(producto.toString())
        }
        println("-".repeat(80))
        
        // Instrucciones para el usuario
        println("\n💡 INSTRUCCIONES:")
        println("• Para un solo producto: Ingresa el ID (ej: 5)")
        println("• Para múltiples productos: Separa los IDs con comas (ej: 1,3,5)")
        println("• Para cancelar: Ingresa 0")
        
        // Solicitar IDs de productos
        val entradaIds = leerEntradaUsuario("\nIngresa el/los ID(s) del/los producto(s): ")
        
        if (entradaIds == "0") {
            println("❌ Operación cancelada")
            return
        }
        
        // Parsear los IDs
        val idsProductos = parsearIds(entradaIds)
        if (idsProductos.isEmpty()) {
            println("❌ No se ingresaron IDs válidos")
            return
        }
        
        // Validar que todos los IDs existan
        val idsDisponibles = inventarioService.obtenerIdsProductosDisponibles()
        val idsValidos = idsProductos.filter { it in idsDisponibles }
        val idsInvalidos = idsProductos.filter { it !in idsDisponibles }
        
        if (idsInvalidos.isNotEmpty()) {
            println("⚠️  IDs no válidos (se omitirán): ${idsInvalidos.joinToString(", ")}")
        }
        
        if (idsValidos.isEmpty()) {
            println("❌ Ningún ID válido encontrado")
            return
        }
        
        // Solicitar cantidades para cada producto
        println("\n📝 CANTIDADES PARA CADA PRODUCTO:")
        var productosAgregados = 0
        
        for (id in idsValidos) {
            val producto = inventarioService.buscarProductoPorId(id)!!
            
            println("\n${producto.nombre} (ID: $id)")
            println("Precio: $${String.format("%.2f", producto.precio)} | Stock disponible: ${producto.cantidadDisponible}")
            
            val cantidadStr = leerEntradaUsuario("¿Cuántas unidades deseas? (0 para omitir): ")
            
            val validacionCantidad = ValidadorEntrada.validarEntero(
                cantidadStr, 
                minimo = 0, 
                nombreCampo = "Cantidad"
            )
            
            if (!validacionCantidad.esValido) {
                println("❌ ${validacionCantidad.mensajeError} - Se omite este producto")
                continue
            }
            
            val cantidad = validacionCantidad.valor!!
            
            if (cantidad == 0) {
                println("⏭️  Producto omitido")
                continue
            }
            
            // Agregar al carrito
            if (carrito.agregarProducto(id, cantidad)) {
                productosAgregados++
                println("✅ Agregado al carrito")
            } else {
                println("❌ No se pudo agregar (posiblemente sin stock suficiente)")
            }
        }
        
        // Resumen final
        println("\n📋 RESUMEN:")
        println("• Productos procesados: ${idsValidos.size}")
        println("• Productos agregados exitosamente: $productosAgregados")
        
        if (productosAgregados > 0) {
            Logger.info("Múltiples productos agregados al carrito: $productosAgregados de ${idsValidos.size}")
        }
        
        pausar()
    }
    
    /**
     * Parsea una cadena de IDs separados por comas
     * @param entrada String con IDs separados por comas
     * @return Lista de IDs válidos
     */
    private fun parsearIds(entrada: String?): List<Int> {
        if (entrada.isNullOrBlank()) return emptyList()
        
        val ids = mutableListOf<Int>()
        
        try {
            val partes = entrada.split(",")
            
            for (parte in partes) {
                val id = parte.trim().toIntOrNull()
                if (id != null && id > 0) {
                    ids.add(id)
                } else {
                    println("⚠️  '$parte' no es un ID válido, se omite")
                }
            }
        } catch (e: Exception) {
            Logger.error("Error al parsear IDs", e)
            println("❌ Error al procesar los IDs")
        }
        
        return ids.distinct() // Eliminar duplicados
    }
    
    /**
     * Muestra el contenido del carrito
     */
    private fun verCarrito() {
        Logger.inicioOperacion("Ver carrito")
        carrito.mostrarCarrito()
        
        if (!carrito.estaVacio()) {
            println("\n¿Qué deseas hacer?")
            println("1. Modificar carrito")
            println("2. Procesar compra")
            println("3. Vaciar carrito")
            println("4. Volver al menú")
            
            val opcion = leerEntradaUsuario("Opción: ")
            when (opcion?.trim()) {
                "1" -> modificarCarrito()
                "2" -> procesarCompra()
                "3" -> {
                    val confirmar = leerEntradaUsuario("¿Estás seguro de vaciar el carrito? (s/n): ")
                    val validacion = ValidadorEntrada.validarSiNo(confirmar)
                    if (validacion.esValido && validacion.valor == true) {
                        carrito.vaciar()
                    }
                }
                "4" -> return
            }
        } else {
            pausar()
        }
    }
    
    /**
     * Permite modificar el contenido del carrito
     */
    private fun modificarCarrito() {
        if (carrito.estaVacio()) {
            println("❌ El carrito está vacío")
            return
        }
        
        carrito.mostrarCarrito()
        
        println("\n✏️ MODIFICAR CARRITO")
        println("1. Eliminar producto")
        println("2. Cambiar cantidad")
        println("3. Volver")
        
        val opcion = leerEntradaUsuario("Opción: ")
        
        when (opcion?.trim()) {
            "1" -> eliminarProductoDelCarrito()
            "2" -> cambiarCantidadEnCarrito()
            "3" -> return
            else -> println("❌ Opción inválida")
        }
    }
    
    /**
     * Elimina un producto del carrito
     */
    private fun eliminarProductoDelCarrito() {
        val items = carrito.obtenerItems()
        val idsValidos = items.map { it.producto.id }
        
        val idProductoStr = leerEntradaUsuario("ID del producto a eliminar: ")
        val validacion = ValidadorEntrada.validarIdProducto(idProductoStr, idsValidos)
        
        if (!validacion.esValido) {
            println("❌ ${validacion.mensajeError}")
            return
        }
        
        carrito.eliminarProducto(validacion.valor!!)
        pausar()
    }
    
    /**
     * Cambia la cantidad de un producto en el carrito
     */
    private fun cambiarCantidadEnCarrito() {
        val items = carrito.obtenerItems()
        val idsValidos = items.map { it.producto.id }
        
        val idProductoStr = leerEntradaUsuario("ID del producto a modificar: ")
        val validacionId = ValidadorEntrada.validarIdProducto(idProductoStr, idsValidos)
        
        if (!validacionId.esValido) {
            println("❌ ${validacionId.mensajeError}")
            return
        }
        
        val idProducto = validacionId.valor!!
        val item = carrito.buscarItem(idProducto)!!
        
        println("Cantidad actual: ${item.cantidad}")
        println("1. Aumentar cantidad")
        println("2. Reducir cantidad")
        
        val accion = leerEntradaUsuario("Acción: ")
        val cantidadStr = leerEntradaUsuario("Cantidad: ")
        
        val validacionCantidad = ValidadorEntrada.validarEntero(
            cantidadStr, 
            minimo = 1, 
            nombreCampo = "Cantidad"
        )
        
        if (!validacionCantidad.esValido) {
            println("❌ ${validacionCantidad.mensajeError}")
            return
        }
        
        val cantidad = validacionCantidad.valor!!
        
        when (accion?.trim()) {
            "1" -> carrito.agregarProducto(idProducto, cantidad)
            "2" -> carrito.reducirCantidadProducto(idProducto, cantidad)
            else -> println("❌ Acción inválida")
        }
        
        pausar()
    }
    
    /**
     * Procesa la compra y genera la factura
     */
    private fun procesarCompra() {
        Logger.inicioOperacion("Procesar compra")
        
        if (carrito.estaVacio()) {
            println("❌ No se puede procesar una compra con el carrito vacío")
            return
        }
        
        carrito.mostrarCarrito()
        
        val confirmar = leerEntradaUsuario("\n¿Confirmas la compra? (s/n): ")
        val validacion = ValidadorEntrada.validarSiNo(confirmar)
        
        if (!validacion.esValido) {
            println("❌ ${validacion.mensajeError}")
            return
        }
        
        if (validacion.valor == false) {
            println("❌ Compra cancelada")
            return
        }
        
        // Preguntar por cargo de servicio
        val servicioStr = leerEntradaUsuario("¿Aplicar cargo por servicio (10%)? (s/n): ")
        val validacionServicio = ValidadorEntrada.validarSiNo(servicioStr)
        val aplicarServicio = validacionServicio.esValido && validacionServicio.valor == true
        
        // Procesar compra
        val factura = facturacionService.procesarCompra(carrito, inventarioService, aplicarServicio)
        
        if (factura != null) {
            facturacionService.mostrarFactura(factura)
            carrito.vaciar()
            println("\n✅ ¡Compra procesada exitosamente!")
        } else {
            println("❌ Error al procesar la compra")
        }
        
        pausar()
    }
    
    /**
     * Muestra estadísticas del inventario
     */
    private fun mostrarEstadisticas() {
        inventarioService.mostrarResumenInventario()
        pausar()
    }
    
    /**
     * Pregunta si desea continuar comprando
     */
    private fun preguntarContinuar(): Boolean {
        val respuesta = leerEntradaUsuario("\n¿Deseas realizar otra compra? (s/n): ")
        val validacion = ValidadorEntrada.validarSiNo(respuesta)
        
        return if (validacion.esValido) {
            validacion.valor!!
        } else {
            println("Se asume que no deseas continuar")
            false
        }
    }
    
    /**
     * Lee entrada del usuario de forma segura
     */
    private fun leerEntradaUsuario(mensaje: String): String? {
        return try {
            print(mensaje)
            scanner.nextLine()?.trim()
        } catch (e: Exception) {
            Logger.error("Error al leer entrada del usuario", e)
            null
        }
    }
    
    /**
     * Pausa la ejecución hasta que el usuario presione Enter
     */
    private fun pausar() {
        print("\nPresiona Enter para continuar...")
        scanner.nextLine()
    }
    
    /**
     * Muestra mensaje de despedida
     */
    private fun despedida() {
        println("\n" + "=".repeat(60))
        println("    🙏 ¡GRACIAS POR USAR TECHSTORE EL SALVADOR! 🙏")
        println("         ¡Esperamos verte pronto de nuevo!")
        println("=".repeat(60))
        Logger.info("Usuario cerró la aplicación")
    }
}