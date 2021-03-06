package com.example.libraryappcodex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.libraryappcodex.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardAdminActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityDashboardAdminBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()



        //handle click adddbook
        binding.addbookBtn.setOnClickListener{
            startActivity(Intent(this, AddNewBookActivity::class.java))
        }

        //handle click adddebook
        binding.addebookBtn.setOnClickListener{
            startActivity(Intent(this, AddBookActivity::class.java))
        }


        //handle click home
        binding.homeBtn.setOnClickListener{
            startActivity(Intent(this, DashboardAdminActivity::class.java))
        }
        //handle click account
        binding.accountBtn.setOnClickListener{
            startActivity(Intent(this, AccountAdminActivity::class.java))
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
            binding.usernameTv.text=email
        }
    }
}
