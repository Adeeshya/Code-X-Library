package com.example.libraryappcodex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryappcodex.databinding.RowReservedNotificationBinding
import com.example.libraryappcodex.model.ModelReservedNotification

class AdapterReservedNotification: RecyclerView.Adapter<AdapterReservedNotification.HolderReservedNotification> {
    //context
    private  var context: Context

    public var pdfArrayList3: ArrayList<ModelReservedNotification>

    //view binding row_pdf_admin.xml
    private lateinit var binding: RowReservedNotificationBinding

    //constructor
    constructor(context: Context, pdfArrayList: ArrayList<ModelReservedNotification>) : super() {
        this.context = context
        this.pdfArrayList3 = pdfArrayList
    }

    inner class HolderReservedNotification(itemView: View): RecyclerView.ViewHolder(itemView) {
        val booktitleTv=binding.booktitleTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderReservedNotification {
        //bind /inflate layout using view binding
        binding = RowReservedNotificationBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderReservedNotification(binding.root)
    }

    override fun onBindViewHolder(holder: HolderReservedNotification, position: Int) {
        /*get data ,set data ,handle clicks etc*/

        //get data
        val model=pdfArrayList3[position]
        val booktitle=model.booktitle

        //set data
        holder.booktitleTv.text = booktitle
    }

    override fun getItemCount(): Int {
        return pdfArrayList3.size
    }
}