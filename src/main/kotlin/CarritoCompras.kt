/**
 * Clase principal que maneja el carrito de compras
 * Permite agregar, eliminar y gestionar productos seleccionados
 */
class CarritoCompras {
    
    private val items: MutableList<ItemCarrito> = mutableListOf()
    private val inventarioService = InventarioService()
    
    init {
        Logger.info("Nuevo carrito de compras creado")
    }
    
    /**
     * Agrega un producto al carrito
     * @param idProducto ID del producto a agregar
     * @param cantidad Cantidad a agregar
     * @return true si se agregó exitosamente, false en caso contrario
     */
    fun agregarProducto(idProducto: Int, cantidad: Int): Boolean {
        try {
            // Validar entrada
            if (cantidad <= 0) {
                Logger.warning("Intento de agregar cantidad inválida: $cantidad")
                println("❌ La cantidad debe ser mayor a 0")
                return false
            }
            
            // Buscar el producto
            val producto = inventarioService.buscarProductoPorId(idProducto)
            if (producto == null) {
                Logger.warning("Intento de agregar producto inexistente: ID $idProducto")
                println("❌ Producto no encontrado")
                return false
            }
            
            // Verificar stock total (incluyendo lo que ya está en el carrito)
            val cantidadEnCarrito = obtenerCantidadEnCarrito(idProducto)
            val cantidadTotalSolicitada = cantidadEnCarrito + cantidad
            
            if (!producto.tieneStockSuficiente(cantidadTotalSolicitada)) {
                Logger.warning("Stock insuficiente para ${producto.nombre}. Disponible: ${producto.cantidadDisponible}, En carrito: $cantidadEnCarrito, Solicitado adicional: $cantidad")
                println("❌ Stock insuficiente. Disponible: ${producto.cantidadDisponible}, ya tienes $cantidadEnCarrito en el carrito")
                return false
            }
            
            // Buscar si el producto ya está en el carrito
            val itemExistente = items.find { it.producto.id == idProducto }
            
            if (itemExistente != null) {
                // Si ya existe, aumentar la cantidad
                itemExistente.aumentarCantidad(cantidad)
                Logger.info("Cantidad aumentada en carrito - ${producto.nombre}: +$cantidad (Total: ${itemExistente.cantidad})")
                println("✅ Se agregaron $cantidad unidades más de '${producto.nombre}' al carrito")
            } else {
                // Si no existe, crear nuevo item
                val nuevoItem = ItemCarrito(producto, cantidad)
                items.add(nuevoItem)
                Logger.info("Producto agregado al carrito - ${producto.nombre}: $cantidad unidades")
                println("✅ '${producto.nombre}' agregado al carrito ($cantidad unidades)")
            }
            
            return true
            
        } catch (e: Exception) {
            Logger.error("Error al agregar producto al carrito", e)
            println("❌ Error interno al agregar el producto")
            return false
        }
    }
    
    /**
     * Elimina un producto completamente del carrito
     * @param idProducto ID del producto a eliminar
     * @return true si se eliminó exitosamente, false si no se encontró
     */
    fun eliminarProducto(idProducto: Int): Boolean {
        val item = items.find { it.producto.id == idProducto }
        
        return if (item != null) {
            items.remove(item)
            Logger.info("Producto eliminado del carrito - ${item.producto.nombre}")
            println("✅ '${item.producto.nombre}' eliminado del carrito")
            true
        } else {
            Logger.warning("Intento de eliminar producto que no está en el carrito: ID $idProducto")
            println("❌ El producto no está en el carrito")
            false
        }
    }
    
    /**
     * Reduce la cantidad de un producto en el carrito
     * @param idProducto ID del producto
     * @param cantidad Cantidad a reducir
     * @return true si se redujo exitosamente, false en caso contrario
     */
    fun reducirCantidadProducto(idProducto: Int, cantidad: Int): Boolean {
        if (cantidad <= 0) {
            println("❌ La cantidad a reducir debe ser mayor a 0")
            return false
        }
        
        val item = items.find { it.producto.id == idProducto }
        if (item == null) {
            println("❌ El producto no está en el carrito")
            return false
        }
        
        return try {
            if (cantidad >= item.cantidad) {
                // Si se quiere reducir toda la cantidad o más, eliminar el item
                eliminarProducto(idProducto)
            } else {
                // Reducir la cantidad
                item.reducirCantidad(cantidad)
                Logger.info("Cantidad reducida en carrito - ${item.producto.nombre}: -$cantidad (Quedan: ${item.cantidad})")
                println("✅ Se redujeron $cantidad unidades de '${item.producto.nombre}' (Quedan: ${item.cantidad})")
            }
            true
        } catch (e: Exception) {
            Logger.error("Error al reducir cantidad en carrito", e)
            println("❌ Error al reducir la cantidad")
            false
        }
    }
    
    /**
     * Obtiene la cantidad de un producto específico en el carrito
     * @param idProducto ID del producto
     * @return Cantidad en el carrito, 0 si no está
     */
    fun obtenerCantidadEnCarrito(idProducto: Int): Int {
        return items.find { it.producto.id == idProducto }?.cantidad ?: 0
    }
    
