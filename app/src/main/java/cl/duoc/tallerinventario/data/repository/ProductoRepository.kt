
package cl.duoc.tallerinventario.data.repository

import cl.duoc.tallerinventario.data.local.Producto
import cl.duoc.tallerinventario.data.local.ProductoDao

class ProductoRepository(
    private val dao: ProductoDao
) {

    // Observar productos guardados en Room
    val productos = dao.observarProductos()

    // REGISTRAR PRODUCTO
    suspend fun registrar(producto: Producto): String? {

        val error = validarProducto(producto)
        if (error != null) return error

        val codigo = producto.codigo.trim()

        if (dao.contarCodigo(codigo) > 0) {
            return "Ya existe un producto con ese código"
        }

        dao.insertar(
            producto.copy(
                codigo = codigo,
                nombre = producto.nombre.trim(),
                categoria = producto.categoria.trim(),
                marca = producto.marca.trim(),
                ubicacion = producto.ubicacion.trim()
            )
        )

        return null
    }

    // EDITAR PRODUCTO
    suspend fun editar(producto: Producto): String? {

        val error = validarProducto(producto)
        if (error != null) return error

        val original = dao.obtenerPorId(producto.id)
            ?: return "El producto no existe"

        val codigo = producto.codigo.trim()

        // Verificar códigos duplicados
        if (
            dao.contarCodigoExcepto(
                codigo,
                producto.id
            ) > 0
        ) {
            return "Ya existe un producto con ese código"
        }

        // Mantener el stock original
        val actualizado = producto.copy(
            codigo = codigo,
            nombre = producto.nombre.trim(),
            categoria = producto.categoria.trim(),
            marca = producto.marca.trim(),
            ubicacion = producto.ubicacion.trim(),
            stock = original.stock
        )

        dao.actualizar(actualizado)

        return null
    }

    // VALIDACIONES DE NEGOCIO
    private fun validarProducto(
        producto: Producto
    ): String? {

        if (producto.codigo.isBlank()) {
            return "Ingresa un código"
        }

        if (producto.nombre.isBlank()) {
            return "Ingresa el nombre del producto"
        }

        if (producto.categoria.isBlank()) {
            return "Ingresa una categoría"
        }

        if (producto.ubicacion.isBlank()) {
            return "Ingresa su ubicación"
        }

        if (
            producto.stock < 0 ||
            producto.stockMinimo < 0
        ) {
            return "El stock no puede ser negativo"
        }

        return null
    }

    // ELIMINAR PRODUCTO
    suspend fun eliminar(producto: Producto) {
        dao.eliminar(producto)
    }
}
