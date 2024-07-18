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

class KurumsalProfileInfo : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private lateinit var imageView: ImageView
    private lateinit var companyNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var appointmentEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var updateButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kurumsalprofileinfo)

        imageView = findViewById(R.id.profileImage)
        val selectButton: Button = findViewById(R.id.selectImageButton)
        companyNameEditText = findViewById(R.id.companyNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        appointmentEditText = findViewById(R.id.appointmentEditText)
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
            val userRef = firestore.collection("users").document(uid).collection("kurumsal").document("kullaniciBilgileri")
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
            val userRef = firestore.collection("users").document(uid).collection("kurumsal").document("kullaniciBilgileri")
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val companyName = document.getString("CompanyName")
                    val email = document.getString("email")
                    val profileImageUrl = document.getString("profileImageUrl")
                    val randevu = document.getLong("RandevuSayi")
                    val address = document.getString("Address")

                    companyName?.let {
                        companyNameEditText.setText(it)
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
                    randevu?.let {
                        appointmentEditText.setText(it.toString())
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
            val companyName = companyNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val randevu = appointmentEditText.text.toString().toInt()
            val address = addressEditText.text.toString()
            val userRef = firestore.collection("users").document(uid).collection("kurumsal").document("kullaniciBilgileri")
            val updates = hashMapOf<String, Any>(
                "CompanyName" to companyName,
                "email" to email,
                "RandevuSayi" to randevu,
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
