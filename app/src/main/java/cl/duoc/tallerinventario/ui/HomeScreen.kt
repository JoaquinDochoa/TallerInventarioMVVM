
package cl.duoc.tallerinventario.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onProductos: () -> Unit,
    onMovimientos: () -> Unit,
    onSalir: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "Panel del taller",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            "Control de inventario de productos automotrices"
        )

        Button(
            onClick = onProductos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver productos")
        }

        Button(
            onClick = onMovimientos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar movimientos")
        }

        OutlinedButton(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
