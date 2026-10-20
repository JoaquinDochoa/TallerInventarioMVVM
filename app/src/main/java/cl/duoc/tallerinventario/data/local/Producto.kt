package cl.duoc.tallerinventario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val marca: String,
    val ubicacion: String,
    val stock: Int,
    val stockMinimo: Int
)
