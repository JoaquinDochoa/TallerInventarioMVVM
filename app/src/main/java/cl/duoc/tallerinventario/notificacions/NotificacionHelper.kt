package cl.duoc.tallerinventario.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object NotificacionHelper {

    private const val CANAL_ID = "stock_bajo"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Alertas de inventario",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Avisos de productos con existencias bajas"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(canal)
        }
    }

    fun mostrarStockBajo(
        context: Context,
        productoId: Int,
        nombre: String,
        stock: Int,
        minimo: Int
    ) {
        if (
            Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        crearCanal(context)

        val titulo = if (stock == 0) {
            "Producto agotado"
        } else {
            "Stock bajo"
        }

        val texto = "$nombre tiene $stock unidades. " +
                "Mínimo establecido: $minimo."

        val builder = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) {
            Notification.Builder(context, CANAL_ID)
        } else {
            Notification.Builder(context)
                .setPriority(Notification.PRIORITY_HIGH)
        }

        val notificacion = builder
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(
                Notification.BigTextStyle()
                    .bigText(texto)
            )
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.notify(productoId, notificacion)
    }
}
