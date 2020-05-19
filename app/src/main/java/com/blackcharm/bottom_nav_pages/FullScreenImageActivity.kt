package com.blackcharm.bottom_nav_pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.blackcharm.R

class FullScreenImageActivity: AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreenimage)

        var fullScreenImageView: PhotoView = findViewById(R.id.fullscreenimage)

        val callingActivityIntent: Intent = getIntent()

        if(callingActivityIntent != null) {

            var imageUri: Uri = callingActivityIntent.getData()
            if(imageUri !=null && fullScreenImageView !=null) {
                Glide.with(this)
                    .load(imageUri)
                    .into(fullScreenImageView)
            }
        }




    }
    override fun onResume() {
        super.onResume()


    }

}