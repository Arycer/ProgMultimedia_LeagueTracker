package me.arycer.leaguetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.email)
        val recoverButton = findViewById<Button>(R.id.recoverButton)
        val backToLoginButton = findViewById<Button>(R.id.backToLoginButton)  // Botón para regresar al Login

        recoverButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su correo", Toast.LENGTH_SHORT).show()
            } else {
                // Intentar enviar correo de recuperación de contraseña
                recoverPassword(email) { isSuccess, msg ->
                    if (isSuccess) {
                        Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Configuración del botón para regresar al Login
        backToLoginButton.setOnClickListener {
            // Crear la intención para abrir la actividad LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finaliza la actividad actual para evitar que el usuario regrese a ella al presionar el botón atrás
        }
    }

    private fun recoverPassword(email: String, onResult: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Correo enviado con éxito
                    onResult(true, "Correo de recuperación enviado")
                } else {
                    // Error en el envío del correo
                    onResult(false, "Error al enviar correo de recuperación: ${task.exception?.message}")
                }
            }
    }
}