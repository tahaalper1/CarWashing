package com.example.carwashing

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var appointmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.status_bar_color)
        }

        val titleImageView = findViewById<ImageView>(R.id.titleImageView)
        val pageImageView = findViewById<ImageView>(R.id.pageImageView)
        val companyNameTextView = findViewById<TextView>(R.id.companyNameTextView)
        val cleaningTypeTextView = findViewById<TextView>(R.id.cleaningTypeTextView)
        val appointmentDateTextView = findViewById<TextView>(R.id.appointmentDateTextView)
        val appointmentTimeTextView = findViewById<TextView>(R.id.appointmentTimeTextView)
        val valetServiceTextView = findViewById<TextView>(R.id.valetServiceTextView)
        val completeButton = findViewById<Button>(R.id.completeButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        completeButton.backgroundTintList = resources.getColorStateList(R.color.green)
        cancelButton.backgroundTintList = resources.getColorStateList(R.color.red)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Retrieve data passed from the previous activity
        val bundle = intent.extras
        if (bundle != null) {
            val userImageUrl = bundle.getString("userImageUrl")
            val name = bundle.getString("name")
            val surname = bundle.getString("surname")
            val appointmentTime = bundle.getString("appointmentTime")
            val appointmentDate = bundle.getString("appointmentDate")
            val selectedService = bundle.getString("selectedService")
            val valeService = bundle.getString("valeService")
            appointmentId = bundle.getString("appointmentId")

            companyNameTextView.text = "$name $surname"
            cleaningTypeTextView.text = selectedService
            appointmentDateTextView.text = appointmentDate
            appointmentTimeTextView.text = appointmentTime
            valetServiceTextView.text = valeService

            userImageUrl?.let {
                Glide.with(this)
                    .load(it)
                    .circleCrop()
                    .into(pageImageView)
            }
        }

        // Handle button clicks
        completeButton.setOnClickListener {
            appointmentId?.let { id ->
                completeAppointment(id)
            } ?: run {
                showToast("Randevu ID'si bulunamadı.")
            }
        }

        cancelButton.setOnClickListener {
            appointmentId?.let { id ->
                cancelAppointment(id)
            } ?: run {
                showToast("Randevu ID'si bulunamadı.")
            }
        }
    }

    private fun completeAppointment(appointmentId: String) {
        val appointmentsRef = db.collection("randevular")
        val pastAppointmentsRef = db.collection("gecmisRandevular")
        appointmentsRef.document(appointmentId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val data = document.data!!
                pastAppointmentsRef.document(appointmentId).set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        appointmentsRef.document(appointmentId).delete()
                            .addOnSuccessListener {
                                showToast("Randevu başarıyla tamamlandı.")
                            }
                            .addOnFailureListener { e ->

                                showToast("Randevu silinirken hata oluştu: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        showToast("Geçmiş randevu eklenirken hata oluştu: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            showToast("Randevu alınırken hata oluştu: ${e.message}")
        }
    }

    private fun cancelAppointment(appointmentId: String) {
        val appointmentsRef = db.collection("randevular")
        appointmentsRef.document(appointmentId).delete()
            .addOnSuccessListener {
                showToast("Randevu başarıyla iptal edildi.")
            }
            .addOnFailureListener { e ->
                showToast("Randevu iptal edilirken hata oluştu: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
