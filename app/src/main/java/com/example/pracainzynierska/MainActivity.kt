package com.example.pracainzynierska


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


/**
 *
 * @author: Karol
 * @version 1.0.0
 * Ustawiany jest tutaj widok activity_main, na którym później będą malowane inne widoki przy użyciu fragmentów
 */

class MainActivity : AppCompatActivity() {
    /**
     * Ta metoda ustawia widok activity_main
     * chowane są niektóre elementy aplikacji
     * wymuszenie orientacji poziomej
     * @param savedInstanceState
     * @see AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Chowanie ActionBar i zmiana orientacji na poziomą
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContentView(R.layout.activity_main)

    }



}