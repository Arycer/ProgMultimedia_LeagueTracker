package me.arycer.leaguetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val registerButton = findViewById<Button>(R.id.registrar)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Intentar registrar al usuario
                registerUser(email, password) { isSuccess, msg ->
                    if (isSuccess) {
                        // Si el registro fue exitoso
                        Toast.makeText(this, "Usuario registrado exitosamente. Verifique su correo.", Toast.LENGTH_SHORT).show()

                        // Redirigir a LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()  // Cerrar la Activity actual
                    } else {
                        // Si ocurrió un error en el registro
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun registerUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Usuario creado exitosamente
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                // Enviado correo de verificación
                                onResult(true, "Correo de verificación enviado")
                            } else {
                                // Error al enviar correo de verificación
                                onResult(false, "Error al enviar correo de verificación")
                            }
                        }
                } else {
                    // Error en el registro
                    onResult(false, "Error en el registro: ${task.exception?.message}")
                }
            }
    }
}
