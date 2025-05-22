package com.artbook.artbookmobileapp

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.artbook.artbookmobileapp.databinding.ActivityArtListBinding

class ArtListActivity:AppCompatActivity() {
    private lateinit var binding: ActivityArtListBinding
    private lateinit var artList: ArrayList<Art>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artList = ArrayList()
        val database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null)
        val cursor = database.rawQuery("SELECT * FROM arts", null)
        val artNameIx = cursor.getColumnIndex("artname")
        val artistIx = cursor.getColumnIndex("artistname")
        val yearIx = cursor.getColumnIndex("year")
        val idIx = cursor.getColumnIndex("id")
        val imageIx = cursor.getColumnIndex("image")

        while (cursor.moveToNext()) {
            val name = cursor.getString(artNameIx)
            val artist = cursor.getString(artistIx)
            val year = cursor.getString(yearIx)
            val id = cursor.getInt(idIx)
            val imageByteArray = cursor.getBlob(imageIx)
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            artList.add(Art(id, name, artist, year, bitmap))
        }

        cursor.close()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ArtAdapter(artList)
    }
}