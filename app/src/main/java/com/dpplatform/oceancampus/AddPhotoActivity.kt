package com.dpplatform.oceancampus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
        //Initate storage
        storage = FirebaseStorage.getInstance()

        //Open the Album
        var PhotoPickerIntent = Intent(Intent.ACTION_PICK)
        PhotoPickerIntent.type = "image/*"
        startActivityForResult(PhotoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        //add image upload event
        addPhoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (requestCode == Activity.RESULT_OK) {
                photoUri = data?.data
                addPhoto_image.setImageURI(photoUri)

            } else {
                finish()
            }
        }
    }

    fun contentUpload() {

        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timeStamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //FileUpload
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success),Toast.LENGTH_SHORT).show()

        }
    }
}
