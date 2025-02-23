package me.arycer.leaguetracker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private var usernameEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var loginButton: Button? = null
    private var registerButton: Button? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)

        val currentUser = auth.currentUser

        if (sharedPreferences.getBoolean("IS_LOGGED_IN", false) && currentUser != null) {
            currentUser.reload()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser != null) {
                            navigateToMain(auth.currentUser!!.email ?: "")
                        } else {
                            clearSession()
                        }
                    } else {
                        clearSession()
                    }
                }
        } else {
            clearSession()
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        usernameEditText = findViewById(R.id.username_field)
        passwordEditText = findViewById(R.id.password_field)
        registerButton = findViewById(R.id.register_button)
        loginButton = findViewById(R.id.login_button)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            v.setPadding(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
            insets
        }
    }

    private fun setupListeners() {
        loginButton?.setOnClickListener {
            val email = usernameEditText!!.text.toString().trim()
            val password = passwordEditText!!.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Tienes que introducir un usuario y contraseña")
                return@setOnClickListener
            }
            handleFirebaseLogin(email, password)
        }

        registerButton?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleFirebaseLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                        saveSession(email)
                        showToast("Sesión iniciada correctamente.")
                        navigateToMain(email)
                    } else {
                        showToast("Debes verificar tu dirección de correo electrónico antes de iniciar sesión. Revisa tu bandeja de entrada.")
                    }
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> showToast("Contraseña incorrecta.")
                        is FirebaseAuthInvalidUserException -> showToast("Usuario no encontrado.")
                        else -> showToast("Error en el login: ${task.exception?.message}")
                    }
                }
            }
    }

    private fun saveSession(email: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.putString("USER_EMAIL", email)
        editor.apply()
    }

    private fun navigateToMain(email: String) {
        val intent = Intent(this, FavouriteUsersActivity::class.java)
        intent.putExtra("LoginUsername", email)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", false)
        editor.putString("USER_EMAIL", null)
        editor.apply()
    }
}