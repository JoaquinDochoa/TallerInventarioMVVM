package cl.duoc.tallerinventario.ui

import android.widget.Toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import cl.duoc.tallerinventario.data.local.Producto

import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions

@Composable
fun ProductosScreen(
    productos: List<Producto>,
    onNuevo: () -> Unit,
    onEditar: (Producto) -> Unit,
    onEliminar: (Producto) -> Unit
) {

    val context = LocalContext.current

    // ESTADOS DE LA PANTALLA

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var pendiente by remember {
        mutableStateOf<Producto?>(null)
    }

    var productoEscaneado by remember {
        mutableStateOf<Producto?>(null)
    }

    var errorQR by remember {
        mutableStateOf<String?>(null)
    }

    // CONFIGURACIÓN DEL LECTOR QR

    val scannerOptions = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .enableAutoZoom()
            .build()
    }

    val scanner = remember(context, scannerOptions) {
        GmsBarcodeScanning.getClient(
            context,
            scannerOptions
        )
    }

    // FORMATO DE CÓDIGOS INTERNOS
    // Entre 4 y 20 caracteres alfanuméricos.

    val formatoCodigo = remember {
        Regex("^[A-Za-z0-9]{4,20}$")
    }

    // FILTRADO DE PRODUCTOS

    val filtrados = productos.filter { producto ->
        producto.nombre.contains(
            busqueda,
            ignoreCase = true
        ) || producto.codigo.contains(
            busqueda,
            ignoreCase = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // TÍTULO

        Text(
            text = "Inventario de productos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BUSCADOR

        OutlinedTextField(
            value = busqueda,
            onValueChange = { nuevoTexto ->
                busqueda = nuevoTexto
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Buscar por nombre o código")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // BOTÓN ESCANEAR QR

        Button(
            onClick = {

                // Limpiar resultados anteriores
                productoEscaneado = null
                errorQR = null

                scanner.startScan()
                    .addOnSuccessListener { barcode ->

                        val codigo = barcode.rawValue
                            ?.trim()
                            .orEmpty()

                        // Buscar el código en los productos
                        // registrados en el inventario.

                        val encontrado = productos.find { producto ->
                            producto.codigo.equals(
                                codigo,
                                ignoreCase = true
                            )
                        }

                        when {

                            // QR sin contenido
                            codigo.isEmpty() -> {
                                errorQR =
                                    "El código QR está vacío."
                            }

                            // Producto existente
                            encontrado != null -> {

                                busqueda = encontrado.codigo

                                productoEscaneado = encontrado
                            }

                            // Formato no permitido
                            !formatoCodigo.matches(codigo) -> {

                                errorQR =
                                    "El código QR no tiene un formato válido. " +
                                            "Debe contener entre 4 y 20 " +
                                            "letras o números."
                            }

                            // Código correcto, pero no registrado
                            else -> {

                                errorQR =
                                    "No existe ningún producto " +
                                            "registrado con el código: $codigo"
                            }
                        }
                    }

                    .addOnCanceledListener {
                        // El usuario cerró el lector QR.
                    }

                    .addOnFailureListener { error ->

                        errorQR =
                            "No se pudo leer el código QR: " +
                                    (error.message
                                        ?: "Error desconocido")
                    }
            },

            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Escanear código QR")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BOTÓN REGISTRAR PRODUCTO

        Button(
            onClick = onNuevo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Registrar producto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CONTADOR

        Text(
            text = "Productos encontrados: ${filtrados.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // LISTADO DE PRODUCTOS

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = filtrados,
                key = { producto -> producto.id }
            ) { producto ->

                ProductoCardAnimada(
                    producto = producto,

                    onEditar = {
                        onEditar(producto)
                    },

                    onEliminar = {
                        pendiente = producto
                    }
                )
            }
        }
    }

    // DIÁLOGO PARA ELIMINAR PRODUCTO

    pendiente?.let { producto ->

        AlertDialog(
            onDismissRequest = {
                pendiente = null
            },

            title = {
                Text("Confirmar eliminación")
            },

            text = {
                Text(
                    "¿Estás seguro de eliminar " +
                            "${producto.nombre}?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar(producto)
                        pendiente = null
                    }
                ) {
                    Text("Eliminar")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        pendiente = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIÁLOGO DE PRODUCTO ENCONTRADO POR QR

    productoEscaneado?.let { producto ->

        AlertDialog(
            onDismissRequest = {
                productoEscaneado = null
            },

            title = {
                Text("Producto encontrado")
            },

            text = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = "Nombre: ${producto.nombre}"
                    )

                    Text(
                        text = "Código: ${producto.codigo}"
                    )

                    Text(
                        text = "Categoría: ${producto.categoria}"
                    )

                    Text(
                        text = "Marca: ${producto.marca}"
                    )

                    Text(
                        text = "Stock disponible: ${producto.stock}"
                    )

                    Text(
                        text = "Stock mínimo: ${producto.stockMinimo}"
                    )

                    Text(
                        text = "Ubicación: ${producto.ubicacion}"
                    )

                    if (
                        producto.stock <= producto.stockMinimo
                    ) {
                        Text(
                            text = if (producto.stock == 0) {
                                "PRODUCTO AGOTADO"
                            } else {
                                "STOCK BAJO"
                            },
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        productoEscaneado = null
                    }
                ) {
                    Text("Aceptar")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        productoEscaneado = null
                        onEditar(producto)
                    }
                ) {
                    Text("Editar producto")
                }
            }
        )
    }

    // DIÁLOGO DE ERROR DEL QR

    errorQR?.let { mensaje ->

        AlertDialog(
            onDismissRequest = {
                errorQR = null
            },

            title = {
                Text("Código QR no válido")
            },

            text = {
                Text(mensaje)
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        errorQR = null
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

// TARJETA ANIMADA PARA CADA PRODUCTO

@Composable
private fun ProductoCardAnimada(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {

    val agotado = producto.stock == 0

    val stockBajo =
        producto.stock <= producto.stockMinimo

    // CAMBIO DE COLOR ANIMADO

    val colorFondo by animateColorAsState(
        targetValue = if (stockBajo) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },

        animationSpec = tween(
            durationMillis = 600
        ),

        label = "AnimacionStock"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),

        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            // NOMBRE

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            // DATOS DEL PRODUCTO

            Text("Código: ${producto.codigo}")

            Text("Categoría: ${producto.categoria}")

            Text("Marca: ${producto.marca}")

            Text("Ubicación: ${producto.ubicacion}")

            HorizontalDivider()

            Text(
                "Stock disponible: ${producto.stock}"
            )

            Text(
                "Stock mínimo: ${producto.stockMinimo}"
            )

            // ALERTA ANIMADA DE STOCK

            AnimatedVisibility(
                visible = stockBajo,

                enter = fadeIn(
                    animationSpec = tween(500)
                ) + expandVertically(),

                exit = fadeOut(
                    animationSpec = tween(300)
                ) + shrinkVertically()
            ) {

                Text(
                    text = if (agotado) {
                        "⚠ PRODUCTO AGOTADO"
                    } else {
                        "⚠ STOCK BAJO EL MÍNIMO"
                    },

                    color = MaterialTheme.colorScheme.error,

                    style = MaterialTheme.typography.titleSmall,

                    modifier = Modifier.padding(
                        vertical = 8.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // BOTONES EDITAR Y ELIMINAR

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onEditar,

                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }

                OutlinedButton(
                    onClick = onEliminar,

                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}
