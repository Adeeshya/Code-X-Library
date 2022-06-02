package com.example.libraryappcodex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.libraryappcodex.databinding.ActivityAccountAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AccountAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAccountAdminBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        getuserdata()

        //hancle click, logout
        binding.logout.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        //handle click account
        binding.accountBtn.setOnClickListener{
            startActivity(Intent(this, AccountAdminActivity::class.java))
        }
        //handle click home
        binding.homeBtn.setOnClickListener{
            startActivity(Intent(this, DashboardAdminActivity::class.java))
        }

    }

    private fun getuserdata() {
        val uid=firebaseAuth.uid

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        if (uid != null) {
            ref.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = "${snapshot.child("name").value}"

                        binding.usernameTv.text=username
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser==null){
            //not logged in , go to main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            //logged in get and show user info
            val email = firebaseUser.email
            //set to textview of toolbar
            binding.useremailTv.text=email
        }
    }
}
