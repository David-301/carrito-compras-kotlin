
/**
 * Clase que representa un producto en la tienda
 * @param id Identificador único del producto
 * @param nombre Nombre del producto
 * @param precio Precio unitario del producto
 * @param cantidadDisponible Cantidad disponible en inventario
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    var cantidadDisponible: Int
) {
    
    /**
     * Verifica si hay stock suficiente para la cantidad solicitada
     * @param cantidadSolicitada Cantidad que se quiere comprar
     * @return true si hay stock suficiente, false en caso contrario
     */
    fun tieneStockSuficiente(cantidadSolicitada: Int): Boolean {
        return cantidadDisponible >= cantidadSolicitada
    }
    
    /**
     * Reduce el stock del producto
     * @param cantidadVendida Cantidad que se vendió
     * @throws IllegalArgumentException si la cantidad es mayor al stock disponible
     */
    fun reducirStock(cantidadVendida: Int) {
        if (!tieneStockSuficiente(cantidadVendida)) {
            throw IllegalArgumentException("Stock insuficiente. Disponible: $cantidadDisponible, Solicitado: $cantidadVendida")
        }
        cantidadDisponible -= cantidadVendida
    }
    
    /**
     * Restaura el stock del producto (por ejemplo, si se cancela una compra)
     * @param cantidad Cantidad a restaurar
     */
    fun restaurarStock(cantidad: Int) {
        cantidadDisponible += cantidad
    }
    
    /**
     * Representación en texto del producto para mostrar en consola
     */
    override fun toString(): String {
        return String.format(
            "ID: %3d | %-25s | $%8.2f | Stock: %3d",
            id, nombre, precio, cantidadDisponible
        )
    }
    
    /**
     * Representación simplificada del producto
     */
    fun toStringSimple(): String {
        return "$nombre - $${"%.2f".format(precio)}"
    }
}