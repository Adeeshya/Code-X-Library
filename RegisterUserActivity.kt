package com.example.libraryappcodex

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.libraryappcodex.databinding.ActivityRegisterUserBinding
import com.google.firebase.database.FirebaseDatabase

class RegisterUserActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityRegisterUserBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth




    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog , will show while creating account | Register Use
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle back button
        binding.backBtn.setOnClickListener {
            onBackPressed()//go to previous screen
        }

        //handle click , begin register
        binding.registerBtn.setOnClickListener {
            //steps
            /*
            * 1)Input data
            * 2)validate data
            * 3)create account-Firebase Auth
            * 4)save user info - firebase realtime databse*/
            validateData()
        }
    }

    private var name=""
    private var email=""
    private var password=""
    private var batch=""

    private fun validateData() {
        //1)Input data
        name = binding.nameEt.text.toString().trim()
        email = binding.universityIdEt.text.toString().trim()
        batch = binding.batchEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        var cPassword = binding.cPasswordEt.text.toString().trim()

        // 2)validate data
        if (name.isEmpty()){
            //check name empty
            Toast.makeText(this,"Enter User Name", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern
            Toast.makeText(this,"Invalid Email pattern", Toast.LENGTH_SHORT).show()
        }else if (batch.isEmpty()){
            //check name empty
            Toast.makeText(this,"Enter Batch", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            //password is empty
            Toast.makeText(this,"Enter Password..", Toast.LENGTH_SHORT).show()
        }else if(cPassword.isEmpty()){
            //password is empty
            Toast.makeText(this,"Confirm Password", Toast.LENGTH_SHORT).show()
        }else if(password!=cPassword){
            //matching password and confirm password
            Toast.makeText(this,"Password and Confirm Password doesn't match..", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }

        //4)save user info - firebase realtime databse
    }

    private fun createUserAccount() {
        // 3)create account-Firebase Auth

        //show progressbar
        progressDialog.setMessage("Creating Account..")
        progressDialog.show()

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //account created successfully,now add user to db
                updateUserInfo()
            }
            .addOnFailureListener { e->
                //failed creating account
                progressDialog.dismiss()
                Toast.makeText(this,"Failed Creating User Account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateUserInfo() {
        //4)save user info - firebase realtime databse
        progressDialog.setMessage("Saving your info..")

        //timestamp
        val timestamp= System.currentTimeMillis()

        //get current user uid , since user is registered so we can get it now
        val uid=firebaseAuth.uid

        //setup data to add in db
        val hashMap:HashMap<String,Any?> = HashMap()
        hashMap["uid"]=uid
        hashMap["email"]=email
        hashMap["name"]=name
        hashMap["batch"]=batch
        hashMap["profileImage"]=""//add empty , do in profile edit
        hashMap["userType"]="user" //possible values admin/user, change it manually on firebase
        hashMap["timestamp"]=timestamp

        //set data into db
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //user info saved , open user databse
                progressDialog.dismiss()
                Toast.makeText(this,"Account Created ...", Toast.LENGTH_SHORT).show()
                //startActivity(Intent(this@RegisterUserActivity, HomeUserActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this,"Failed Saving user Info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}