package me.arycer.leaguetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Verifica si el usuario ya está logueado
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            // Si el usuario ya está logueado y el correo está verificado, redirige a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cierra LoginActivity para no poder regresar
            return
        }

        setContentView(R.layout.activity_login)

        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPassword)
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(intent)
        }

        val nombreUsuarioLayout = findViewById<EditText>(R.id.username_field)
        val usuarioEditText = nombreUsuarioLayout.text

        val passwordEditText = findViewById<TextInputEditText>(R.id.password_field)

        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val username = usuarioEditText?.toString() ?: ""
            val password = passwordEditText?.text.toString()

            // Validación de campos vacíos
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Intentar iniciar sesión con Firebase
                startLogin(username, password) { isSuccess, msg ->
                    if (isSuccess) {
                        // Login exitoso
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Cerrar LoginActivity
                    } else {
                        // Mostrar el mensaje de error
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startLogin(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Guardar el UID del usuario y el estado de la sesión en SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("USER_ID", user.uid)  // Guarda el UID del usuario
                        editor.putBoolean("IS_LOGGED_IN", true)  // Marca que el usuario está logeado
                        editor.apply()

                        onResult(true, "Login exitoso")
                    } else {
                        // El correo no está verificado
                        onResult(false, "Por favor, verifica tu correo electrónico.")
                    }
                } else {
                    // Manejo de errores de login
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Contraseña incorrecta
                            onResult(false, "Contraseña incorrecta.")
                        }
                        is FirebaseAuthUserCollisionException -> {
                            // Correo ya registrado
                            onResult(false, "Este correo ya está registrado.")
                        }
                        is FirebaseAuthInvalidUserException -> {
                            // Correo electrónico no existe
                            onResult(false, "Correo electrónico no registrado.")
                        }
                        else -> {
                            // Otro tipo de error
                            onResult(false, "Error en el login: ${exception?.message}")
                        }
                    }
                }
            }
    }
}