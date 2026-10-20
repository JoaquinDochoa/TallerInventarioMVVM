
package cl.duoc.tallerinventario.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Insert
    suspend fun insertar(movimiento: Movimiento)

    @Query(
        "SELECT * FROM movimientos ORDER BY fecha DESC, id DESC"
    )
    fun obtenerMovimientos(): Flow<List<Movimiento>>
}
