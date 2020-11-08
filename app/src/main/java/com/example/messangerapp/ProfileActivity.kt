package com.example.messangerapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.messangerapp.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileActivity : AppCompatActivity() {

    companion object{
        val RESULT_CODE_SELECT_IMAGE = 2
    }

    private val fAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var nameOfUser: String

    private  val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserDocRef: DocumentReference
    get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserStorageRef:StorageReference
        get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        botton sign out
        btn_sign_out.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
//            val signInActivity = Intent(this,SignInActivity::class.java )
//            //                when user click back not back to sign in activity
//            signInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(signInActivity)
            startActivity(Intent(this@ProfileActivity, SignInActivity::class.java))
            finish()

        }


        //        change color icons in state bar M sdk = 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }  else{
            window.statusBarColor = Color.WHITE
        }

        image_profile_user.setOnClickListener {
            val myIntentImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(myIntentImage, "Select Image"), RESULT_CODE_SELECT_IMAGE)

        }
        //for action bar
        setSupportActionBar(profile_toolbar)
        supportActionBar?.title = "Me"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // call function get Info Users
        getUserInfo { user ->
            nameOfUser =  user.name

            name_profile_user.text = nameOfUser

            if (user.profileImage.isNotEmpty()){
                Glide.with(this)
                    .load(storageInstance.getReference(user.profileImage))
                    .placeholder(R.drawable.ic_account_circle)
                    .into(image_profile_user)
            }

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_SELECT_IMAGE && requestCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {

            progress_bar_profile.visibility = View.VISIBLE

            image_profile_user.setImageURI(data.data)

            // تقليص الصورة
            val selectedImagePath = data.data
            val selectedImageBmp =
                MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            image_profile_user.setImageURI(data.data)

            uploadProfileImage(selectedImageBytes) {path->

                // input path image to database  in field user
                val userFieldMap = mutableMapOf<String, Any>()
                userFieldMap["name"] = nameOfUser
                userFieldMap["profileImage"] = path
                currentUserDocRef.update(userFieldMap)



            }
        }
    }
    private fun uploadProfileImage(selectedImageBytes: ByteArray, onSuccess:(imagePath: String)->Unit) {

        val ref = currentUserStorageRef.child("profilePictures/${UUID.nameUUIDFromBytes(selectedImageBytes)}")

        ref.putBytes(selectedImageBytes).addOnCompleteListener{
            if (it.isSuccessful) {
                onSuccess(ref.path)
                progress_bar_profile.visibility = View.GONE
            }else
                Toast.makeText(this, "ERROR :${it.exception?.message.toString()}", Toast.LENGTH_LONG).show()
            }
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home ->{
                finish()
                return true
            }
        }
        return false
    }

    private fun getUserInfo(onComplete:(Users)->Unit){
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(Users::class.java)!!)
        }
    }
}
