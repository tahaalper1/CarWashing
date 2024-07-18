package com.example.carwashing

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class KurumsalLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kurumsallogin)

        val auth = FirebaseAuth.getInstance()

        // Status bar rengini değiştirmek için window özelliğini kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.black) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }

        val loginButton: Button = findViewById(R.id.loginButton)
        val forgotPassword: Button = findViewById(R.id.forgotPasswordButton)
        val register: Button = findViewById(R.id.registerButton)
        val mail: EditText = findViewById(R.id.usernameEditText)
        val password: EditText = findViewById(R.id.passwordEditText)

        loginButton.backgroundTintList = resources.getColorStateList(R.color.black)
        register.backgroundTintList = resources.getColorStateList(R.color.black)
        forgotPassword.backgroundTintList = resources.getColorStateList(R.color.white)

        register.setOnClickListener {
            // Butona tıklandığında yapılacak işlemler
            Toast.makeText(this, "Üye Olma Sayfası", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, KurumsalRegister::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = mail.text.toString()
            val sifre = password.text.toString()

            // Firebase Authentication instance'ını alın
            val auth = FirebaseAuth.getInstance()

            // E-posta ve şifreyle giriş yapma işlemi
            auth.signInWithEmailAndPassword(email, sifre)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Kullanıcı başarıyla giriş yaptı, diğer sayfaya geçiş yapın
                        Toast.makeText(this, "Kurumsal Giriş Başarılı", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, KurumsalHome::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Giriş başarısız oldu, hata mesajını gösterin
                        Toast.makeText(this, "Giriş başarısız. Lütfen bilgilerinizi kontrol edin.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        forgotPassword.setOnClickListener {
            // Butona tıklandığında yapılacak işlemler
            Toast.makeText(this, "Şifremi Unuttum Sayfası", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