    /**
     * Calcula el subtotal del carrito (sin impuestos)
     * @return Subtotal del carrito
     */
    fun calcularSubtotal(): Double {
        return items.sumOf { it.calcularSubtotal() }
    }
    
    /**
     * Obtiene una copia inmutable de los items del carrito
     * @return Lista de items en el carrito
     */
    fun obtenerItems(): List<ItemCarrito> {
        return items.toList()
    }
    
    /**
     * Verifica si el carrito está vacío
     * @return true si está vacío, false en caso contrario
     */
    fun estaVacio(): Boolean {
        return items.isEmpty()
    }
    
    /**
     * Obtiene el número total de items únicos en el carrito
     * @return Número de productos diferentes
     */
    fun obtenerNumeroItems(): Int {
        return items.size
    }
    
    /**
     * Obtiene la cantidad total de unidades en el carrito
     * @return Suma de todas las cantidades
     */
    fun obtenerCantidadTotalUnidades(): Int {
        return items.sumOf { it.cantidad }
    }
    
    /**
     * Vacía completamente el carrito
     */
    fun vaciar() {
        val cantidadItems = items.size
        items.clear()
        Logger.info("Carrito vaciado - Se eliminaron $cantidadItems items")
        println("✅ Carrito vaciado")
    }
    
    /**
     * Muestra el contenido actual del carrito
     */
    /*fun mostrarCarrito() {
        if (estaVacio()) {
            println("\n🛒 Tu carrito está vacío")
            return
        }
        
        val subtotal = calcularSubtotal()
        
        println("\n" + "=".repeat(70))
        println("                        TU CARRITO DE COMPRAS")
        println("=".repeat(70))
        
        items.forEachIndexed { index, item ->
            println("${index + 1}. ${item}")
        }
        
        println("-".repeat(70))
        println("Total de items: ${obtenerNumeroItems()} productos (${obtenerCantidadTotalUnidades()} unidades)")
        println("SUBTOTAL: $${"%.2f".format(subtotal)}")
        println("=".repeat(70))
        
        Logger.debug("Carrito mostrado - ${obtenerNumeroItems()} items, subtotal: $${"%.2f".format(subtotal)}")
    }*/
    /**
 * Muestra el contenido actual del carrito
 */
fun mostrarCarrito() {
    if (estaVacio()) {
        println("\n🛒 Tu carrito está vacío")
        return
    }
    
    val subtotal = calcularSubtotal()
    
    println("\n" + "=".repeat(70))
    println("                        TU CARRITO DE COMPRAS")
    println("=".repeat(70))
    
    items.forEachIndexed { index, item ->
        println("${index + 1}. ID: ${item.producto.id} | ${item.producto.nombre} | Cant: ${item.cantidad} | $${String.format("%.2f", item.producto.precio)} c/u | Subtotal: $${String.format("%.2f", item.calcularSubtotal())}")
    }
    
    println("-".repeat(70))
    println("Total de items: ${obtenerNumeroItems()} productos (${obtenerCantidadTotalUnidades()} unidades)")
    println("SUBTOTAL: $${"%.2f".format(subtotal)}")
    println("=".repeat(70))
    
    Logger.debug("Carrito mostrado - ${obtenerNumeroItems()} items, subtotal: $${"%.2f".format(subtotal)}")
}
    
    /**
     * Valida que todos los productos en el carrito tengan stock suficiente
     * @return Pair<Boolean, String> - (esValido, mensajeError)
     */
    fun validarDisponibilidad(): Pair<Boolean, String> {
        if (estaVacio()) {
            return Pair(false, "El carrito está vacío")
        }
        
        return inventarioService.validarDisponibilidadItems(items)
    }
    
    /**
     * Busca un item en el carrito por ID de producto
     * @param idProducto ID del producto a buscar
     * @return ItemCarrito encontrado o null
     */
    fun buscarItem(idProducto: Int): ItemCarrito? {
        return items.find { it.producto.id == idProducto }
    }
    
    /**
     * Obtiene un resumen rápido del carrito
     * @return String con resumen del carrito
     */
    fun obtenerResumen(): String {
        return if (estaVacio()) {
            "Carrito vacío"
        } else {
            "${obtenerNumeroItems()} productos (${obtenerCantidadTotalUnidades()} unidades) - ${"%.2f".format(calcularSubtotal())}"
        }
    }
    
    /**
     * Actualiza las referencias de productos en el carrito con los datos más recientes del inventario
     * (Útil para refrescar precios o información de productos)
     */
    fun actualizarReferenciaProductos() {
        items.forEach { item ->
            val productoActualizado = inventarioService.buscarProductoPorId(item.producto.id)
            if (productoActualizado != null) {
                // Aquí podrías actualizar la referencia si fuera necesario
                // Por ahora, solo registramos que se verificó
                Logger.debug("Referencia verificada para producto: ${item.producto.nombre}")
            }
        }
    }
}