
package cl.duoc.tallerinventario.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

import cl.duoc.tallerinventario.R
import java.security.MessageDigest

// Autenticación ficticia para el proyecto académico
private const val PIN_HASH_DEMO =
    "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"

private fun hashPin(valor: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(valor.toByteArray())
        .joinToString("") { "%02x".format(it) }
}

@Composable
fun LoginScreen(
    onIngreso: () -> Unit
) {

    var usuario by rememberSaveable {
        mutableStateOf("")
    }

    var pin by rememberSaveable {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // LOGO PRINCIPAL INVENTARIO TALLER
        Image(
            painter = painterResource(
                id = R.drawable.logo_inventario_taller
            ),
            contentDescription = "Logo Inventario Taller",
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        // DESCRIPCIÓN DE LA APLICACIÓN
        Text(
            text = "Gestión de inventario automotriz",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // CAMPO USUARIO
        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
                error = ""
            },
            label = {
                Text("Usuario")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO PIN
        OutlinedTextField(
            value = pin,
            onValueChange = {
                pin = it
                error = ""
            },
            label = {
                Text("PIN")
            },
            visualTransformation =
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // VALIDACIÓN DE CREDENCIALES
        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BOTÓN DE INGRESO
        Button(
            onClick = {
                if (
                    usuario.trim() == "admin" &&
                    hashPin(pin) == PIN_HASH_DEMO
                ) {
                    onIngreso()
                } else {
                    error = "Usuario o PIN incorrectos"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cuenta ficticia: admin / PIN 1234",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // LOGO INSTITUCIONAL DUOC UC
        Image(
            painter = painterResource(
                id = R.drawable.logoduoc
            ),
            contentDescription = "Logo Duoc UC",
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Proyecto académico - Duoc UC",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
