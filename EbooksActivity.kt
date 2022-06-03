package com.example.libraryappcodex

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.libraryappcodex.databinding.ActivityEbooksBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EbooksActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityEbooksBinding


    private companion object{
        const val TAG="PDF_LIST_TAG"
    }

    //arraaylist to hold list of data of type Modelpdf
    private lateinit var  pdfArrayList: ArrayList<Modelpdf>

    // Adapter
    private lateinit var adapterPdf: AdapterPdf;

    //firebase auth
    //private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEbooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        //firebaseAuth = FirebaseAuth.getInstance()

        /*
        //set pdf category
        binding.subtitleTv.text=category
*/
        //load pdf/books
        loadPdfList()


        //search
        binding.searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //filter data
                try {
                    adapterPdf.filter!!.filter(s)
                }catch (e:Exception){
                    Log.d(TAG,"onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        })



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

    private fun loadPdfList() {
        //init arraylist
        pdfArrayList= ArrayList()

        val ref =FirebaseDatabase.getInstance().getReference("EBooks")
        ref.orderByChild("timestamp")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        //get data
                        val model =ds.getValue(Modelpdf::class.java)
                        //add to list
                        if (model!=null){
                            pdfArrayList.add(model)
                            Log.d(TAG,"onDataChanage: ${model.bookname}")
                        }
                    }
                    //setup adapter
                    adapterPdf= AdapterPdf(this@EbooksActivity,pdfArrayList)
                    binding.booksRv.adapter=adapterPdf
                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })
    }
    }
