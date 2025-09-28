/**
 * Servicio para manejar todas las operaciones relacionadas con el inventario
 * Incluye gestión de productos, stock y operaciones de búsqueda
 */
class InventarioService {
    
    private val productos: MutableList<Producto> = InventarioInicial.obtenerProductosIniciales()
    
    init {
        Logger.info("InventarioService inicializado con ${productos.size} productos")
    }
    
    /**
     * Obtiene todos los productos disponibles
     * @return Lista inmutable de productos
     */
    fun obtenerTodosLosProductos(): List<Producto> {
        return productos.toList()
    }
    
    /**
     * Obtiene solo los productos que tienen stock disponible
     * @return Lista de productos con stock > 0
     */
    fun obtenerProductosDisponibles(): List<Producto> {
        val productosDisponibles = productos.filter { it.cantidadDisponible > 0 }
        Logger.debug("Productos disponibles solicitados: ${productosDisponibles.size}")
        return productosDisponibles
    }
    
    /**
     * Busca un producto por su ID
     * @param id ID del producto
     * @return Producto encontrado o null
     */
    fun buscarProductoPorId(id: Int): Producto? {
        val producto = productos.find { it.id == id }
        if (producto == null) {
            Logger.warning("Producto con ID $id no encontrado")
        }
        return producto
    }
    
    /**
     * Verifica si un producto tiene stock suficiente
     * @param idProducto ID del producto
     * @param cantidadSolicitada Cantidad requerida
     * @return true si hay stock suficiente, false en caso contrario
     */
    fun verificarStock(idProducto: Int, cantidadSolicitada: Int): Boolean {
        val producto = buscarProductoPorId(idProducto)
        return producto?.tieneStockSuficiente(cantidadSolicitada) ?: false
    }
    
    /**
     * Reduce el stock de un producto (cuando se vende)
     * @param idProducto ID del producto
     * @param cantidad Cantidad a reducir
     * @throws IllegalArgumentException si no hay stock suficiente
     * @throws IllegalStateException si el producto no existe
     */
    fun reducirStock(idProducto: Int, cantidad: Int) {
        val producto = buscarProductoPorId(idProducto)
            ?: throw IllegalStateException("Producto con ID $idProducto no existe")
        
        val stockAnterior = producto.cantidadDisponible
        
        try {
            producto.reducirStock(cantidad)
            Logger.registrarCambioInventario(
                idProducto, 
                producto.nombre, 
                stockAnterior, 
                producto.cantidadDisponible
            )
            Logger.info("Stock reducido - Producto: ${producto.nombre}, Cantidad: $cantidad")
        } catch (e: IllegalArgumentException) {
            Logger.error("Error al reducir stock", e)
            throw e
        }
    }
    
    /**
     * Restaura el stock de un producto (en caso de cancelación)
     * @param idProducto ID del producto
     * @param cantidad Cantidad a restaurar
     * @throws IllegalStateException si el producto no existe
     */
    fun restaurarStock(idProducto: Int, cantidad: Int) {
        val producto = buscarProductoPorId(idProducto)
            ?: throw IllegalStateException("Producto con ID $idProducto no existe")
        
        val stockAnterior = producto.cantidadDisponible
        producto.restaurarStock(cantidad)
        
        Logger.registrarCambioInventario(
            idProducto, 
            producto.nombre, 
            stockAnterior, 
            producto.cantidadDisponible
        )
        Logger.info("Stock restaurado - Producto: ${producto.nombre}, Cantidad: $cantidad")
    }
    
    /**
     * Obtiene la lista de IDs de productos disponibles
     * @return Lista de IDs de productos con stock > 0
     */
    fun obtenerIdsProductosDisponibles(): List<Int> {
        return obtenerProductosDisponibles().map { it.id }
    }
    
    /**
     * Busca productos por nombre (búsqueda parcial)
     * @param termino Término de búsqueda
     * @return Lista de productos que contienen el término
     */
    fun buscarProductosPorNombre(termino: String): List<Producto> {
        if (termino.isBlank()) {
            Logger.warning("Búsqueda con término vacío")
            return emptyList()
        }
        
        val resultados = productos.filter { 
            it.nombre.lowercase().contains(termino.trim().lowercase()) 
        }
        
        Logger.info("Búsqueda por nombre '$termino': ${resultados.size} resultados")
        return resultados
    }
    
