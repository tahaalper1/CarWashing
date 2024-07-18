package com.example.carwashing
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat

import com.example.carwashing.ui.GuncelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Status bar rengini değiştirmek için window özelliğini kullan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.status_bar_color)) // R.color.status_bar_color, istediğiniz renk kaynağının ID'si olmalı
        }

        val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottom_navigation.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomePageFragment()).commit()
                    true
                }

                R.id.navigation_current -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GuncelFragment()).commit()
                    true
                }

                R.id.navigation_dashboard -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GecmisFragment()).commit()
                    true
                }
                R.id.navigation_notifications -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
                    true
                }
                else -> false
            }
        }


    }
}