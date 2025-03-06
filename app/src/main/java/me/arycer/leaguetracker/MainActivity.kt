package me.arycer.leaguetracker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import me.arycer.leaguetracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si el usuario está logueado antes de continuar con el resto de la inicialización
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

        if (!isLoggedIn) {
            // Si no está logueado, redirige al LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cierra MainActivity para que no pueda regresar
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura el NavController desde el fragmento de host
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        // Configura el Navigation View y el DrawerLayout
        val navView = binding.myNavView
        auth = FirebaseAuth.getInstance()

        // Configura AppBarConfiguration con los destinos principales
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragmentHome, R.id.favUsers), // Destinos principales
            binding.main // DrawerLayout
        )

        // Configura la ActionBar con el NavController y AppBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configura el Navigation View para usar con el NavController
        navView.setupWithNavController(navController)

        // Maneja la selección de los ítems del Drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    // Navega al fragmento Home
                    navController.navigate(R.id.fragmentHome)
                }
                R.id.favUsers -> {
                    // Navega al fragmento de Precipitaciones
                    navController.navigate(R.id.favUsers)
                }
                R.id.logout -> {
                    // Cerrar sesión de Firebase
                    auth.signOut()

                    // Eliminar la sesión guardada en SharedPreferences
                    val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("USER_ID")
                    editor.putBoolean("IS_LOGGED_IN", false)  // Marca que el usuario ya no está logueado
                    editor.apply()

                    // Redirigir al LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                    // Finalizar la actividad actual para que el usuario no pueda regresar
                    finish()
                }
            }
            // Cierra el Drawer después de la navegación
            binding.main.closeDrawer(GravityCompat.START)
            true
        }

        // Recuperar el usuario logueado y actualizar las vistas después de que se haya inicializado el layout
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Accedemos al NavigationView para obtener los TextView que muestran el nombre y correo
            val nameTextView: TextView = navView.getHeaderView(0).findViewById(R.id.txt_name)
            val emailTextView: TextView = navView.getHeaderView(0).findViewById(R.id.txt_email)

            // Establecer nombre y correo en las vistas correspondientes
            nameTextView.text = currentUser.displayName ?: "Usuario"
            emailTextView.text = currentUser.email
        }
    }

    // Maneja el botón de retroceso de la ActionBar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Opcional: Puedes manejar los ítems de la barra de herramientas (si tienes)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_op, menu)
        return true
    }

    // Lógica opcional para manejar los ítems de la barra de opciones (por ejemplo, "Home")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.fragmentHome -> {
                // Navega al fragmento Home desde la barra de opciones
                navController.navigate(R.id.fragmentHome)
                true
            }
            R.id.favUsers -> {
                // Navega al fragmento Precipitaciones desde la barra de opciones
                navController.navigate(R.id.favUsers)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