    /**
     * Filtra productos por rango de precio
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de productos en el rango
     */
    fun filtrarPorRangoPrecio(precioMin: Double, precioMax: Double): List<Producto> {
        if (precioMin < 0 || precioMax < 0 || precioMin > precioMax) {
            Logger.warning("Rango de precio inválido: $precioMin - $precioMax")
            return emptyList()
        }
        
        val resultados = productos.filter { 
            it.precio >= precioMin && it.precio <= precioMax 
        }
        
        Logger.info("Filtro por precio $precioMin - $precioMax: ${resultados.size} resultados")
        return resultados
    }
    
    /**
     * Muestra el catálogo completo de productos
     */
    fun mostrarCatalogo() {
        val productosDisponibles = obtenerProductosDisponibles()
        
        if (productosDisponibles.isEmpty()) {
            println("\n❌ No hay productos disponibles en este momento.")
            Logger.warning("Catálogo mostrado pero no hay productos disponibles")
            return
        }
        
        println("\n" + "=".repeat(80))
        println("                           CATÁLOGO DE PRODUCTOS")
        println("=".repeat(80))
        println("%-4s %-30s %-12s %-8s".format("ID", "PRODUCTO", "PRECIO", "STOCK"))
        println("-".repeat(80))
        
        productosDisponibles.forEach { producto ->
            println(producto.toString())
        }
        
        println("-".repeat(80))
        println("Total de productos disponibles: ${productosDisponibles.size}")
        println("=".repeat(80))
        
        Logger.info("Catálogo mostrado con ${productosDisponibles.size} productos")
    }
    
    /**
     * Muestra un resumen del inventario
     */
    fun mostrarResumenInventario() {
        val totalProductos = productos.size
        val productosDisponibles = obtenerProductosDisponibles().size
        val productosSinStock = totalProductos - productosDisponibles
        val valorTotal = productos.sumOf { it.precio * it.cantidadDisponible }
        
        println("\n" + "=".repeat(50))
        println("           RESUMEN DEL INVENTARIO")
        println("=".repeat(50))
        println("Total de productos:     $totalProductos")
        println("Productos disponibles:  $productosDisponibles")
        println("Productos agotados:     $productosSinStock")
        println("Valor total:           $${"%.2f".format(valorTotal)}")
        println("=".repeat(50))
        
        Logger.info("Resumen de inventario mostrado")
    }
    
    /**
     * Valida si se puede procesar una lista de items del carrito
     * @param items Lista de items a validar
     * @return Pair<Boolean, String> - (esValido, mensajeError)
     */
    fun validarDisponibilidadItems(items: List<ItemCarrito>): Pair<Boolean, String> {
        for (item in items) {
            val producto = buscarProductoPorId(item.producto.id)
            if (producto == null) {
                val mensaje = "El producto '${item.producto.nombre}' ya no existe"
                Logger.error(mensaje)
                return Pair(false, mensaje)
            }
            
            if (!producto.tieneStockSuficiente(item.cantidad)) {
                val mensaje = "Stock insuficiente para '${producto.nombre}'. " +
                        "Disponible: ${producto.cantidadDisponible}, Solicitado: ${item.cantidad}"
                Logger.warning(mensaje)
                return Pair(false, mensaje)
            }
        }
        
        return Pair(true, "")
    }
    
    /**
     * Obtiene estadísticas detalladas del inventario
     * @return Map con las estadísticas
     */
    fun obtenerEstadisticas(): Map<String, Any> {
        val stats = mapOf(
            "totalProductos" to productos.size,
            "productosDisponibles" to obtenerProductosDisponibles().size,
            "valorTotalInventario" to productos.sumOf { it.precio * it.cantidadDisponible },
            "precioPromedio" to productos.map { it.precio }.average(),
            "stockTotal" to productos.sumOf { it.cantidadDisponible }
        )
        
        Logger.debug("Estadísticas del inventario generadas")
        return stats
    }
}