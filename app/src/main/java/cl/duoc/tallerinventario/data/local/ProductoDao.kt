
package cl.duoc.tallerinventario.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Mostrar todos los productos
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun observarProductos(): Flow<List<Producto>>

    // Verificar si existe un código al registrar
    @Query(
        "SELECT COUNT(*) FROM productos WHERE codigo = :codigo"
    )
    suspend fun contarCodigo(codigo: String): Int

    // Buscar producto mediante su ID
    @Query(
        "SELECT * FROM productos WHERE id = :id LIMIT 1"
    )
    suspend fun obtenerPorId(id: Int): Producto?

    // Verificar código al editar, excluyendo el producto actual
    @Query(
        """
        SELECT COUNT(*) FROM productos
        WHERE codigo = :codigo AND id != :productoId
        """
    )
    suspend fun contarCodigoExcepto(
        codigo: String,
        productoId: Int
    ): Int

    // Registrar un producto nuevo
    @Insert
    suspend fun insertar(producto: Producto)

    // Editar información de un producto existente
    @Update
    suspend fun actualizar(producto: Producto)

    // Actualizar existencias después de un movimiento
    @Query(
        "UPDATE productos SET stock = :nuevoStock WHERE id = :id"
    )
    suspend fun actualizarStock(
        id: Int,
        nuevoStock: Int
    )

    // Eliminar un producto
    @Delete
    suspend fun eliminar(producto: Producto)
}
