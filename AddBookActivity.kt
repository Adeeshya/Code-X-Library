package com.example.libraryappcodex

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.libraryappcodex.databinding.ActivityAddBookBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddBookActivity : AppCompatActivity() {

    //Add Book PDF Activity

    //setup view binding activity_pdf_add ---> ActivityPdfBinding
    private lateinit var binding:ActivityAddBookBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    //uri of picked pdf
    private var pdfUri : Uri? = null

    //TAG
    private val TAG="PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back button
        binding.backBtn.setOnClickListener {
            onBackPressed()//go to previous screen
        }

        //handle click pick pdf intent
        binding.addBookPdfBtn.setOnClickListener {
            pdfPickIntent()
        }

        //handle submit button
        binding.submitBtn.setOnClickListener {
            validateData()//go to previous screen


        }

    }

    //              //step 1:validate data
    //              //step 2:upload pdf to firebase storage
    //              //step 3:get url of uploaded pdf
    //              //step 4 :upload pdf into firebase db



    private var  bookname=""
    private var bookdescription=""
    private var categoryname=""
    private var viewsCount=""
    private var downloadsCount=""

    private fun validateData() {
        //              //step 1:validate data
        //validate data
        Log.d(TAG,"validateData:validating data")
        //get data
        bookname = binding.booknameEt.text.toString().trim()
        bookdescription = binding.bookdescriptionEt.text.toString().trim()
        categoryname = binding.categorynameEt.text.toString().trim()


        //validate data
        if (bookname.isEmpty()){
            Toast.makeText(this,"Enter Book Name", Toast.LENGTH_SHORT).show()
        }else if (bookdescription.isEmpty()){
            Toast.makeText(this,"Enter Book Description", Toast.LENGTH_SHORT).show()
        }
        else if (categoryname.isEmpty()){
            Toast.makeText(this,"Enter Book Bolongs Category", Toast.LENGTH_SHORT).show()
        }
        else if(pdfUri==null) {
            Toast.makeText(this,"Please Select PDF",Toast.LENGTH_SHORT).show()
        }
        else{
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {

        //step 2:upload pdf to firebase storage
        Log.d(TAG,"uploadPdfToStorage:uploading to storage")

        //show progress dialog
        progressDialog.setMessage("Uploading PDF...")
        progressDialog.show()

        //timestamp
        val timestamp=System.currentTimeMillis()

        //path of pdf in firebase storage
        val filePathAndName ="Books/$timestamp"

        //storage reference
        val storageReference= FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {takeSnapshot->
                Log.d(TAG,"uploadPdfToStorage:PDF Uploaded now getting url...")

                //step 3:get url of uploaded pdf
                val uriTask: Task<Uri> = takeSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl="${uriTask.result}"

                uploadPdfInfoToDb(uploadedPdfUrl,timestamp)
            }
            .addOnFailureListener{e->
                Log.d(TAG,"uploadPdfToStorage:Failed to upload PDF due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to Upload PDF due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {

        //step 4 :upload pdf into firebase db
        Log.d(TAG, "uploadPdfInfoToDb:uploading to db")
        progressDialog.setMessage("Uploading pdf info")

        //uid of current user
        val uid = firebaseAuth.uid

        //setup data to upload
        val hashMap: HashMap<String, kotlin.Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["bookname"] = "$bookname"
        hashMap["bookdescription"] = "$bookdescription"
        hashMap["categoryname"] = "$categoryname"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0


        //db reference DB > Books > (Book Info)
        val ref = FirebaseDatabase.getInstance().getReference("EBooks")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDb:upload to db")
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                pdfUri = null
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadePdfInfoToDb:failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")

        val intent = Intent()
        intent.type="application/pdf"
        intent.action= Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }
    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback <ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "PDF Picked")
                pdfUri = result.data!!.data
            } else {
                Log.d(TAG, "PDF Pick cancelled")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        })

}