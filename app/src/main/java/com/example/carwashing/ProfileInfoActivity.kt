package com.example.carwashing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileInfoActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private lateinit var imageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var updateButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileinfo)

        imageView = findViewById(R.id.profileImage)
        val selectButton: Button = findViewById(R.id.selectImageButton)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        updateButton = findViewById(R.id.updateButton)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        updateButton.backgroundTintList = resources.getColorStateList(R.color.black)

        selectButton.setOnClickListener {
            openGallery()
        }

        imageView.setOnClickListener {
            openGallery()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.status_bar_color)
        }

        showUserInfo()

        updateButton.setOnClickListener {
            updateUserInfo()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data!!
            val currentUser = auth.currentUser
            val uid = currentUser?.uid
            uid?.let {
                val userImageRef = storageRef.child("images/$uid/profile.jpg")
                val uploadTask = userImageRef.putFile(filePath)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        imageView.setImageURI(filePath)
                        saveImageUriToUserInfo(uri.toString())
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Resim yüklenirken bir hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveImageUriToUserInfo(uri: String) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        uid?.let {
            val userRef = firestore.collection("users").document(uid).collection("bireysel").document("kullaniciBilgileri")
            userRef.update("profileImageUrl", uri)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profil resmi güncellendi.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Profil resmi güncellenirken bir hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showUserInfo() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        uid?.let {
            val userRef = firestore.collection("users").document(uid).collection("bireysel").document("kullaniciBilgileri")
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val profileImageUrl = document.getString("profileImageUrl")
                    val surname = document.getString("surname")
                    val address = document.getString("Address")

                    name?.let {
                        nameEditText.setText(it)
                    }
                    surname?.let {
                        surnameEditText.setText(it)
                    }
                    email?.let {
                        emailEditText.setText(it)
                    }
                    profileImageUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .circleCrop()
                            .into(imageView)
                    }
                    address?.let {
                        addressEditText.setText(it)
                    }
                } else {
                    Toast.makeText(this, "Kullanıcı bilgileri bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Kullanıcı bilgileri alınırken bir hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserInfo() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        uid?.let {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val surname = surnameEditText.text.toString()
            val address = addressEditText.text.toString().ifEmpty { "Adres bilgisi girilmedi" }
            val userRef = firestore.collection("users").document(uid).collection("bireysel").document("kullaniciBilgileri")
            val updates = hashMapOf<String, Any>(
                "name" to name,
                "surname" to surname,
                "email" to email,
                "Address" to address
            )
            userRef.update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Kullanıcı bilgileri güncellendi.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Kullanıcı bilgileri güncellenirken bir hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
