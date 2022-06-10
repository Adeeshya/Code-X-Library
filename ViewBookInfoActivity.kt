package com.example.libraryappcodex.view

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.libraryappcodex.*
import com.example.libraryappcodex.uitel.getProgessDrawable
import com.example.libraryappcodex.uitel.loadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_reserve_book.view.*
import kotlinx.android.synthetic.main.activity_view_book_info.*
import java.text.SimpleDateFormat
import java.util.*

class ViewBookInfoActivity : AppCompatActivity() {

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog


    var formatDate= SimpleDateFormat("dd MMMM yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_book_info)


        //init progress dialog , will show while creating account | Register Use
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)



        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()


        /*get data*/
        val bookIntent=intent
        val booktitle=bookIntent.getStringExtra("title")
        val bookdescription=bookIntent.getStringExtra("description")
        val bookurl=bookIntent.getStringExtra("url")
        val bookid=bookIntent.getStringExtra("bookid")
        val publishedDate=bookIntent.getStringExtra("publishedDate")
        val authors=bookIntent.getStringExtra("authors")


        /*call text and images*/
        titleTv.text=booktitle
        descriptionTv.text=bookdescription
        publishdateTv.text=publishedDate
        AuthorsTv.text=authors
        bookImage.loadImage(bookurl, getProgessDrawable(this))

        //handle click home
        homeBtn.setOnClickListener{
            startActivity(Intent(this, DashboardUserActivity::class.java))
        }
        //handle click mybooks
        mybooksBtn.setOnClickListener{
            startActivity(Intent(this, MyBooksActivity::class.java))
        }
        //handle click notifications
        notificationsBtn.setOnClickListener{
            startActivity(Intent(this, NotificationActivity2::class.java))
        }
        //handle click ebooks
        ebooksBtn.setOnClickListener{
            startActivity(Intent(this, EbooksActivity::class.java))
        }
        //handle click account
        accountBtn.setOnClickListener{
            startActivity(Intent(this, AccountUserActivity::class.java))
        }

        //handle click readlater button
        readlaterBtn.setOnClickListener{
            readLater(bookid,booktitle,bookurl)
        }


