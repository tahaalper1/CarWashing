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

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
        val surnameEditText: EditText = findViewById(R.id.editText2)
        val emailEditText: EditText = findViewById(R.id.editText3) // Username yerine email
        val passwordEditText: EditText = findViewById(R.id.editText4)
        val confirmPasswordEditText: EditText = findViewById(R.id.editText5)

        val signupButton: Button = findViewById(R.id.signupButton)
        signupButton.backgroundTintList = resources.getColorStateList(R.color.black)

        signupButton.setOnClickListener {

            val name = nameEditText.text.toString()
            val surname = surnameEditText.text.toString()
            val email = emailEditText.text.toString() // Username yerine email
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Şifrelerin doğruluğunu kontrol et
            if (password != confirmPassword) {
                // Şifreler eşleşmiyorsa kullanıcıya uyarı ver
                // Burada bir Toast mesajı veya bir AlertDialog gösterebilirsiniz
                Toast.makeText(this, "Şifreler eşleşmiyor!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = hashMapOf(
                            "name" to name,
                            "surname" to surname,
                            "email" to email,
                            "password" to password
                        )
                        firestore.collection("users")
                            .document(auth.currentUser?.uid ?: "")
                            .collection("bireysel")
                            .document("kullaniciBilgileri")
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                                onBackPressed()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Kayıt başarısız! ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Kayıt başarısız! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
