package com.example.carwashing

import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KurumsalCardActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var companyNameTextView: TextView
    private lateinit var serviceSpinner: Spinner
    private lateinit var yesCheckBox: CheckBox
    private lateinit var noCheckBox: CheckBox
    private lateinit var bookAppointmentButton: Button

    private lateinit var timeSlots: List<TextView>
    private var selectedTimeSlot: TextView? = null

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kurumsal_card)

        profileImageView = findViewById(R.id.profileImageView)
        companyNameTextView = findViewById(R.id.companyNameTextView)
        serviceSpinner = findViewById(R.id.serviceSpinner)
        yesCheckBox = findViewById(R.id.yesCheckBox)
        noCheckBox = findViewById(R.id.noCheckBox)
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton)

        bookAppointmentButton.backgroundTintList = resources.getColorStateList(R.color.black)

        timeSlots = listOf(
            findViewById(R.id.timeSlot1),
            findViewById(R.id.timeSlot2),
            findViewById(R.id.timeSlot3),
            findViewById(R.id.timeSlot4),
            findViewById(R.id.timeSlot5),
            findViewById(R.id.timeSlot6),
            findViewById(R.id.timeSlot7),
            findViewById(R.id.timeSlot8),
            findViewById(R.id.timeSlot9)
        )

        timeSlots.forEach { timeSlot ->
            timeSlot.setOnClickListener { onTimeSlotSelected(timeSlot) }
        }

        val companyName = intent.getStringExtra("companyName")
        val companyProfileImageUrl = intent.getStringExtra("profileImageUrl")
        val sellerId = intent.getStringExtra("sellerId") // Satıcı UID'si

        companyNameTextView.text = companyName
        companyProfileImageUrl?.let {
            Glide.with(this).load(it).into(profileImageView)
        }

        bookAppointmentButton.setOnClickListener {
            fetchUserDetailsAndBookAppointment(sellerId, companyProfileImageUrl)
        }
    }

    private fun onTimeSlotSelected(timeSlot: TextView) {
        selectedTimeSlot?.setBackgroundResource(android.R.color.transparent) // Önceki seçimi temizle
        timeSlot.setBackgroundResource(R.drawable.selected_time_slot_background) // Yeni seçimi vurgula
        selectedTimeSlot = timeSlot
    }

    private fun fetchUserDetailsAndBookAppointment(sellerId: String?, companyProfileImageUrl: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null && sellerId != null && selectedTimeSlot != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId)
                .collection("bireysel").document("kullaniciBilgileri").get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: "Ad"
                        val surname = document.getString("surname") ?: "Soyad"
                        val userProfileImageUrl = document.getString("profileImageUrl")

                        bookAppointment(sellerId, name, surname, userProfileImageUrl, companyProfileImageUrl)
                    } else {
                        Toast.makeText(this, "Kullanıcı bilgileri alınamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Kullanıcı bilgileri alınamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Kullanıcı oturumu açık değil, satıcı bilgisi eksik veya saat seçilmedi.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun bookAppointment(sellerId: String, name: String, surname: String, userProfileImageUrl: String?, companyProfileImageUrl: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null && selectedTimeSlot != null) {
            val userId = currentUser.uid
            val selectedService = serviceSpinner.selectedItem.toString()
            val valeService = if (yesCheckBox.isChecked) "Evet" else "Hayır"
            val selectedTime = selectedTimeSlot?.text.toString()
            val timestamp = System.currentTimeMillis()
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val appointment = hashMapOf(
                "name" to name,
                "surname" to surname,
                "selectedService" to selectedService,
                "valeService" to valeService,
                "selectedTime" to selectedTime,
                "timestamp" to timestamp,
                "date" to date,
                "userProfileImageUrl" to userProfileImageUrl,
                "companyProfileImageUrl" to companyProfileImageUrl,
                "companyName" to intent.getStringExtra("companyName"),
                "userId" to userId,
                "sellerId" to sellerId
            )

            db.collection("randevular")
                .add(appointment)
                .addOnSuccessListener {
                    Toast.makeText(this, "Randevu başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Randevu oluşturulamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Kullanıcı oturumu açık değil veya saat seçilmedi.", Toast.LENGTH_SHORT).show()
        }
    }
}
