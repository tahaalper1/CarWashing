package com.example.carwashing.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.carwashing.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ImageView
import android.widget.TextView
import com.example.carwashing.RandevuDetailsActivity

class GuncelFragment : Fragment() {

    private lateinit var appointmentsContainer: LinearLayout
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_guncel, container, false)
        appointmentsContainer = view.findViewById(R.id.appointmentsContainer)
        loadUserAppointments()
        return view
    }

    private fun loadUserAppointments() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("randevular")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val appointmentCard = LayoutInflater.from(context).inflate(R.layout.item_appointment_card, appointmentsContainer, false)

                        val imageView = appointmentCard.findViewById<ImageView>(R.id.appointmentImageView)
                        val companyNameTextView = appointmentCard.findViewById<TextView>(R.id.companyNameTextView)
                        val appointmentTimeTextView = appointmentCard.findViewById<TextView>(R.id.appointmentTimeTextView)
                        val appointmentDateTextView = appointmentCard.findViewById<TextView>(R.id.appointmentDateTextView)

                        val profileImageUrl = document.getString("companyProfileImageUrl")
                        val companyName = document.getString("companyName")
                        val appointmentTime = document.getString("selectedTime")
                        val appointmentDate = document.getString("date")
                        val selectedService = document.getString("selectedService")
                        val valeService = document.getString("valeService")

                        companyNameTextView.text = companyName
                        companyNameTextView.setTextColor(resources.getColor(R.color.black))

                        appointmentTimeTextView.text = "Randevu Saati: $appointmentTime"
                        appointmentTimeTextView.setTextColor(resources.getColor(R.color.black))

                        appointmentDateTextView.text = "Randevu Tarihi: $appointmentDate"
                        appointmentDateTextView.setTextColor(resources.getColor(R.color.black))

                        profileImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .circleCrop()
                                .into(imageView)
                        }

                        appointmentCard.setOnClickListener {
                            val intent = Intent(context, RandevuDetailsActivity::class.java)
                            intent.putExtra("appointmentId", document.id)
                            startActivity(intent)
                        }
                        appointmentsContainer.addView(appointmentCard)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Randevular yüklenemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Kullanıcı oturumu açık değil.", Toast.LENGTH_SHORT).show()
        }
    }
}
