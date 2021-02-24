package com.dpplatform.oceancampus.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dpplatform.oceancampus.R
import com.dpplatform.oceancampus.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        comment_btn_send?.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = comment_edit_message.text.toString()
            comment.timestamp = System.currentTimeMillis()


            
        }


    }
}