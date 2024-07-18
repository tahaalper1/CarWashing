package com.example.carwashing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.os.Build
import android.view.Window
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val individualButton: Button = findViewById(R.id.individualButton)
        val corporateButton: Button = findViewById(R.id.corporateButton)
        val auth = FirebaseAuth.getInstance()

        // Status bar rengini değiştirmek için window özelliğini kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.black) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }

        // Bireysel Kullanıcı butonunun arka plan rengini değiştir
        individualButton.backgroundTintList = resources.getColorStateList(R.color.black)

        // Kurumsal Kullanıcı butonunun arka plan rengini değiştir
        corporateButton.backgroundTintList = resources.getColorStateList(R.color.black)

        individualButton.setOnClickListener {
            // Butona tıklandığında yapılacak işlemler
            if (auth.currentUser != null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Bireysel Kullanıcı butonuna tıklandı", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        corporateButton.setOnClickListener {
            // Butona tıklandığında yapılacak işlemler
            if (auth.currentUser != null) {
                val intent = Intent(this, KurumsalHome::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Kurumsal Kullanıcı butonuna tıklandı", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, KurumsalLogin::class.java)
                startActivity(intent)
            }
        }
    }
}
