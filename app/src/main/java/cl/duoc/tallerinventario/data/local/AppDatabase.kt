
package cl.duoc.tallerinventario.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Producto::class,
        Movimiento::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {

        @Volatile
        private var instancia: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS movimientos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productoId INTEGER NOT NULL,
                        productoNombre TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        cantidad INTEGER NOT NULL,
                        stockAnterior INTEGER NOT NULL,
                        stockResultante INTEGER NOT NULL,
                        fecha INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taller_inventario.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instancia = it }
            }
    }
}
