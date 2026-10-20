
package cl.duoc.tallerinventario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val productoNombre: String,
    val tipo: String,
    val cantidad: Int,
    val stockAnterior: Int,
    val stockResultante: Int,
    val fecha: Long = System.currentTimeMillis()
)
