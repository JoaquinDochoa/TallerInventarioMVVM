package cl.duoc.tallerinventario.data.repository

import androidx.room.withTransaction
import cl.duoc.tallerinventario.data.local.AppDatabase
import cl.duoc.tallerinventario.data.local.Movimiento
import cl.duoc.tallerinventario.data.local.Producto


class MovimientoRepository(
    private val db: AppDatabase
) {
    private val productoDao = db.productoDao()
    private val movimientoDao = db.movimientoDao()

    val movimientos = movimientoDao.obtenerMovimientos()

    suspend fun consultarProducto(
        productoId: Int
    ): Producto? {
        return productoDao.obtenerPorId(productoId)
    }

    suspend fun registrar(
        productoId: Int,
        tipo: String,
        cantidad: Int
    ): String? {

        if (tipo !in listOf("Ingreso", "Venta", "Consumo")) {
            return "Tipo de movimiento inválido"
        }

        if (cantidad <= 0) {
            return "La cantidad debe ser mayor que cero"
        }

        return try {
            db.withTransaction {
                val producto = productoDao.obtenerPorId(productoId)
                    ?: return@withTransaction "Producto no encontrado"

                val stockAnterior = producto.stock

                val nuevoStock = when (tipo) {
                    "Ingreso" -> {
                        val resultado = stockAnterior.toLong() +
                                cantidad.toLong()
                        if (resultado > Int.MAX_VALUE) {
                            return@withTransaction "Stock fuera de rango"
                        }
                        resultado.toInt()
                    }
                    else -> {
                        if (cantidad > stockAnterior) {
                            return@withTransaction "Stock insuficiente"
                        }
                        stockAnterior - cantidad
                    }
                }

                productoDao.actualizarStock(
                    productoId,
                    nuevoStock
                )

                movimientoDao.insertar(
                    Movimiento(
                        productoId = producto.id,
                        productoNombre = producto.nombre,
                        tipo = tipo,
                        cantidad = cantidad,
                        stockAnterior = stockAnterior,
                        stockResultante = nuevoStock
                    )
                )

                null
            }
        } catch (e: Exception) {
            "No se pudo registrar el movimiento"
        }
    }
}
