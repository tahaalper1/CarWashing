package com.example.carwashing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class KurumsalRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kurumsalregister)

        // Status bar rengini değiştirmek için window özelliğini kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.black) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Input alanlarından kullanıcı bilgilerini al
        val nameEditText: EditText = findViewById(R.id.editText1)
        val emailEditText: EditText = findViewById(R.id.editText3) // Username yerine email
        val passwordEditText: EditText = findViewById(R.id.editText4)
        val confirmPasswordEditText: EditText = findViewById(R.id.editText5)

        val signupButton: Button = findViewById(R.id.signupButton)
        signupButton.backgroundTintList = resources.getColorStateList(R.color.black)

        signupButton.setOnClickListener {

            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString() // Username yerine email
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Şifrelerin doğruluğunu kontrol et
            if (password != confirmPassword) {
                // Şifreler eşleşmiyorsa kullanıcıya uyarı ver
                Toast.makeText(this, "Şifreler eşleşmiyor!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kullanıcıyı Firebase Authentication ile kaydet
            auth.createUserWithEmailAndPassword(email, password) // Username yerine email
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Firebase Authentication başarılı bir şekilde kaydedildiyse
                        val userId = auth.currentUser?.uid ?: ""
                        // Firestore'da kullanıcı bilgilerini kaydet
                        val user = hashMapOf(
                            "CompanyName" to name,
                            "email" to email, // Username yerine email
                            "password" to password, // Güvenlik açısından tavsiye edilmez!
                            "uid" to userId
                        )
                        // Users koleksiyonu altında yeni bir kurumsal koleksiyon oluştur
                        firestore.collection("users")
                            .document(userId)
                            .collection("kurumsal")
                            .document("kullaniciBilgileri")
                            .set(user)
                            .addOnSuccessListener {
                                // Firestore'da kullanıcı bilgileri başarılı bir şekilde kaydedildiyse
                                Toast.makeText(this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                                // Önceki sayfaya geri dön
                                onBackPressed()
                            }
                            .addOnFailureListener { e ->
                                // Firestore'da kullanıcı bilgileri kaydedilirken bir hata oluşursa
                                Toast.makeText(this, "Kayıt başarısız! ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Firebase Authentication sırasında bir hata oluşursa
                        Toast.makeText(this, "Kayıt başarısız! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
