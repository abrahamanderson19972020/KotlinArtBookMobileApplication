package com.artbook.artbookmobileapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artbook.mobileapp.R
import com.artbook.mobileapp.databinding.ActivityArtBookBinding

class ArtBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtBookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArtBookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fun save(view: View){

        }

        fun selectImage(view:View){

        }
    }
}