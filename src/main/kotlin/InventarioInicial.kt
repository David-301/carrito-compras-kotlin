/**
 * Datos iniciales del inventario de la tienda
 * Contiene todos los productos disponibles para la venta
 */
object InventarioInicial {
    
    /**
     * Lista de productos disponibles en la tienda
     * Puedes cambiar estos productos por los que quieras (electrónicos, ropa, comida, etc.)
     */
    fun obtenerProductosIniciales(): MutableList<Producto> {
        return mutableListOf(
            // CATEGORÍA: ELECTRÓNICOS
            Producto(
                id = 1,
                nombre = "Smartphone Samsung Galaxy",
                precio = 899.99,
                cantidadDisponible = 15
            ),
            Producto(
                id = 2,
                nombre = "Laptop HP Pavilion",
                precio = 1299.99,
                cantidadDisponible = 8
            ),
            Producto(
                id = 3,
                nombre = "Auriculares Bluetooth Sony",
                precio = 199.99,
                cantidadDisponible = 25
            ),
            Producto(
                id = 4,
                nombre = "Tablet iPad Air",
                precio = 749.99,
                cantidadDisponible = 12
            ),
            Producto(
                id = 5,
                nombre = "Monitor Gaming 27 pulgadas",
                precio = 449.99,
                cantidadDisponible = 10
            ),
            
            // CATEGORÍA: ACCESORIOS
            Producto(
                id = 6,
                nombre = "Teclado Mecánico RGB",
                precio = 129.99,
                cantidadDisponible = 20
            ),
            Producto(
                id = 7,
                nombre = "Mouse Gaming Inalámbrico",
                precio = 89.99,
                cantidadDisponible = 30
            ),
            Producto(
                id = 8,
                nombre = "Webcam HD 1080p",
                precio = 79.99,
                cantidadDisponible = 18
            ),
            Producto(
                id = 9,
                nombre = "Cargador Portátil 20000mAh",
                precio = 39.99,
                cantidadDisponible = 35
            ),
            Producto(
                id = 10,
                nombre = "Cable USB-C 2 metros",
                precio = 19.99,
                cantidadDisponible = 50
            ),
            
            // CATEGORÍA: GAMING
            Producto(
                id = 11,
                nombre = "Control Xbox Wireless",
                precio = 69.99,
                cantidadDisponible = 22
            ),
            Producto(
                id = 12,
                nombre = "Tarjeta Gráfica RTX 4060",
                precio = 599.99,
                cantidadDisponible = 5
            ),
            Producto(
                id = 13,
                nombre = "Silla Gaming Ergonómica",
                precio = 299.99,
                cantidadDisponible = 7
            ),
            Producto(
                id = 14,
                nombre = "Micrófono USB Streaming",
                precio = 149.99,
                cantidadDisponible = 15
            ),
            Producto(
                id = 15,
                nombre = "Mousepad Gaming XL",
                precio = 29.99,
                cantidadDisponible = 40
            )
        )
    }
    
    /**
     * Obtiene un producto por su ID
     * @param id ID del producto a buscar
     * @param productos Lista de productos donde buscar
     * @return El producto encontrado o null
     */
    fun obtenerProductoPorId(id: Int, productos: List<Producto>): Producto? {
        return productos.find { it.id == id }
    }
    
    /**
     * Obtiene todos los IDs de productos disponibles
     * @param productos Lista de productos
     * @return Lista de IDs
     */
    fun obtenerIdsDisponibles(productos: List<Producto>): List<Int> {
        return productos.map { it.id }
    }
    
    /**
     * Filtra productos que tienen stock disponible
     * @param productos Lista de productos
     * @return Lista de productos con stock > 0
     */
    fun obtenerProductosConStock(productos: List<Producto>): List<Producto> {
        return productos.filter { it.cantidadDisponible > 0 }
    }
    
    /**
     * Busca productos por nombre (búsqueda parcial, no sensible a mayúsculas)
     * @param productos Lista de productos
     * @param termino Término de búsqueda
     * @return Lista de productos que coinciden
     */
    fun buscarProductosPorNombre(productos: List<Producto>, termino: String): List<Producto> {
        return productos.filter { 
            it.nombre.lowercase().contains(termino.lowercase()) 
        }
    }
    
    /**
     * Obtiene productos dentro de un rango de precios
     * @param productos Lista de productos
     * @param precioMinimo Precio mínimo
     * @param precioMaximo Precio máximo
     * @return Lista de productos en el rango
     */
    fun obtenerProductosPorRangoPrecio(
        productos: List<Producto>, 
        precioMinimo: Double, 
        precioMaximo: Double
    ): List<Producto> {
        return productos.filter { 
            it.precio >= precioMinimo && it.precio <= precioMaximo 
        }
    }
    
    /**
     * Muestra estadísticas del inventario
     * @param productos Lista de productos
     */
    fun mostrarEstadisticasInventario(productos: List<Producto>) {
        val totalProductos = productos.size
        val productosConStock = obtenerProductosConStock(productos).size
        val productosSinStock = totalProductos - productosConStock
        val valorTotalInventario = productos.sumOf { it.precio * it.cantidadDisponible }
        val precioPromedio = productos.map { it.precio }.average()
        
        println("\n" + "=".repeat(50))
        println("           ESTADÍSTICAS DEL INVENTARIO")
        println("=".repeat(50))
        println("Total de productos:        $totalProductos")
        println("Productos con stock:       $productosConStock")
        println("Productos sin stock:       $productosSinStock")
        println("Valor total inventario:    $${"%.2f".format(valorTotalInventario)}")
        println("Precio promedio:           $${"%.2f".format(precioPromedio)}")
        println("=".repeat(50))
        
        Logger.info("Estadísticas mostradas - Total productos: $totalProductos, Con stock: $productosConStock")
    }
}