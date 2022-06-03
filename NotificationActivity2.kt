package com.example.libraryappcodex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.libraryappcodex.adapter.AdapterReadlaterNotification
import com.example.libraryappcodex.adapter.AdapterReservedNotification
import com.example.libraryappcodex.databinding.ActivityNotification2Binding
import com.example.libraryappcodex.model.ModelReadlaterNotification
import com.example.libraryappcodex.model.ModelReservedNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationActivity2 : AppCompatActivity(){

//view binding
private lateinit var binding: ActivityNotification2Binding

    private companion object{
        const val TAG="NOTIFICATION_LIST_TAG"
    }

    //arraaylist to hold list of data of type Modelpdf
    private lateinit var  pdfArrayList: ArrayList<ModelReadlaterNotification>

    // Adapter
    private lateinit var adapterPdf: AdapterReadlaterNotification;

    //reserved
    //arraaylist to hold list of data of type
    private lateinit var  pdfArrayList3: ArrayList<ModelReservedNotification>

    // Adapter
    private lateinit var adapterPdf3: AdapterReservedNotification;




    //firebase auth
private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotification2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        getReadLaterBooksData()
        getReservedBooksData()


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
        //init arraylist
        pdfArrayList= ArrayList()

        //uid of current user
        val uid=firebaseAuth.uid

        val ref = FirebaseDatabase.getInstance().getReference("ReadLater")
        ref.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        //get data
                        val model =ds.getValue(ModelReadlaterNotification::class.java)
                        //add to list
                        if (model!=null){
                            pdfArrayList.add(model)
                            Log.d(NotificationActivity2.TAG,"onDataChanage: ${model.booktitle}")
                        }
                    }
                    //setup adapter
                    adapterPdf= AdapterReadlaterNotification(this@NotificationActivity2,pdfArrayList)
                    binding.readlaterRv.adapter=adapterPdf
                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })

    }

    private fun getReservedBooksData() {
        //init arraylist
        pdfArrayList3= ArrayList()

        //uid of current user
        val uid=firebaseAuth.uid

        val ref = FirebaseDatabase.getInstance().getReference("ReservedBooks")
        ref.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    pdfArrayList3.clear()
                    for (ds in snapshot.children){
                        //get data
                        val model =ds.getValue(ModelReservedNotification::class.java)
                        //add to list
                        if (model!=null){
                            pdfArrayList3.add(model)
                            Log.d(NotificationActivity2.TAG,"onDataChanage: ${model.booktitle}")
                        }
                    }
                    //setup adapter
                    adapterPdf3= AdapterReservedNotification(this@NotificationActivity2,pdfArrayList3)
                    binding.reservedRv.adapter=adapterPdf3
                }

                override fun onCancelled(error: DatabaseError) {
                    //
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
            binding.usernameTv.text=email
        }
    }
}