package com.example.libraryappcodex

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryappcodex.databinding.RowPdfBinding

class AdapterPdf: RecyclerView.Adapter<AdapterPdf.HolderPdf>, Filterable {


    //context
    private  var context: Context

    //arraaylist to hold list of data of type Modelpdf
    public var pdfArrayList: ArrayList<Modelpdf>

    private val filterList:ArrayList<Modelpdf>


    //view binding row_pdf_admin.xml
    private lateinit var binding: RowPdfBinding

    //filter object
    private var filter: FilterPdf?= null


    //constructor
    constructor(context: Context, pdfArrayList: ArrayList<Modelpdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList=pdfArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdf {
        //bind /inflate layout using view binding
        binding = RowPdfBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdf(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdf , position: Int) {
        /*get data ,set data ,handle clicks etc*/

        //get data
        val model=pdfArrayList[position]
        val pdfId=model.id
        val categoryname=model.categoryname
        val bookname=model.bookname
        val bookdescription=model.bookdescription
        val viewsCount=model.viewsCount
        val downloadsCount=model.downloadsCount
        val pdfUrl=model.url
        val timestamp=model.timestamp

        //convert timestamp to dd/mm/yyyy
        val formattedDate = MyApplication.formatTimestamp(timestamp)

        //set data
        holder.titleTv.text = bookname
        holder.descriptionTv.text = bookdescription
        holder.dateTv.text = formattedDate
        holder.categoryTv.text=categoryname

        //load further details like category , pdf from url , pdf size in seperate functions
        //load category
        //MyApplication.loadCategory(categoryId,holder.categoryTv)

        //we dont need page number here , pass null for page number
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl,bookname, holder.pdfView ,null)

        //load pdf size
        MyApplication.loadPdfSize(pdfUrl,bookname,holder.sizeTv)

        //handle item click,open pdfdetailactivity
        holder.itemView.setOnClickListener {
            //intent with bookid
            val intent= Intent(context,PdfDetailActivity::class.java)
            intent.putExtra("bookId",pdfId)//use to load bookdetails
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return pdfArrayList.size //return number of records | list size
    }


    override fun getFilter(): Filter {
        //
        if (filter==null){
            filter= FilterPdf(filterList,this)
        }
        return filter as FilterPdf
    }

    /*view holder class for row_pdf_admin.xml*/
    inner class HolderPdf(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //ui views of row_pdf_admin.xml
        val pdfView=binding.pdfView
        //val progressBar=binding.progressBar
        val titleTv=binding.titleTv
        val descriptionTv=binding.descriptionTv
        val categoryTv=binding.categoryTv
        val sizeTv=binding.sizeTv
        val dateTv=binding.dateTv
        val moreBtn=binding.moreBtn
    }
}