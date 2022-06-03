package com.example.libraryappcodex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryappcodex.databinding.RowReadlaterNotificationBinding
import com.example.libraryappcodex.model.ModelReadlaterNotification

class AdapterReadlaterNotification: RecyclerView.Adapter<AdapterReadlaterNotification.HolderReadlaterNotification> {

    //context
    private  var context: Context

    public var pdfArrayList: ArrayList<ModelReadlaterNotification>

    //view binding row_pdf_admin.xml
    private lateinit var binding: RowReadlaterNotificationBinding

    //constructor
    constructor(context: Context, pdfArrayList: ArrayList<ModelReadlaterNotification>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterReadlaterNotification.HolderReadlaterNotification {
        //bind /inflate layout using view binding
        binding = RowReadlaterNotificationBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderReadlaterNotification(binding.root)
    }

    override fun onBindViewHolder(holder: HolderReadlaterNotification, position: Int) {
        /*get data ,set data ,handle clicks etc*/

        //get data
        val model=pdfArrayList[position]
        val booktitle=model.booktitle

        //set data
        holder.booktitleTv.text = booktitle

    }


    inner class HolderReadlaterNotification(itemView: View): RecyclerView.ViewHolder(itemView) {
        val booktitleTv=binding.booktitleTv
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

}