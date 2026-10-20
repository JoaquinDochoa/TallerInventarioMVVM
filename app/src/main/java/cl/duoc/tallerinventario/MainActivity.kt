
package cl.duoc.tallerinventario

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import cl.duoc.tallerinventario.data.local.AppDatabase
import cl.duoc.tallerinventario.data.repository.ProductoRepository
import cl.duoc.tallerinventario.data.repository.MovimientoRepository
import cl.duoc.tallerinventario.notifications.NotificacionHelper
import cl.duoc.tallerinventario.ui.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TallerApp()
            }
        }
    }
}

@Composable
fun TallerApp() {

    val context = LocalContext.current

    // Base de datos
    val database = remember {
        AppDatabase.obtener(context)
    }

    // Repositorios
    val productoRepo = remember(database) {
        ProductoRepository(database.productoDao())
    }

    val movimientoRepo = remember(database) {
        MovimientoRepository(database)
    }

    // ViewModels
    val productoVM: ProductoViewModel = viewModel(
        factory = ProductoViewModel.Factory(productoRepo)
    )

    val movimientoVM: MovimientoViewModel = viewModel(
        factory = MovimientoViewModel.Factory(movimientoRepo)
    )

    // Navegación
    val nav = rememberNavController()

    // Estados de productos
    val productos by productoVM.productos.collectAsState()
    val mensajeProducto by productoVM.mensaje.collectAsState()
    val guardado by productoVM.guardado.collectAsState()
    val estadoProducto by productoVM.estado.collectAsState()
    val editando by productoVM.editando.collectAsState()

    // Estados de movimientos
    val movimientos by movimientoVM.movimientos.collectAsState()
    val mensajeMovimiento by movimientoVM.mensaje.collectAsState()
    val guardandoMovimiento by movimientoVM.guardando.collectAsState()

    // Crear canal de notificaciones
    LaunchedEffect(Unit) {
        NotificacionHelper.crearCanal(context)
    }

    // Permisos de notificaciones
    val permisoNotificaciones =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { _ ->
                // El usuario decide si permite notificaciones
            }
        )

    fun solicitarNotificaciones() {
        if (Build.VERSION.SDK_INT >= 33) {

            val concedido = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!concedido) {
                permisoNotificaciones.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    // Escuchar eventos de stock bajo
    LaunchedEffect(movimientoVM) {
        movimientoVM.alertas.collect { alerta ->
            NotificacionHelper.mostrarStockBajo(
                context = context,
                productoId = alerta.productoId,
                nombre = alerta.nombre,
                stock = alerta.stock,
                minimo = alerta.minimo
            )
        }
    }

    // Regresar al listado después de guardar
    LaunchedEffect(guardado) {
        if (guardado) {
            productoVM.confirmarNavegacion()
            nav.popBackStack()
        }
    }

    NavHost(
        navController = nav,
        startDestination = "login"
    ) {

        // LOGIN
        composable("login") {
            LoginScreen(
                onIngreso = {
                    nav.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // HOME
        composable("home") {
            HomeScreen(
                onProductos = {
                    nav.navigate("productos")
                },

                onMovimientos = {
                    solicitarNotificaciones()
                    movimientoVM.limpiarMensaje()
                    nav.navigate("movimientos")
                },

                onSalir = {
                    nav.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // LISTADO DE PRODUCTOS
        composable("productos") {
            ProductosScreen(
                productos = productos,

                onNuevo = {
                    productoVM.limpiarFormulario()
                    nav.navigate("nuevo")
                },

                // NUEVO: EDITAR PRODUCTO
                onEditar = { producto ->
                    productoVM.cargarProducto(producto)
                    nav.navigate("editar")
                },

                onEliminar = productoVM::eliminar
            )
        }

        // REGISTRAR PRODUCTO
        composable("nuevo") {
            ProductoFormScreen(
                estado = estadoProducto,
                mensaje = mensajeProducto,
                editando = false,

                onCodigoChange = productoVM::onCodigoChange,
                onNombreChange = productoVM::onNombreChange,
                onCategoriaChange = productoVM::onCategoriaChange,
                onMarcaChange = productoVM::onMarcaChange,
                onUbicacionChange = productoVM::onUbicacionChange,
                onStockChange = productoVM::onStockChange,
                onMinimoChange = productoVM::onMinimoChange,

                onGuardar = productoVM::guardarProducto
            )
        }

        // EDITAR PRODUCTO
        composable("editar") {
            ProductoFormScreen(
                estado = estadoProducto,
                mensaje = mensajeProducto,
                editando = editando,

                onCodigoChange = productoVM::onCodigoChange,
                onNombreChange = productoVM::onNombreChange,
                onCategoriaChange = productoVM::onCategoriaChange,
                onMarcaChange = productoVM::onMarcaChange,
                onUbicacionChange = productoVM::onUbicacionChange,
                onStockChange = productoVM::onStockChange,
                onMinimoChange = productoVM::onMinimoChange,

                onGuardar = productoVM::guardarProducto
            )
        }

        // MOVIMIENTOS
        composable("movimientos") {
            MovimientoScreen(
                productos = productos,
                movimientos = movimientos,
                mensaje = mensajeMovimiento,
                guardando = guardandoMovimiento,
                onRegistrar = movimientoVM::registrar,
                onLimpiarMensaje = movimientoVM::limpiarMensaje
            )
        }
    }
}