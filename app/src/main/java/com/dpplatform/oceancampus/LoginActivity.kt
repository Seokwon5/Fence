package com.dpplatform.oceancampus

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        button_signIn.setOnClickListener {
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("542623062976-b6n4caegjps5kurpfgnh2clmdg0mof62.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        printHashKey()

    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }



    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess) {
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)

            }else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }


        }
    }
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        moveMainPage(task.result?.user)
                    }else{
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

    }


    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    moveMainPage(task.result?.user)
                }else if (task.exception?.message.isNullOrEmpty()){
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }else{
                    signinEmail()
                }
        }

        }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
                ?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                moveMainPage(task.result?.user)
            }else{
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                     }
                 }

         }
    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
      }


    }

