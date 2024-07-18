package com.example.carwashing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class KurumsalHomePageFragment : Fragment() {

    private lateinit var appointmentsContainer: LinearLayout
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.kurumsalhomepage, container, false)

        // Status bar rengini değiştirmek için window özelliğini kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window? = activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = resources.getColor(R.color.status_bar_color) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }

        appointmentsContainer = view.findViewById(R.id.appointmentsContainer)
        loadSellerAppointments()

        return view
    }

    private fun loadSellerAppointments() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val sellerId = currentUser.uid

            db.collection("randevular")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val appointmentCard = LayoutInflater.from(context).inflate(R.layout.kurumsal_item_appointment_card, appointmentsContainer, false)

                        val imageView = appointmentCard.findViewById<ImageView>(R.id.appointmentImageView)
                        val nameTextView = appointmentCard.findViewById<TextView>(R.id.nameTextView)
                        val appointmentTimeTextView = appointmentCard.findViewById<TextView>(R.id.appointmentTimeTextView)
                        val appointmentDateTextView = appointmentCard.findViewById<TextView>(R.id.appointmentDateTextView)

                        val userImageUrl = document.getString("userProfileImageUrl")
                        val name = document.getString("name")
                        val surname = document.getString("surname")
                        val appointmentTime = document.getString("selectedTime")
                        val appointmentDate = document.getString("date")
                        val selectedService = document.getString("selectedService")
                        val valeService = document.getString("valeService")

                        nameTextView.text = "$name $surname"
                        nameTextView.setTextColor(resources.getColor(R.color.black))

                        appointmentTimeTextView.text = "Randevu Saati: $appointmentTime"
                        appointmentTimeTextView.setTextColor(resources.getColor(R.color.black))

                        appointmentDateTextView.text = "Randevu Tarihi: $appointmentDate"
                        appointmentDateTextView.setTextColor(resources.getColor(R.color.black))

                        userImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .circleCrop()
                                .into(imageView)
                        }

                        appointmentCard.setOnClickListener {
                            val intent = Intent(requireContext(), AppointmentDetailActivity::class.java).apply {
                                putExtra("userImageUrl", userImageUrl)
                                putExtra("name", name)
                                putExtra("surname", surname)
                                putExtra("appointmentTime", appointmentTime)
                                putExtra("appointmentDate", appointmentDate)
                                putExtra("selectedService", selectedService)
                                putExtra("valeService", valeService)
                                putExtra("appointmentId", document.id) // Randevu ID'sini ekle
                            }
                            startActivity(intent)
                        }


                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(0, 0, 0, 16) // Alt kısma 16dp margin ekler
                        appointmentCard.layoutParams = layoutParams

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
