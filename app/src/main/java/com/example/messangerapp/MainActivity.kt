package com.example.messangerapp

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.messangerapp.model.Users
import com.example.messangerapp.views.ChatFragment
import com.example.messangerapp.views.MoreFragment
import com.example.messangerapp.views.PeopleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val mChatFragment = ChatFragment()
    private val mPeopleFragment = PeopleFragment()
    private val mMoreFragment = MoreFragment()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        get image profile from firebase
        firestoreInstance.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(Users::class.java)
                if (user!!.profileImage.isNotEmpty()){
                    Glide.with(this)
                        .load(storageInstance.getReference(user.profileImage))
                        .into(image_main_profile_user)
                }else{

                    image_main_profile_user.setImageResource(R.drawable.ic_account_circle)
                }
            }
//        action bar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""


//        change color icons in state bar M sdk = 23
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }  else{
            window.statusBarColor = Color.WHITE
    }


        nav_bottom.setOnNavigationItemSelectedListener(this)

//        Chat Fragment as default
        setFragment(mChatFragment)





    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.navigation_chat_item ->{
                setFragment(mChatFragment)
                true
            }
            R.id.navigation_people_item ->{
                setFragment(mPeopleFragment)
                true
            }
            R.id.navigation_more_item ->{
                setFragment(mMoreFragment)
                true
            }
            else -> false
        }

    }

      fun setFragment(mFragment: Fragment) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout ,mFragment)
        fragmentTransaction.commit()
    }

}


