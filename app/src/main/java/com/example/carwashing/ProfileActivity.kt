package com.example.carwashing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide


class ProfileFragment : Fragment() {

    private lateinit var companyNameTextView: TextView
    private lateinit var imageView: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.kurumsalprofile, container, false)

        // Status bar rengini değiştirmek için window özelliğini kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window? = activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = resources.getColor(R.color.status_bar_color) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }


        imageView = view.findViewById(R.id.circularImage)



        val editProfileButton: Button = view.findViewById(R.id.button1)
        editProfileButton.setOnClickListener {
            // Profil düzenleme sayfasına geçiş yap
            val intent = Intent(context, ProfileInfoActivity::class.java)
            startActivity(intent)
        }

        val about: Button = view.findViewById(R.id.button2)
        about.setOnClickListener {
            // Profil düzenleme sayfasına geçiş yap
            val intent = Intent(context, AboutUsActivity::class.java)
            startActivity(intent)
        }

        val private: Button = view.findViewById(R.id.button3)
        private.setOnClickListener {
            // Profil düzenleme sayfasına geçiş yap
            val intent = Intent(context, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        val security: Button = view.findViewById(R.id.button4)
        security.setOnClickListener {
            // Profil düzenleme sayfasına geçiş yap
            val intent = Intent(context, SecurityActivity::class.java)
            startActivity(intent)
        }



        // Giriş yapan kullanıcının CompanyName bilgisini alıp textView'e yerleştirme
        companyNameTextView = view.findViewById(R.id.textView)

        // Firebase Authentication ve Firestore referansları
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val logout: Button = view.findViewById(R.id.button5)
        logout.setOnClickListener {

            auth.signOut()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)

        }



        // Giriş yapmış kullanıcının UID'sini al
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            firestore.collection("users")
                .document(uid)
                .collection("bireysel")
                .document("kullaniciBilgileri")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val surname = document.getString("surname")
                        companyNameTextView.text = name + " " + surname

                        val profileImageUrl = document.getString("profileImageUrl")
                        profileImageUrl?.let {
                            // Glide ile resmi yükle
                            Glide.with(this)
                                .load(it) // Resmin URL'si
                                .circleCrop() // Resmi yuvarlak yap
                                .into(imageView) // ImageView'e yükle

                        }


                    } else {
                        // Belirtilen belge bulunamadı veya yok
                        // Kullanıcıya uygun bir geri bildirim sağlayın
                        Toast.makeText(context, "name verisi alınamadı!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Firestore'dan veri alırken bir hata oluştu
                    // Hata durumunda kullanıcıya uygun bir geri bildirim sağlayın
                    Toast.makeText(context, "Veri alınamadı: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("KurumsalProfileFragment", "Veri alınamadı: ${exception.message}")
                }
        }
        return view
    }
}

