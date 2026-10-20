package cl.duoc.tallerinventario.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.tallerinventario.data.local.Movimiento
import cl.duoc.tallerinventario.data.repository.MovimientoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AlertaStock(
    val productoId: Int,
    val nombre: String,
    val stock: Int,
    val minimo: Int
)

class MovimientoViewModel(
    private val repository: MovimientoRepository
) : ViewModel() {

    val movimientos: StateFlow<List<Movimiento>> =
        repository.movimientos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _alertas = MutableSharedFlow<AlertaStock>()
    val alertas = _alertas.asSharedFlow()

    fun registrar(
        productoId: Int,
        tipo: String,
        cantidadTexto: String
    ) {
        val cantidad = cantidadTexto.toIntOrNull()

        if (cantidad == null || cantidad <= 0) {
            _mensaje.value = "Ingresa una cantidad válida"
            return
        }

        if (_guardando.value) return

        _guardando.value = true

        viewModelScope.launch {
            try {
                val error = repository.registrar(
                    productoId,
                    tipo,
                    cantidad
                )

                if (error != null) {
                    _mensaje.value = error
                    return@launch
                }

                _mensaje.value = "Movimiento registrado"

                // Solo verificar alertas cuando sale stock
                if (tipo == "Venta" || tipo == "Consumo") {
                    val producto = repository.consultarProducto(
                        productoId
                    )

                    if (
                        producto != null &&
                        producto.stock <= producto.stockMinimo
                    ) {
                        _alertas.emit(
                            AlertaStock(
                                productoId = producto.id,
                                nombre = producto.nombre,
                                stock = producto.stock,
                                minimo = producto.stockMinimo
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo completar la operación"
            } finally {
                _guardando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    class Factory(
        private val repository: MovimientoRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T = MovimientoViewModel(repository) as T
    }
}
