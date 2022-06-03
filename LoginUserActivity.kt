package com.example.libraryappcodex

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.libraryappcodex.databinding.ActivityLoginUserBinding
import com.example.libraryappcodex.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginUserActivity : AppCompatActivity() {
    // view binding
    private  lateinit var binding: ActivityLoginUserBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog , will show while login user | Registered User
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle click , begin register
        binding.signInBtn.setOnClickListener {
            //steps
            /*
            * 1)Input data
            * 2)validate data
            * 3)login -Firebase Auth
            * 4)check user type - firebase realtime databse
            *       if user - move to user dashboard
            *       if admin - move to admin dashboard*/
            validateData()
        }
        //handle back button
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))//go to previous screen
        }

    }

    private var email=""
    private var password=""

    private fun validateData() {
        //1)Input data
        email = binding.universityIdEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //2)validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern
            Toast.makeText(this,"Invalid Email pattern", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this,"Enter Passsword", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }
    }

    private fun loginUser() {
        //3)login -Firebase Auth

        //show progress
        progressDialog.setMessage("Login In..")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //login success
                checkUser()
            }
            .addOnFailureListener { e->
                //failed login
                progressDialog.dismiss()
                Toast.makeText(this,"Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun checkUser() {
        /*4)check user type - firebase realtime databse
       *       if user - move to user dashboard
       *       if admin - move to admin dashboard*/

        progressDialog.setMessage("Checking User..")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get usertype user or admin
                    val userType=snapshot.child("userType").value
                    if (userType=="user"){
                        //its simple user , redirect to user dashboard
                        startActivity(Intent(this@LoginUserActivity, DashboardUserActivity::class.java))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })


    }
}