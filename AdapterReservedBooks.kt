package com.example.libraryappcodex.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryappcodex.MyBooksActivity
import com.example.libraryappcodex.R
import com.example.libraryappcodex.databinding.RowReservedBooksBinding
import com.example.libraryappcodex.model.ModelReservedBooks

class AdapterReservedBooks (
    var c: Context,var bookList:ArrayList<ModelReservedBooks>
    ):RecyclerView.Adapter<AdapterReservedBooks.ReservedBooksViewHolder>()
    {
        inner class ReservedBooksViewHolder(var v: RowReservedBooksBinding) : RecyclerView.ViewHolder(v.root) {}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservedBooksViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val v = DataBindingUtil.inflate<RowReservedBooksBinding>(
                inflater, R.layout.row_reserved_books, parent,
                false

            )
            return ReservedBooksViewHolder(v)

        }

        override fun onBindViewHolder(holder: ReservedBooksViewHolder, position: Int) {
            val newList = bookList[position]
            holder.v.isBooks = bookList[position]
            holder.v.root.setOnClickListener {
                val booktitle = newList.booktitle
                val bookid = newList.bookid
                val url = newList.url
                val borrowdate = newList.borrowdate
                val returndate = newList.returndate
                val uid = newList.uid

                /**set Data*/
                val mIntent = Intent(c, MyBooksActivity::class.java)
                mIntent.putExtra("booktitle", booktitle)
                mIntent.putExtra("bookid", bookid)
                mIntent.putExtra("url", url)
                mIntent.putExtra("borrowdate", borrowdate)
                mIntent.putExtra("returndate", returndate)
                mIntent.putExtra("uid", uid)
                c.startActivity(mIntent)

            }

        }

        override fun getItemCount(): Int {
            return bookList.size
        }
    }

