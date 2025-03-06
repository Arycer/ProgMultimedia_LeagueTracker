package me.arycer.leaguetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import me.arycer.leaguetracker.databinding.ActivityRecoverPasswordBinding

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoverPasswordBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.recoverButton.setOnClickListener { attemptRecovery() }
        binding.backToLoginButton.setOnClickListener { navigateToLogin() }
    }

    private fun attemptRecovery() {
        val email = binding.email.text.toString().trim()
        if (email.isEmpty()) {
            showToast("Por favor, ingrese su correo")
        } else {
            recoverPassword(email)
        }
    }

    private fun recoverPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Correo de recuperación enviado")
                } else {
                    showToast("Error al enviar correo de recuperación: ${task.exception?.message}")
                }
            }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}