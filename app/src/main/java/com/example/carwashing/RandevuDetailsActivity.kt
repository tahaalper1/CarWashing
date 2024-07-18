package com.example.carwashing

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RandevuDetailsActivity : AppCompatActivity() {

    private lateinit var titleImageView: ImageView
    private lateinit var pageImageView: ImageView
    private lateinit var cleaningTypeTextView: TextView
    private lateinit var appointmentDateTextView: TextView
    private lateinit var appointmentTimeTextView: TextView
    private lateinit var valetServiceTextView: TextView
    private lateinit var companyNameTextView: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_randevu_details)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.status_bar_color)) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }

        // Initialize views
        titleImageView = findViewById(R.id.titleImageView)
        pageImageView = findViewById(R.id.pageImageView)
        cleaningTypeTextView = findViewById(R.id.cleaningTypeTextView)
        appointmentDateTextView = findViewById(R.id.appointmentDateTextView)
        appointmentTimeTextView = findViewById(R.id.appointmentTimeTextView)
        valetServiceTextView = findViewById(R.id.valetServiceTextView)
        companyNameTextView = findViewById(R.id.companyNameTextView)

        // Load the appointment details
        loadAppointmentDetails()
    }

    private fun loadAppointmentDetails() {
        val appointmentId = intent.getStringExtra("appointmentId")
        if (appointmentId != null) {
            db.collection("randevular").document(appointmentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val profileImageUrl = document.getString("companyProfileImageUrl")
                        val companyName = document.getString("companyName")
                        val appointmentTime = document.getString("selectedTime")
                        val appointmentDate = document.getString("date")
                        val cleaningType = document.getString("selectedService")
                        val valetService = document.getString("valeService")
                        appointmentTimeTextView.text = appointmentTime
                        appointmentDateTextView.text = appointmentDate
                        cleaningTypeTextView.text = cleaningType
                        valetServiceTextView.text = valetService
                        companyNameTextView.text = companyName
                        profileImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .into(pageImageView)
                        }
                    } else {
                        Toast.makeText(this, "Randevu bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Randevu detayları yüklenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Randevu ID alınamadı", Toast.LENGTH_SHORT).show()
        }
    }
}
