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

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isUserLoggedIn()) {
            redirectToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationController()
        setupDrawer()
        updateNavHeader()
    }

    private fun isUserLoggedIn(): Boolean {
        return getSharedPreferences("UserSession", MODE_PRIVATE)
            .getBoolean("IS_LOGGED_IN", false)
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragmentHome, R.id.favUsers), // Destinos principales
            binding.main // DrawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.myNavView.setupWithNavController(navController)
    }

    private fun setupDrawer() {
        binding.myNavView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> navController.navigate(R.id.fragmentHome)
                R.id.favUsers -> navController.navigate(R.id.favUsers)
                R.id.logout -> performLogout()
            }
            binding.main.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun performLogout() {
        auth.signOut()
        getSharedPreferences("UserSession", MODE_PRIVATE).edit().apply {
            remove("USER_ID")
            putBoolean("IS_LOGGED_IN", false)
            apply()
        }
        redirectToLogin()
    }

    private fun updateNavHeader() {
        auth.currentUser?.let { currentUser ->
            val headerView = binding.myNavView.getHeaderView(0)
            headerView.findViewById<TextView>(R.id.txt_name).text =
                currentUser.displayName ?: "Usuario"
            headerView.findViewById<TextView>(R.id.txt_email).text = currentUser.email
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_op, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.fragmentHome -> {
                navController.navigate(R.id.fragmentHome)
                true
            }
            R.id.favUsers -> {
                navController.navigate(R.id.favUsers)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}