
package cl.duoc.tallerinventario.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.tallerinventario.data.local.Producto
import cl.duoc.tallerinventario.data.repository.ProductoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    val productos: StateFlow<List<Producto>> =
        repository.productos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _estado = MutableStateFlow(ProductoUiState())
    val estado: StateFlow<ProductoUiState> = _estado.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    // Producto que se está editando
    private var productoEnEdicion: Producto? = null

    private val _editando = MutableStateFlow(false)
    val editando: StateFlow<Boolean> = _editando.asStateFlow()

    // ACTUALIZAR CAMPOS

    fun onCodigoChange(valor: String) {
        _estado.update {
            it.copy(
                codigo = valor,
                errores = it.errores.copy(codigo = null)
            )
        }
        limpiarMensaje()
    }

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null)
            )
        }
        limpiarMensaje()
    }

    fun onCategoriaChange(valor: String) {
        _estado.update {
            it.copy(
                categoria = valor,
                errores = it.errores.copy(categoria = null)
            )
        }
        limpiarMensaje()
    }

    fun onMarcaChange(valor: String) {
        _estado.update {
            it.copy(marca = valor)
        }
        limpiarMensaje()
    }

    fun onUbicacionChange(valor: String) {
        _estado.update {
            it.copy(
                ubicacion = valor,
                errores = it.errores.copy(ubicacion = null)
            )
        }
        limpiarMensaje()
    }

    fun onStockChange(valor: String) {
        if (_editando.value) return

        _estado.update {
            it.copy(
                stock = valor,
                errores = it.errores.copy(stock = null)
            )
        }
        limpiarMensaje()
    }

    fun onMinimoChange(valor: String) {
        _estado.update {
            it.copy(
                minimo = valor,
                errores = it.errores.copy(minimo = null)
            )
        }
        limpiarMensaje()
    }

    // PREPARAR FORMULARIO PARA EDICIÓN

    fun cargarProducto(producto: Producto) {

        productoEnEdicion = producto
        _editando.value = true
        _guardado.value = false
        _mensaje.value = null

        _estado.value = ProductoUiState(
            codigo = producto.codigo,
            nombre = producto.nombre,
            categoria = producto.categoria,
            marca = producto.marca,
            ubicacion = producto.ubicacion,
            stock = producto.stock.toString(),
            minimo = producto.stockMinimo.toString()
        )
    }

    // VALIDAR FORMULARIO

    private fun validarFormulario(): Boolean {

        val datos = _estado.value
        val stock = datos.stock.toIntOrNull()
        val minimo = datos.minimo.toIntOrNull()

        val errores = ProductoErrores(
            codigo = if (datos.codigo.isBlank())
                "El código es obligatorio" else null,

            nombre = if (datos.nombre.isBlank())
                "El nombre es obligatorio" else null,

            categoria = if (datos.categoria.isBlank())
                "La categoría es obligatoria" else null,

            ubicacion = if (datos.ubicacion.isBlank())
                "La ubicación es obligatoria" else null,

            stock = when {
                stock == null -> "Ingresa un número válido"
                stock < 0 -> "El stock no puede ser negativo"
                else -> null
            },

            minimo = when {
                minimo == null -> "Ingresa un número válido"
                minimo < 0 -> "El mínimo no puede ser negativo"
                else -> null
            }
        )

        _estado.update {
            it.copy(errores = errores)
        }

        return errores == ProductoErrores()
    }

    // GUARDAR O EDITAR PRODUCTO

    fun guardarProducto() {

        if (_estado.value.guardando) return
        if (!validarFormulario()) return

        val datos = _estado.value
        val original = productoEnEdicion

        _estado.update {
            it.copy(guardando = true)
        }

        viewModelScope.launch {
            try {

                val producto = Producto(
                    id = original?.id ?: 0,
                    codigo = datos.codigo.trim(),
                    nombre = datos.nombre.trim(),
                    categoria = datos.categoria.trim(),
                    marca = datos.marca.trim(),
                    ubicacion = datos.ubicacion.trim(),

                    // Conservar existencias en edición
                    stock = original?.stock
                        ?: datos.stock.toInt(),

                    stockMinimo = datos.minimo.toInt()
                )

                val error = if (original != null) {
                    repository.editar(producto)
                } else {
                    repository.registrar(producto)
                }

                if (error == null) {
                    _guardado.value = true
                    _mensaje.value = null
                } else {
                    _mensaje.value = error

                    if (
                        error.contains(
                            "Ya existe un producto",
                            ignoreCase = true
                        )
                    ) {
                        _estado.update {
                            it.copy(
                                errores = it.errores.copy(
                                    codigo = error
                                )
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo guardar el producto"
            } finally {
                _estado.update {
                    it.copy(guardando = false)
                }
            }
        }
    }

    // ELIMINAR PRODUCTO

    fun eliminar(producto: Producto) {
        viewModelScope.launch {
            try {
                repository.eliminar(producto)
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo eliminar el producto"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    // REINICIAR PARA REGISTRAR OTRO PRODUCTO

    fun limpiarFormulario() {
        productoEnEdicion = null
        _editando.value = false
        _estado.value = ProductoUiState()
        _mensaje.value = null
        _guardado.value = false
    }

    fun confirmarNavegacion() {
        _guardado.value = false
    }

    class Factory(
        private val repository: ProductoRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T = ProductoViewModel(repository) as T
    }
}
