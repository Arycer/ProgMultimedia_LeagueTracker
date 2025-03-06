package me.arycer.leaguetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import me.arycer.leaguetracker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si el usuario ya está logueado y el correo está verificado, ir a MainActivity
        auth.currentUser?.takeIf { it.isEmailVerified }?.let {
            navigateToMain()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val email = binding.usernameField.text?.toString()?.trim() ?: ""
            val password = binding.passwordField.text?.toString() ?: ""
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(email, password)
            }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                auth.currentUser?.takeIf { it.isEmailVerified }?.let { user ->
                    saveUserSession(user.uid)
                    navigateToMain()
                } ?: run {
                    Toast.makeText(this, "Por favor, verifica tu correo electrónico.", Toast.LENGTH_SHORT).show()
                }
            } else {
                handleLoginError(task.exception)
            }
        }
    }

    private fun saveUserSession(userId: String) {
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("USER_ID", userId)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun handleLoginError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta."
            is FirebaseAuthUserCollisionException -> "Este correo ya está registrado."
            is FirebaseAuthInvalidUserException -> "Correo electrónico no registrado."
            else -> "Error en el login: ${exception?.message}"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}