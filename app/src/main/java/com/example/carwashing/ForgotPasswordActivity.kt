package com.example.carwashing

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener




class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        auth = FirebaseAuth.getInstance()

        // Status bar rengini değiştirmek için window özelliğini kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.black)) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }


        val emailEditText: EditText = findViewById(R.id.mailEdittext)
        val sendButton: Button = findViewById(R.id.forgotButton)

        sendButton.backgroundTintList = resources.getColorStateList(R.color.black)

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Şifre sıfırlama bağlantısı gönderilemedi. Lütfen e-posta adresinizi kontrol edin.", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
