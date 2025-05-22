package com.artbook.artbookmobileapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artbook.artbookmobileapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()
    }

    fun save(view: View){
        val artName = binding.nameText.text.toString()
        val artistName = binding.artistText.text.toString()
        val year = binding.yearText.text.toString()
       if (selectedBitmap != null){
           val smallBitmap = makeSamallerBitmap(selectedBitmap!!, maximumSize = 300)
           val imageToByte = imageToByteArray(smallBitmap)
           try {
               val database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null)
               database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artname VARCHAR, artistname VARCHAR, year VARCHAR, image BLOB)")
               val sqlString = "INSERT INTO arts (artname, artistname, year, image) VALUES (?, ?, ?, ?)"
               val statement = database.compileStatement(sqlString)
               statement.bindString(1, artName)
               statement.bindString(2, artistName)
               statement.bindString(3, year)
               statement.bindBlob(4, imageToByte)
               statement.execute()
           }catch (e:Exception){
               e.printStackTrace()
           }
       }
    }

    private fun imageToByteArray(image:Bitmap):ByteArray{
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return byteArray
    }

    private fun makeSamallerBitmap(image:Bitmap, maximumSize:Int=300):Bitmap{
        var width = image.width
        var height = image.height
        val ratio = width.toDouble()/height.toDouble()
        if (ratio > 1){
            // Landscape
            width = maximumSize
            val scaledHeight = width / ratio
            height = scaledHeight.toInt()
        }
        else{
            // Portrait
            height = maximumSize
            val scaledWidth = height * ratio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }
    fun showArtList(view: View) {
        val intent = Intent(this, ArtListActivity::class.java)
        startActivity(intent)
    }

    fun selectImage(view: View){
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
                        permissionLauncher.launch(permission) // FIXED: Launch proper permission
                    }.show()
            } else {
                permissionLauncher.launch(permission) // FIXED
            }
       }
        else{
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
           activityResultLauncher.launch(intentToGallery)
       }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null) {
                    val imageData = intentFromResult.data
                    // binding.imageView.setImageURI(imageData)
                    if (imageData != null) {
                        try {
                            if(Build.VERSION.SDK_INT >= 28){
                                val source = ImageDecoder.createSource(
                                    this@MainActivity.contentResolver,
                                    imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                            else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result -> if(result){
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        } else {
            Toast.makeText(this@MainActivity, "Permission needed!", Toast.LENGTH_LONG).show()
        }
        }
    }
}