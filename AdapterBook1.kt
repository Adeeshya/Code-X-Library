package com.example.libraryappcodex.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryappcodex.R
import com.example.libraryappcodex.databinding.RowBook1Binding
import com.example.libraryappcodex.model.ModelBook1
import com.example.libraryappcodex.view.ViewBookInfoActivity

class AdapterBook1(
    var c: Context,var bookList:ArrayList<ModelBook1>
    ):RecyclerView.Adapter<AdapterBook1.BooksViewHolder>()
{
    inner class BooksViewHolder(var v: RowBook1Binding) : RecyclerView.ViewHolder(v.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = DataBindingUtil.inflate<RowBook1Binding>(
            inflater, R.layout.row_book1, parent,
            false

        )
        return BooksViewHolder(v)

    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val newList = bookList[position]
        holder.v.isBooks = bookList[position]
        holder.v.root.setOnClickListener {
            val title = newList.title
            val description = newList.description
            val url = newList.url
            val publishedDate=newList.publishedDate
            val bookid=newList.bookid
            val authors=newList.authors



                /**set Data*/
                val mIntent = Intent(c,ViewBookInfoActivity::class.java)
                mIntent.putExtra("title",title)
                mIntent.putExtra("description",description)
                mIntent.putExtra("url",url)
                mIntent.putExtra("bookid",bookid)
                mIntent.putExtra("publishedDate",publishedDate)
                mIntent.putExtra("authors",authors)
                c.startActivity(mIntent)

        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}
