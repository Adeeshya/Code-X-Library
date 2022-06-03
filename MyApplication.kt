package com.example.libraryappcodex

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        //created a static metthod to convert timestamp to proper date format
        fun formatTimestamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            //format timestamp to dd/mm/yy
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        //function to get pdf size
        fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView) {
            val TAG = "PDF_SIZE_TAG"

            //using url we can get file and its metadata from firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener { storageMetadata -> //get size in bytes
                    Log.d(TAG, "loadPdfSize: Got metadata")
                    val bytes = storageMetadata.sizeBytes.toDouble()
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    //convert bytes to KB ,MB
                    val kb = bytes / 1024
                    val mb = kb / 1024
                    if (mb >= 1) {
                        sizeTv.text = "${String.format("%.2f", mb)} MB"
                    } else if (kb >= 1) {
                        sizeTv.text = "${String.format("%.2f", kb)} KB"
                    } else {
                        sizeTv.text = "${String.format("%.2f", bytes)} bytes"
                    }
                }
                .addOnFailureListener { e -> //failed  getting metadata
                    Log.d(TAG, "loadPdfSize: Failed to get metadata dueto ${e.message}")
                }
        }

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle: String,
            pdfView: PDFView,
            pagesTv: TextView?
        ) {
            val TAG = "PDF_THUMBNAIL_TAG"

            //using url we can get file and its metadata from firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener { bytes -> //get size in bytes
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    //set to PDFVIEW
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError { t ->
                            //progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                        }
                        .onPageError { page, t ->
                            //progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                        }
                        .onLoad { nbPages ->
                            Log.d(TAG, "loadPdfFromUrlSinglePage: $nbPages")
                            //pdf pagesTv param is not null then set page numbers
                            if (pagesTv != null) {
                                pagesTv.text = "$nbPages"
                            }

                        }
                        .load()

                }
                .addOnFailureListener { e -> //failed  getting metadata
                    Log.d(TAG, "loadPdfSize: Failed to get metadata dueto ${e.message}")
                }
        }

        fun incrementBookViewCount(bookId:String){
            val ref = FirebaseDatabase.getInstance().getReference("EBooks")
            ref.child(bookId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var viewsCount = "${snapshot.child("viewsCount").value}"

                        if (viewsCount==""||viewsCount=="null"){
                            viewsCount="0";
                        }

                        //increment views count
                        val newViewsCount=viewsCount.toLong()+1

                        //setup data to update in db
                        val hashMap =HashMap<String,Any>()
                        hashMap["viewsCount"]=newViewsCount

                        //set to db
                        val ref = FirebaseDatabase.getInstance().getReference("EBooks")
                        ref.child(bookId)
                            .updateChildren(hashMap)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

        }

    }

}