        //handle reserve button
        reserveBtn.setOnClickListener {
            //inflate the dialog with custom view
            val mdialogView = LayoutInflater.from(this).inflate(R.layout.activity_reserve_book,null)
            //alertDialogBuilder
            val mBuilder= AlertDialog.Builder(this)
                .setView(mdialogView)
                //.setTitle("\nBook Title :" +booktitle +"\n Book ID:"+ bookid)
                //.setMessage("Book Title :  "+booktitle+"\n\nBook ID :   "+bookid)



            //show dialog
            val mAlertDialog=mBuilder.show()

            mdialogView.booktitleTv.text=booktitle
            mdialogView.bookidTv.text=bookid

            //borrowdate calender button
            mdialogView.borrowdateBtn.setOnClickListener ( View.OnClickListener {

                val getDate:Calendar=Calendar.getInstance()
                val datepicker= DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->

                    val selectDate:Calendar=Calendar.getInstance()
                    selectDate.set(Calendar.YEAR,year)
                    selectDate.set(Calendar.MONTH,month)
                    selectDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    val date=formatDate.format(selectDate.time)
                    Toast.makeText(this,"Borrow Date : "+date,Toast.LENGTH_SHORT).show()
                        mdialogView.borrowdateTv.text=date


                },getDate.get(Calendar.YEAR),getDate.get(Calendar.MONTH),getDate.get(Calendar.DAY_OF_MONTH))
                datepicker.show()

            })
            //return date calender button
            mdialogView.returndateBtn.setOnClickListener ( View.OnClickListener {

                val getDate:Calendar=Calendar.getInstance()
                val datepicker=DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->

                    val selectDate:Calendar=Calendar.getInstance()
                    selectDate.set(Calendar.YEAR,year)
                    selectDate.set(Calendar.MONTH,month)
                    selectDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    val date=formatDate.format(selectDate.time)
                    Toast.makeText(this,"Return Date : "+date,Toast.LENGTH_SHORT).show()
                    mdialogView.returndateTv.text=date

                },getDate.get(Calendar.YEAR),getDate.get(Calendar.MONTH),getDate.get(Calendar.DAY_OF_MONTH))
                datepicker.show()
            })



            //login button click of custom layout
            mdialogView.confirmBtn.setOnClickListener {
                //get text from EditTexts of custom layout
                val borrowdate=mdialogView.borrowdateTv.text.toString()
                val returndate=mdialogView.returndateTv.text.toString()
                //inflate the dialog with custom view
                //dismiss dialog
                mAlertDialog.dismiss()
                val sdialogView = LayoutInflater.from(this).inflate(R.layout.successfully_recerved,null)
                //alertDialogBuilder
                val sBuilder= AlertDialog.Builder(this)
                    .setView(sdialogView)
                //show dialog
                //sBuilder.show()
                //val sAlertDialog=sBuilder.show()
                val dialog =sBuilder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                //save reserved status/ info - firebase realtime databse
                progressDialog.setMessage("Adding to Reserved books...")

                //timestamp
                val timestamp= System.currentTimeMillis()

                //get current user uid , since user is registered so we can get it now
                val uid=firebaseAuth.uid

                val bookreservedDate = MyApplication.formatTimestamp(timestamp)
                //setup data to add in db
                val hashMap:HashMap<String,Any?> = HashMap()
                hashMap["uid"]="$uid"
                hashMap["bookid"]="$bookid"
                hashMap["booktitle"]="$booktitle"
                hashMap["reserved"]=true
                hashMap["borrowdate"]="$borrowdate"
                hashMap["returndate"]="$returndate"
                hashMap["id"]="$timestamp"
                hashMap["url"]="$bookurl"
                hashMap["bookreservedDate"]="$bookreservedDate"

                //set data into db
                val ref= FirebaseDatabase.getInstance().getReference("ReservedBooks")
                ref.child("$timestamp")
                    .setValue(hashMap)
                    .addOnSuccessListener {
                        //user info saved , open user databse
                        progressDialog.dismiss()
                        Toast.makeText(this,"Reserved Book successfully ...", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e->
                        //failed adding data to db
                        progressDialog.dismiss()
                        Toast.makeText(this,"Failed Reserve due to ${e.message}", Toast.LENGTH_SHORT).show()
                    }




            }
            //cancel button click of custom layout
            mdialogView.cancelBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }

    }

    private fun readLater(bookid: String?,booktitle:String?,bookurl:String?) {
        //save readlater info - firebase realtime databse
        progressDialog.setMessage("Adding to readLater..")

        val sdialogView = LayoutInflater.from(this).inflate(R.layout.successfully_addedtoreadlater,null)
        //alertDialogBuilder
        val sBuilder= AlertDialog.Builder(this)
        sBuilder.setView(sdialogView)

        //show dialog
        //sBuilder.show()
        val dialog =sBuilder.create()
            dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        //timestamp
        val timestamp= System.currentTimeMillis()

        //get current user uid , since user is registered so we can get it now
        val uid=firebaseAuth.uid

        val addedToReadLaterDate = MyApplication.formatTimestamp(timestamp)

        //setup data to add in db
        val hashMap:HashMap<String,Any?> = HashMap()
        hashMap["uid"]="$uid"
        hashMap["bookid"]="$bookid"
        hashMap["booktitle"]="$booktitle"
        hashMap["readlater"]=true
        hashMap["id"]="$timestamp"
        hashMap["url"]="$bookurl"
        hashMap["addedToReadLaterDate"]="$addedToReadLaterDate"

        //set data into db
        val ref= FirebaseDatabase.getInstance().getReference("ReadLater")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //user info saved , open user databse
                progressDialog.dismiss()
                Toast.makeText(this,"Added To ReadLater ...", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e->
                //failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to add ReadLater due to ${e.message}", Toast.LENGTH_SHORT).show()
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
               // binding.usernameTv.text=email
            }
    }
}