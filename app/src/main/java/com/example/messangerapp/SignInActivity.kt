package com.example.messangerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.edit_text_email

class SignInActivity : AppCompatActivity(), TextWatcher {

    private val fAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        edit_text_email.addTextChangedListener(this)
        edit_pass_word.addTextChangedListener(this)

        btn_sign_in.setOnClickListener {
            val email = edit_text_email.text.toString()
            val password = edit_pass_word.text.toString()

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
                edit_pass_word.error = "6 Char required"
                edit_pass_word.requestFocus()
                return@setOnClickListener
            }
            
            signIn(email, password)
        }

        btn_create_new_account.setOnClickListener {
            val createNewAccount = Intent(this,SignUpActivity::class.java )
            startActivity(createNewAccount)
        }


    }

    private fun signIn(email: String, password: String) {

        progress_bar_sign_in.visibility = View.VISIBLE

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                progress_bar_sign_in.visibility = View.INVISIBLE

                val intentMainActivity = Intent(this, MainActivity::class.java)
//                when user click back not back to sign in activity
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMainActivity)
            }else{
                progress_bar_sign_in.visibility = View.INVISIBLE

                Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (fAuth.currentUser?.uid != null){
            val intentMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentMainActivity)
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        btn_sign_in.isEnabled =  edit_text_email.text.toString().trim().isNotEmpty()
                && edit_pass_word.text.toString().trim().isNotEmpty()
    }
}
