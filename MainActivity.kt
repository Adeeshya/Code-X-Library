package com.example.libraryappcodex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.libraryappcodex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // view binding
    private  lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //handle click ,login user
        binding.userLoginBtn.setOnClickListener{
            startActivity(Intent(this, LoginUserActivity::class.java))
        }

        //handle click login admin
        binding.adminLoginBtn.setOnClickListener{
            startActivity(Intent(this, LoginAdminActivity::class.java))
        }


        //handle click user register
        binding.nameTv.setOnClickListener{
            startActivity(Intent(this, RegisterUserActivity::class.java))
        }


    }
}