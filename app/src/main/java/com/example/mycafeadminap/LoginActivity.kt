package com.example.mycafeadminap

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mycafeadminap.data.AppDatabase
import com.example.mycafeadminap.data.AuthRepository
import com.example.mycafeadminap.data.LoginResult
import com.example.mycafeadminap.data.Seeder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        if (session.isAdminLoggedIn()) {
            openHome()
            return
        }
        setContentView(R.layout.activity_login)

        val db = AppDatabase.get(applicationContext)
        val repo = AuthRepository(db.userDb())
        val seedJob: Job = scope.launch(Dispatchers.IO) { Seeder.seedIfEmpty(db) }

        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val errorText = findViewById<TextView>(R.id.errorText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val progress = findViewById<ProgressBar>(R.id.progress)

        loginButton.setOnClickListener {
            emailLayout.error = null
            passwordLayout.error = null
            errorText.visibility = View.GONE

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            var valid = true
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Enter a valid email address"
                valid = false
            }
            if (password.isEmpty()) {
                passwordLayout.error = "Enter your password"
                valid = false
            }
            if (!valid) return@setOnClickListener

            progress.visibility = View.VISIBLE
            loginButton.isEnabled = false

            scope.launch {
                seedJob.join()
                val result = withContext(Dispatchers.IO) { repo.login(email, password) }
                progress.visibility = View.GONE
                loginButton.isEnabled = true

                when (result) {
                    is LoginResult.Success -> {
                        session.save(result.user)
                        openHome()
                    }
                    LoginResult.InvalidCredentials -> {
                        errorText.text = "Incorrect email or password"
                        errorText.visibility = View.VISIBLE
                    }
                    LoginResult.NotAdmin -> {
                        errorText.text = "This account does not have admin access"
                        errorText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun openHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}