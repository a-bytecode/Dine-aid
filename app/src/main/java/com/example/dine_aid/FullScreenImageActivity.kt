package com.example.dine_aid

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dine_aid.R

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector

    private var scaleFactor = 1.0f
    private var previousX = 0.0f
    private var previousY = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreen_image_activity)

        imageView = findViewById(R.id.fullscreenImageIV)
        val imageUrl = intent.getStringExtra("image_url")

        if (imageUrl != null) {

            Glide.with(this).load(imageUrl).into(imageView)

        }

        scaleGestureDetector = ScaleGestureDetector(this, MyScaleGestureListener())
        gestureDetector = GestureDetector(this,MyGestureListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - previousX
                val deltaY = event.y - previousY

                imageView.translationX += deltaX
                imageView.translationY += deltaY

                previousX = event.x
                previousY = event.y
            }
        }
        return true
    }

    inner class MyScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 3.0f))

            imageView.scaleX = scaleFactor
            imageView.scaleY = scaleFactor
            return true
        }
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {

            scaleFactor = 1.0f
            imageView.scaleX = scaleFactor
            imageView.scaleY = scaleFactor
            imageView.translationX = 0f
            imageView.translationY = 0f
            return true
        }
    }

}
