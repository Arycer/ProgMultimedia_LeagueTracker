package me.arycer.leaguetracker

import android.content.Intent
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
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        usernameEditText = findViewById(R.id.username_field)
        passwordEditText = findViewById(R.id.password_field)
        registerButton = findViewById(R.id.register_button)
        loginButton = findViewById(R.id.login_button)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
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
        loginButton?.setOnClickListener { v: View? ->
            val email = usernameEditText!!.text.toString().trim { it <= ' ' }
            val password = passwordEditText!!.text.toString().trim { it <= ' ' }

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Tienes que introducir un usuario y contraseña")
                return@setOnClickListener
            }
            handleFirebaseLogin(email, password)
        }

        registerButton?.setOnClickListener { v: View? ->
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleFirebaseLogin(email: String, password: String) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (auth?.currentUser != null && auth?.currentUser?.isEmailVerified == true) {
                        showToast("Inicio de sesión exitoso")
                        navigateToMain(email)
                    } else {
                        showToast("Por favor, verifica tu correo electrónico.")
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showToast("Contraseña incorrecta.")
                    } else if (task.exception is FirebaseAuthInvalidUserException) {
                        showToast("Correo electrónico no registrado.")
                    } else {
                        showToast("Error en el login: " + task.exception?.message)
                    }
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain(email: String) {
        val intent = Intent(
            this,
            FavouriteUsersActivity::class.java
        )
        intent.putExtra("LoginUsername", email)
        startActivity(intent)
        finish()
    }
}