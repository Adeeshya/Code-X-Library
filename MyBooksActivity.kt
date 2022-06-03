package com.example.libraryappcodex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryappcodex.adapter.AdapterReadLaterBooks
import com.example.libraryappcodex.adapter.AdapterReservedBooks
import com.example.libraryappcodex.databinding.ActivityMyBooksBinding
import com.example.libraryappcodex.model.ModelReadLaterBooks
import com.example.libraryappcodex.model.ModelReservedBooks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_books.*

class MyBooksActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityMyBooksBinding

    lateinit var mDataBase: DatabaseReference

    private lateinit var bookList:ArrayList<ModelReservedBooks>
    private lateinit var mAdapter:AdapterReservedBooks

    private lateinit var bookList2:ArrayList<ModelReadLaterBooks>
    private lateinit var nAdapter: AdapterReadLaterBooks

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        /**initialized*/
        bookList = ArrayList()
        mAdapter = AdapterReservedBooks(this,bookList)
        booksImagesRv.layoutManager = LinearLayoutManager(this)
        booksImagesRv.setHasFixedSize(true)
        booksImagesRv.adapter = mAdapter
        /**getData firebase*/
        getBooksData()

        /**initialized*/
        bookList2 = ArrayList()
        nAdapter = AdapterReadLaterBooks(this,bookList2)
        readlaterRv.layoutManager = LinearLayoutManager(this)
        readlaterRv.setHasFixedSize(true)
        readlaterRv.adapter = nAdapter
        /**getData firebase*/
        getReadLaterBooksData()

        //handle click home
        binding.homeBtn.setOnClickListener{
            startActivity(Intent(this, DashboardUserActivity::class.java))
        }
        //handle click mybooks
        binding.mybooksBtn.setOnClickListener{
            startActivity(Intent(this, MyBooksActivity::class.java))
        }
        //handle click notifications
        binding.notificationsBtn.setOnClickListener{
            startActivity(Intent(this, NotificationActivity2::class.java))
        }
        //handle click ebooks
        binding.ebooksBtn.setOnClickListener{
            startActivity(Intent(this, EbooksActivity::class.java))
        }
        //handle click account
        binding.accountBtn.setOnClickListener{
            startActivity(Intent(this, AccountUserActivity::class.java))
        }


    }

    private fun getReadLaterBooksData() {
        //uid of current user
        val uid=firebaseAuth.uid

        mDataBase = FirebaseDatabase.getInstance().getReference("ReadLater")
        mDataBase.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (bookSnapshot in snapshot.children){
                            val book = bookSnapshot.getValue(ModelReadLaterBooks::class.java)
                            bookList2.add(book!!)
                        }
                        readlaterRv.adapter = nAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MyBooksActivity,
                        error.message, Toast.LENGTH_SHORT).show()
                }

            })

    }



    private fun getBooksData() {
        //uid of current user
        val uid=firebaseAuth.uid

        mDataBase = FirebaseDatabase.getInstance().getReference("ReservedBooks")
        mDataBase.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (bookSnapshot in snapshot.children){
                            val book = bookSnapshot.getValue(ModelReservedBooks::class.java)
                            bookList.add(book!!)
                        }
                        booksImagesRv.adapter = mAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MyBooksActivity,
                        error.message, Toast.LENGTH_SHORT).show()
                }

            })


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
            //binding.usernameTv.text=email
        }
    }
}
