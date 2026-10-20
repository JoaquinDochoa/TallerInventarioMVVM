
package cl.duoc.tallerinventario.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ProductoFormScreen(
    estado: ProductoUiState,
    mensaje: String?,
    onCodigoChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onMarcaChange: (String) -> Unit,
    onUbicacionChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onMinimoChange: (String) -> Unit,
    onGuardar: () -> Unit,
    editando: Boolean = false
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = if (editando)
                "Editar producto"
            else
                "Registrar producto",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (editando)
                "Modifica los datos del producto seleccionado."
            else
                "Completa los datos del nuevo producto."
        )

        CampoProducto(
            valor = estado.codigo,
            onCambio = onCodigoChange,
            titulo = "Código interno *",
            error = estado.errores.codigo
        )

        CampoProducto(
            valor = estado.nombre,
            onCambio = onNombreChange,
            titulo = "Nombre del producto *",
            error = estado.errores.nombre
        )

        CampoProducto(
            valor = estado.categoria,
            onCambio = onCategoriaChange,
            titulo = "Categoría *",
            error = estado.errores.categoria
        )

        CampoProducto(
            valor = estado.marca,
            onCambio = onMarcaChange,
            titulo = "Marca",
            error = null
        )

        CampoProducto(
            valor = estado.ubicacion,
            onCambio = onUbicacionChange,
            titulo = "Ubicación en bodega *",
            error = estado.errores.ubicacion
        )

        // En edición solo se consulta el stock.
        CampoProducto(
            valor = estado.stock,
            onCambio = onStockChange,
            titulo = if (editando)
                "Stock actual (solo lectura)"
            else
                "Stock inicial *",
            error = estado.errores.stock,
            numerico = true,
            habilitado = !editando
        )

        if (editando) {
            Text(
                text = "Para cambiar las existencias, " +
                        "utiliza el módulo de movimientos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        CampoProducto(
            valor = estado.minimo,
            onCambio = onMinimoChange,
            titulo = "Stock mínimo *",
            error = estado.errores.minimo,
            numerico = true
        )

        mensaje?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onGuardar,
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    estado.guardando -> "Guardando..."
                    editando -> "Guardar cambios"
                    else -> "Registrar producto"
                }
            )
        }
    }
}

@Composable
private fun CampoProducto(
    valor: String,
    onCambio: (String) -> Unit,
    titulo: String,
    error: String?,
    numerico: Boolean = false,
    habilitado: Boolean = true
) {

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(titulo) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = habilitado,
        isError = error != null,

        keyboardOptions = KeyboardOptions(
            keyboardType = if (numerico)
                KeyboardType.Number
            else
                KeyboardType.Text
        ),

        supportingText = {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },

        trailingIcon = {
            if (error != null) {
                Text(
                    text = "!",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}
