/**
 * Utilidad para validar y procesar entradas del usuario
 * Previene errores y asegura que los datos sean válidos
 */
object ValidadorEntrada {
    
    /**
     * Resultado de una validación
     */
    data class ResultadoValidacion<T>(
        val esValido: Boolean,
        val valor: T?,
        val mensajeError: String = ""
    )
    
    /**
     * Valida que una cadena no esté vacía ni sea solo espacios
     * @param entrada La cadena a validar
     * @param nombreCampo Nombre del campo para el mensaje de error
     * @return ResultadoValidacion con la cadena limpia o error
     */
    fun validarTextoNoVacio(entrada: String?, nombreCampo: String = "Campo"): ResultadoValidacion<String> {
        return when {
            entrada.isNullOrBlank() -> {
                Logger.warning("Validación fallida: $nombreCampo está vacío")
                ResultadoValidacion(false, null, "$nombreCampo no puede estar vacío")
            }
            entrada.trim().isEmpty() -> {
                Logger.warning("Validación fallida: $nombreCampo solo contiene espacios")
                ResultadoValidacion(false, null, "$nombreCampo no puede ser solo espacios")
            }
            else -> {
                ResultadoValidacion(true, entrada.trim())
            }
        }
    }
    
    /**
     * Valida y convierte una cadena a entero
     * @param entrada La cadena a convertir
     * @param minimo Valor mínimo permitido (opcional)
     * @param maximo Valor máximo permitido (opcional)
     * @param nombreCampo Nombre del campo para mensajes de error
     * @return ResultadoValidacion con el entero o error
     */
    fun validarEntero(
        entrada: String?, 
        minimo: Int? = null, 
        maximo: Int? = null,
        nombreCampo: String = "Número"
    ): ResultadoValidacion<Int> {
        
        if (entrada.isNullOrBlank()) {
            Logger.warning("Validación fallida: $nombreCampo está vacío")
            return ResultadoValidacion(false, null, "$nombreCampo no puede estar vacío")
        }
        
        val numero = try {
            entrada.trim().toInt()
        } catch (e: NumberFormatException) {
            Logger.warning("Validación fallida: '$entrada' no es un número válido")
            return ResultadoValidacion(false, null, "'$entrada' no es un número válido")
        }
        
        return when {
            minimo != null && numero < minimo -> {
                Logger.warning("Validación fallida: $numero es menor que el mínimo ($minimo)")
                ResultadoValidacion(false, null, "$nombreCampo debe ser mayor o igual a $minimo")
            }
            maximo != null && numero > maximo -> {
                Logger.warning("Validación fallida: $numero es mayor que el máximo ($maximo)")
                ResultadoValidacion(false, null, "$nombreCampo debe ser menor o igual a $maximo")
            }
            else -> {
                ResultadoValidacion(true, numero)
            }
        }
    }
    
    /**
     * Valida y convierte una cadena a double (para precios, etc.)
     * @param entrada La cadena a convertir
     * @param minimo Valor mínimo permitido (opcional)
     * @param maximo Valor máximo permitido (opcional)
     * @param nombreCampo Nombre del campo para mensajes de error
     * @return ResultadoValidacion con el double o error
     */
    fun validarDecimal(
        entrada: String?, 
        minimo: Double? = null, 
        maximo: Double? = null,
        nombreCampo: String = "Número decimal"
    ): ResultadoValidacion<Double> {
        
        if (entrada.isNullOrBlank()) {
            Logger.warning("Validación fallida: $nombreCampo está vacío")
            return ResultadoValidacion(false, null, "$nombreCampo no puede estar vacío")
        }
        
        val numero = try {
            entrada.trim().toDouble()
        } catch (e: NumberFormatException) {
            Logger.warning("Validación fallida: '$entrada' no es un decimal válido")
            return ResultadoValidacion(false, null, "'$entrada' no es un número decimal válido")
        }
        
        return when {
            minimo != null && numero < minimo -> {
                Logger.warning("Validación fallida: $numero es menor que el mínimo ($minimo)")
                ResultadoValidacion(false, null, "$nombreCampo debe ser mayor o igual a $minimo")
            }
            maximo != null && numero > maximo -> {
                Logger.warning("Validación fallida: $numero es mayor que el máximo ($maximo)")
                ResultadoValidacion(false, null, "$nombreCampo debe ser menor o igual a $maximo")
            }
            else -> {
                ResultadoValidacion(true, numero)
            }
        }
    }
    
    /**
     * Valida una opción de menú
     * @param entrada La opción ingresada
     * @param opcionesValidas Lista de opciones válidas
     * @return ResultadoValidacion con la opción o error
     */
    fun validarOpcionMenu(entrada: String?, opcionesValidas: List<String>): ResultadoValidacion<String> {
        if (entrada.isNullOrBlank()) {
            return ResultadoValidacion(false, null, "Debe seleccionar una opción")
        }
        
        val opcionLimpia = entrada.trim().lowercase()
        val opcionesValidasLower = opcionesValidas.map { it.lowercase() }
        
        return if (opcionLimpia in opcionesValidasLower) {
            val indice = opcionesValidasLower.indexOf(opcionLimpia)
            ResultadoValidacion(true, opcionesValidas[indice])
        } else {
            Logger.warning("Opción inválida seleccionada: '$entrada'")
            ResultadoValidacion(
                false, 
                null, 
                "Opción inválida. Opciones válidas: ${opcionesValidas.joinToString(", ")}"
            )
        }
    }
    
    /**
     * Valida un ID de producto
     * @param entrada El ID ingresado
     * @param idsValidos Lista de IDs válidos
     * @return ResultadoValidacion con el ID o error
     */
    fun validarIdProducto(entrada: String?, idsValidos: List<Int>): ResultadoValidacion<Int> {
        val resultadoEntero = validarEntero(entrada, nombreCampo = "ID del producto")
        
        if (!resultadoEntero.esValido) {
            return resultadoEntero
        }
        
        val id = resultadoEntero.valor!!
        
        return if (id in idsValidos) {
            ResultadoValidacion(true, id)
        } else {
            Logger.warning("ID de producto inválido: $id")
            ResultadoValidacion(false, null, "ID de producto no válido. IDs disponibles: ${idsValidos.joinToString(", ")}")
        }
    }
    
    /**
     * Valida una respuesta Sí/No
     * @param entrada La respuesta del usuario
     * @return ResultadoValidacion con boolean o error
     */
    fun validarSiNo(entrada: String?): ResultadoValidacion<Boolean> {
        if (entrada.isNullOrBlank()) {
            return ResultadoValidacion(false, null, "Debe responder sí o no")
        }
        
        val respuesta = entrada.trim().lowercase()
        
        return when (respuesta) {
            "s", "si", "sí", "y", "yes" -> ResultadoValidacion(true, true)
            "n", "no" -> ResultadoValidacion(true, false)
            else -> {
                Logger.warning("Respuesta Sí/No inválida: '$entrada'")
                ResultadoValidacion(false, null, "Responda con 's' para sí o 'n' para no")
            }
        }
    }
    
    /**
     * Sanitiza una cadena removiendo caracteres especiales peligrosos
     * @param entrada La cadena a sanitizar
     * @return La cadena sanitizada
     */
    fun sanitizarTexto(entrada: String): String {
        return entrada.trim()
            .replace(Regex("[<>\"'&]"), "") // Remover caracteres potencialmente peligrosos
            .take(100) // Limitar longitud
    }
}