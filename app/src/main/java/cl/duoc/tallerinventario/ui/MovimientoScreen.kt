package cl.duoc.tallerinventario.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.tallerinventario.data.local.Movimiento
import cl.duoc.tallerinventario.data.local.Producto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoScreen(
    productos: List<Producto>,
    movimientos: List<Movimiento>,
    mensaje: String?,
    guardando: Boolean,
    onRegistrar: (Int, String, String) -> Unit,
    onLimpiarMensaje: () -> Unit
) {
    var productoSeleccionado by remember {
        mutableStateOf<Int?>(null)
    }
    var tipoSeleccionado by remember {
        mutableStateOf("Ingreso")
    }
    var cantidad by remember {
        mutableStateOf("")
    }
    var menuProductos by remember {
        mutableStateOf(false)
    }
    var menuTipos by remember {
        mutableStateOf(false)
    }

    val productoActual = productos.find {
        it.id == productoSeleccionado
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Movimientos de inventario",
            style = MaterialTheme.typography.headlineSmall
        )

        ExposedDropdownMenuBox(
            expanded = menuProductos,
            onExpandedChange = {
                menuProductos = it
            }
        ) {
            OutlinedTextField(
                value = productoActual?.nombre
                    ?: "Seleccionar producto",
                onValueChange = {},
                readOnly = true,
                label = { Text("Producto") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuProductos
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = menuProductos,
                onDismissRequest = {
                    menuProductos = false
                }
            ) {
                productos.forEach { producto ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "${producto.nombre} (${producto.codigo})"
                            )
                        },
                        onClick = {
                            productoSeleccionado = producto.id
                            menuProductos = false
                            onLimpiarMensaje()
                        }
                    )
                }
            }
        }

        productoActual?.let {
            Text("Stock actual: ${it.stock}")
        }

        ExposedDropdownMenuBox(
            expanded = menuTipos,
            onExpandedChange = {
                menuTipos = it
            }
        ) {
            OutlinedTextField(
                value = tipoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de movimiento") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuTipos
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = menuTipos,
                onDismissRequest = {
                    menuTipos = false
                }
            ) {
                listOf("Ingreso", "Venta", "Consumo").forEach {
                        tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo) },
                        onClick = {
                            tipoSeleccionado = tipo
                            menuTipos = false
                            onLimpiarMensaje()
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = cantidad,
            onValueChange = {
                cantidad = it.filter { caracter ->
                    caracter.isDigit()
                }
                onLimpiarMensaje()
            },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                productoSeleccionado?.let { id ->
                    onRegistrar(
                        id,
                        tipoSeleccionado,
                        cantidad
                    )
                }
            },
            enabled = productoSeleccionado != null &&
                    cantidad.isNotBlank() &&
                    !guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (guardando) "Guardando..."
                else "Registrar movimiento"
            )
        }

        AnimatedVisibility(
            visible = mensaje != null,
            enter = fadeIn(
                animationSpec = tween(400)
            ) + expandVertically(),
            exit = fadeOut(
                animationSpec = tween(300)
            ) + shrinkVertically()
        ) {
            val esExito = mensaje == "Movimiento registrado"

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (esExito) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Text(
                    text = mensaje.orEmpty(),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        HorizontalDivider()

        Text(
            "Historial de movimientos",
            style = MaterialTheme.typography.titleLarge
        )

        if (movimientos.isEmpty()) {
            Text("Todavía no hay movimientos registrados.")
        }

        movimientos.forEach { movimiento ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        movimiento.productoNombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Tipo: ${movimiento.tipo}")
                    Text("Cantidad: ${movimiento.cantidad}")
                    Text(
                        "Stock anterior: ${movimiento.stockAnterior}"
                    )
                    Text(
                        "Stock resultante: ${movimiento.stockResultante}"
                    )
                    Text(
                        SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                        ).format(Date(movimiento.fecha))
                    )
                }
            }
        }
    }
}
