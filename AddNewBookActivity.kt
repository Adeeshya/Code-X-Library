package com.example.libraryappcodex

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libraryappcodex.databinding.ActivityAddNewBookBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddNewBookActivity : AppCompatActivity() {

    //add Book Image Activity

    lateinit var binding: ActivityAddNewBookBinding
    //uri of picked iamge
    private var ImageUri:Uri? = null

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog (show while uploading pdf)
    private lateinit var progressDialog:ProgressDialog

    //TAG
    private val TAG="IMAGE_ADD_TAG"


    //spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddNewBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

//setup progress dialog
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase
        firebaseAuth= FirebaseAuth.getInstance()


        //handle back button
        binding.backBtn.setOnClickListener {
            onBackPressed()//go to previous screen
        }

        binding.addBookImageBtn.setOnClickListener{
            selectImage()
        }

        //handle click start uploading pdf /book
        binding.submitBtn.setOnClickListener {
            //step 1:validate data
            //step 2:upload pdf to firebase storage
            //step 3:get url of uploaded pdf
            //step 4 :upload pdf into firebase db

            validateData()
        }
    }

    private var title=""
    private var description=""
    private var faculty=""
    private var authors=""
    private var publisheddate=""
    private var bookid=""

    private fun validateData() {
        //step 1:validate data

        Log.d(TAG,"validateData:validating data")
        //get data
        title=binding.booktitleEt.text.toString().trim()
        bookid=binding.bookidEt.text.toString().trim()
        description=binding.bookdescriptionEt.text.toString().trim()
        faculty=binding.facultyEt.text.toString().trim()
        authors=binding.authorsEt.text.toString().trim()
        publisheddate=binding.publisheddateEt.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this,"Enter Title", Toast.LENGTH_SHORT).show()
        }
        else if (bookid.isEmpty()){
            Toast.makeText(this,"Enter bookID", Toast.LENGTH_SHORT).show()
        }else if (description.isEmpty()){
            Toast.makeText(this,"Enter Description", Toast.LENGTH_SHORT).show()
        }
        else if (faculty.isEmpty()){
            Toast.makeText(this,"Pick Category", Toast.LENGTH_SHORT).show()
       }
        else if (authors.isEmpty()){
            Toast.makeText(this,"Pick Category", Toast.LENGTH_SHORT).show()
        }
        else if (publisheddate.isEmpty()){
            Toast.makeText(this,"Pick Category", Toast.LENGTH_SHORT).show()
        }
        else if(ImageUri==null) {
            Toast.makeText(this,"Please Select IMAGE", Toast.LENGTH_SHORT).show()
        }else {
            //data validated begin upload
            uploadImageToStorage()
        }

    }

    private fun uploadImageToStorage() {
        //step 2:upload iamge to firebase storage
        Log.d(TAG,"uploadImageToStorage:uploading to storage")

        //show progress dialog
        progressDialog.setMessage("Uploading IMAGE...")
        progressDialog.show()

        //timestamp
        val timestamp=System.currentTimeMillis()

        //path of pdf in firebase storage
        val filePathAndName ="Images/$timestamp"

        //storage reference
        val storageReference= FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(ImageUri!!)
            .addOnSuccessListener {takeSnapshot->
                Log.d(TAG,"uploadImageToStorage:IMAGE Uploaded now getting url...")

                //step 3:get url of uploaded pdf
                val uriTask: Task<Uri> = takeSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl="${uriTask.result}"

                uploadImageInfoToDb(uploadedImageUrl,timestamp)
            }
            .addOnFailureListener{e->
                Log.d(TAG,"uploadImageToStorage:Failed to upload IMAGE due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to Upload IMAGE due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageInfoToDb(uploadedImageUrl: String, timestamp: Long) {
        //step 4 :upload image into firebase db
        Log.d(TAG,"uploadImageInfoToDb:uploading to db")
        progressDialog.setMessage("Uploading image info")

        //uid of current user
        val uid=firebaseAuth.uid

        val bookaddedDate = MyApplication.formatTimestamp(timestamp)

        //setup data to upload
        val hashMap: HashMap<String,kotlin.Any> = HashMap()
        hashMap["uid"]="$uid"
        hashMap["id"]="$bookid"
        hashMap["title"]="$title"
        hashMap["description"]="$description"
        hashMap["faculty"]="$faculty"
        hashMap["url"]="$uploadedImageUrl"
        hashMap["bookaddedDate"]="$bookaddedDate"
        hashMap["authors"]="$authors"
        hashMap["publishedDate"]="$publisheddate"

        //db reference DB > Books > (Book Info)
        val ref= FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG,"uploadImageInfoToDb:upload to db")
                progressDialog.dismiss()
                Toast.makeText(this,"Uploaded",Toast.LENGTH_SHORT).show()
                ImageUri=null
            }
            .addOnFailureListener { e->
                Log.d(TAG,"uploadeImageInfoToDb:failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload due to ${e.message}",Toast.LENGTH_SHORT).show()
            }


    }


    private fun selectImage() {
        Log.d(TAG, "imagePickIntent: starting iamge pick intent")
        val intent =Intent()
        intent.type = "image/*"
        intent.action=Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100&& resultCode==RESULT_OK){
            Log.d(TAG, "IMAGE Picked")
            ImageUri =data?.data!!
            //binding.firebaseImage.setImageURI(ImageUri)
        }else {
            Log.d(TAG, "IMAGE Pick cancelled")
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}