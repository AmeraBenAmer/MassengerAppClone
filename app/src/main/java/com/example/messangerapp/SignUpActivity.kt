package com.example.messangerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.messangerapp.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.edit_text_email

class SignUpActivity : AppCompatActivity() , TextWatcher{

    private val fAuth :FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseInstant: FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }

//  shortcut  for create collection and document.
    private val countUserDocumentRef: DocumentReference
    get() = firebaseInstant.document("users/${fAuth.currentUser?.uid.toString()}")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

//    for create collection and document.
//        firebaseInstant.collection("users").document(fAuth.currentUser?.uid.toString())

        edit_name_sign_up.addTextChangedListener(this)
        edit_pass_up.addTextChangedListener(this)
        edit_text_email.addTextChangedListener(this)

        btn_sign_up.setOnClickListener {
            val name = edit_name_sign_up.text.toString().trim()
            val password = edit_pass_up.text.toString().trim()
            val email = edit_text_email.text.toString().trim()

            if (name.isEmpty()){
                edit_name_sign_up.error = "Name is required"
                edit_name_sign_up.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()){
                edit_text_email.error = "Email is required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edit_text_email.error = "Please enter a valid email"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6){
            edit_pass_up.error = "6 Char required"
            edit_pass_up.requestFocus()
            return@setOnClickListener
        }
            createNewAccount(name, email, password)
        }
    }

    private fun  createNewAccount (name :String, email:String, password:String){
        progress_bar_sign_up.visibility = View.VISIBLE
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{

            val newUser = Users(name,"")
            countUserDocumentRef.set(newUser)
            if (it.isSuccessful){
                progress_bar_sign_up.visibility = View.INVISIBLE

                val intentMainActivity = Intent(this, MainActivity::class.java)
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMainActivity)
            }else{
                progress_bar_sign_up.visibility = View.INVISIBLE
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        btn_sign_up.isEnabled = edit_name_sign_up.text.trim().isNotEmpty()
                && edit_pass_up.text.trim().isNotEmpty()
                && edit_text_email.text.trim().isNotEmpty()

    }
}
