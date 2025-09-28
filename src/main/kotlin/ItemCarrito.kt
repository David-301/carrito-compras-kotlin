
/**
 * Clase que representa un item dentro del carrito de compras
 * @param producto El producto seleccionado
 * @param cantidad Cantidad seleccionada de este producto
 */
data class ItemCarrito(
    val producto: Producto,
    var cantidad: Int
) {
    
    /**
     * Calcula el subtotal para este item (precio * cantidad)
     * @return El subtotal del item
     */
    fun calcularSubtotal(): Double {
        return producto.precio * cantidad
    }
    
    /**
     * Aumenta la cantidad del item
     * @param cantidadAdicional Cantidad a agregar
     */
    fun aumentarCantidad(cantidadAdicional: Int) {
        if (cantidadAdicional <= 0) {
            throw IllegalArgumentException("La cantidad adicional debe ser mayor a 0")
        }
        cantidad += cantidadAdicional
    }
    
    /**
     * Reduce la cantidad del item
     * @param cantidadAReducir Cantidad a reducir
     * @throws IllegalArgumentException si se intenta reducir más de lo que hay
     */
    fun reducirCantidad(cantidadAReducir: Int) {
        if (cantidadAReducir <= 0) {
            throw IllegalArgumentException("La cantidad a reducir debe ser mayor a 0")
        }
        if (cantidadAReducir >= cantidad) {
            throw IllegalArgumentException("No se puede reducir más cantidad de la que existe en el carrito")
        }
        cantidad -= cantidadAReducir
    }
    
    /**
     * Verifica si la cantidad solicitada está disponible en el inventario
     * @return true si hay stock suficiente, false en caso contrario
     */
    fun esValidaCantidad(): Boolean {
        return producto.tieneStockSuficiente(cantidad)
    }
    
    /**
     * Representación detallada del item para mostrar en el carrito
     */
    override fun toString(): String {
        return String.format(
            "%-25s | Cant: %2d | $%8.2f c/u | Subtotal: $%10.2f",
            producto.nombre, 
            cantidad, 
            producto.precio, 
            calcularSubtotal()
        )
    }
    
    /**
     * Representación para la factura
     */
    fun toStringFactura(): String {
        return String.format(
            "%-25s %2d x $%8.2f = $%10.2f",
            producto.nombre,
            cantidad,
            producto.precio,
            calcularSubtotal()
        )
    }
    
    /**
     * Obtiene información resumida del item
     */
    fun getResumen(): String {
        return "${cantidad}x ${producto.nombre} - ${"%.2f".format(calcularSubtotal())}"
    }
}