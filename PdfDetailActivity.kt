package com.example.libraryappcodex

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.libraryappcodex.databinding.ActivityPdfDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class PdfDetailActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityPdfDetailBinding

    //bookid get from intent
    private var bookId=""
    //get from firebase
    //book name/title
    private var bookName=""
    private var bookUrl=""

    private companion object{
        //TAG
        const val TAG="BOOK_DETAILS_TAG"
    }

    private lateinit var progressDialog:ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPdfDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init progress bar
        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        //get book id from intent
        bookId= intent.getStringExtra("bookId")!!

        //increment book view count,whenever this page starts
        MyApplication.incrementBookViewCount(bookId)
        loadBookDetails()

        //handle back button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //handle download  button
        binding.downloadBtn.setOnClickListener {
            //check storage permissions in AndroidManifest.xml file
            //check WHITE_EXTERNAL_STORAGE permission first, if granted download book, if not granted request permission
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"onCreate:STORAGE PERMISSION is Already Granted")
                downloadBook()
            }else {
                Log.d(TAG, "onCreate:STORAGE PERMISSION was not Granted ,LETS Request It")
                requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGanted:Boolean ->
        //lets check if granted or not
        if(isGanted){
            Log.d(TAG,"onCreate:STORAGE PERMISSION is Already Granted")
            downloadBook()
        }else {
            Log.d(TAG, "onCreate:STORAGE PERMISSION is denied")
            Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
    }

    }

    private fun downloadBook(){

        Log.d(TAG, "downloadBook:Downloading Book")
        //progeress bar
        progressDialog.setMessage("Downloading Book")
        progressDialog.show()

        //download book from firebase storage using url
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
        storageReference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes->
                Log.d(TAG, "downloadBook:book downloaded")
                saveToDownloadsFolder(bytes)
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Log.d(TAG, "downloadBook:Failed book download due to ${e.message}")
                Toast.makeText(this,"Failed book download due to ${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun saveToDownloadsFolder(bytes: ByteArray?) {
        Log.d(TAG, "saveToDownloadsFolder:saving downloaded book")

        val nameWithExtension="$bookName.pdf"//val nameWithExtension="${System.currentTimeMills()}.pdf"
        try {
            val downloadsFolder =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdir()//create folder if not exists

            val filePath=downloadsFolder.path+"/"+nameWithExtension

            val out=FileOutputStream(filePath)
            out.write(bytes)
            out.close()

            Toast.makeText(this,"Saved to downloads Folder",Toast.LENGTH_SHORT).show()
            Log.d(TAG, "saveToDownloadsFolder:Saved to downloads Folder")
            progressDialog.dismiss()
            incrementDownloadCount()


        }catch (e:Exception){
            Log.d(TAG, "saveToDownloadsFolder:Failed to save due to ${e.message}")
            Toast.makeText(this,"Failed to save due to ${e.message}",Toast.LENGTH_SHORT).show()
        }


    }

    private fun incrementDownloadCount() {
        //incre,ment downloads count to firebase databse
        Log.d(TAG, "incrementDownloadCount:increment donwload count in process")

        val ref = FirebaseDatabase.getInstance().getReference("EBooks")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var downloadsCount = "${snapshot.child("downloadsCount").value}"
                    Log.d(TAG, "onDataChange:Current downloads count")

                    if (downloadsCount==""||downloadsCount=="null"){
                        downloadsCount="0";
                    }

                    //increment views count
                    val newdownloadsCount=downloadsCount.toLong()+1
                    Log.d(TAG, "onDataChange:New downloads count :$newdownloadsCount")

                    //setup data to update in db
                    val hashMap = HashMap<String,Any>()
                    hashMap["downloadsCount"]=newdownloadsCount

                    //set to db
                    val ref = FirebaseDatabase.getInstance().getReference("EBooks")
                    ref.child(bookId)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "onDataChange:Downloads count incremented")
                        }
                        .addOnFailureListener { e->
                            Log.d(TAG, "onDataChange:Failed to increment due to ${e.message}")
                        }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun loadBookDetails() {
        //Ebooks > bookid >details

        val ref = FirebaseDatabase.getInstance().getReference("EBooks")
        //ref.orderByChild("categoryId").equalTo(categoryId)
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    val bookdescription = "${snapshot.child("bookdescription").value}"
                    bookName = "${snapshot.child("bookname").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val categoryname = "${snapshot.child("categoryname").value}"
                    val uid = "${snapshot.child("uid").value}"
                    bookUrl = "${snapshot.child("url").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"
                    val downloadsCount = "${snapshot.child("downloadsCount").value}"

                    //formate date
                    val date=MyApplication.formatTimestamp(timestamp.toLong())

                    //load pdf
                    MyApplication.loadPdfFromUrlSinglePage("$bookUrl","$bookName",binding.pdfView,binding.pagesTv)
                    MyApplication.loadPdfSize("$bookUrl","$bookName",binding.sizeTv)

                    //set data
                    binding.titleTv.text=bookName
                    binding.descriptionTv.text=bookdescription
                    binding.categoryTv.text=categoryname
                    binding.viewTv.text=viewsCount
                    binding.downloadsTv.text=downloadsCount
                    binding.dateTv.text=date


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}