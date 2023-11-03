package com.example.dine_aid

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dine_aid.R

class FullScreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreen_image_activity)

        val imageView = findViewById<ImageView>(R.id.fullscreenImageIV)
        val imageUrl = intent.getStringExtra("image_url")

        if (imageUrl != null) {
            // Laden und Anzeigen des Bildes in voller Größe
            Glide.with(this).load(imageUrl).into(imageView)
        }
    }
}
