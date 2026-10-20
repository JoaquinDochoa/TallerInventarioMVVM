package cl.duoc.tallerinventario.ui

data class ProductoErrores(
    val codigo: String? = null,
    val nombre: String? = null,
    val categoria: String? = null,
    val ubicacion: String? = null,
    val stock: String? = null,
    val minimo: String? = null
)

data class ProductoUiState(
    val codigo: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val marca: String = "",
    val ubicacion: String = "",
    val stock: String = "",
    val minimo: String = "",
    val errores: ProductoErrores = ProductoErrores(),
    val guardando: Boolean = false
)
