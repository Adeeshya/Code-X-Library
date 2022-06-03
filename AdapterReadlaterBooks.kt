package com.example.libraryappcodex.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryappcodex.MyBooksActivity
import com.example.libraryappcodex.R
import com.example.libraryappcodex.databinding.RowReadlaterBooksBinding
import com.example.libraryappcodex.model.ModelReadLaterBooks

class AdapterReadLaterBooks  (
    var c: Context, var bookList2:ArrayList<ModelReadLaterBooks>
): RecyclerView.Adapter<AdapterReadLaterBooks.ReadLaterBooksViewHolder>()
{
    inner class ReadLaterBooksViewHolder(var v: RowReadlaterBooksBinding) : RecyclerView.ViewHolder(v.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadLaterBooksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = DataBindingUtil.inflate<RowReadlaterBooksBinding>(
            inflater, R.layout.row_readlater_books, parent,
            false

        )
        return ReadLaterBooksViewHolder(v)

    }

    override fun onBindViewHolder(holder: ReadLaterBooksViewHolder, position: Int) {
        val newList2 = bookList2[position]
        holder.v.isBooks = bookList2[position]
        holder.v.root.setOnClickListener {
            val booktitle = newList2.booktitle
            val bookid = newList2.bookid
            val url = newList2.url
            val uid = newList2.uid

            /**set Data*/
            val nIntent = Intent(c, MyBooksActivity::class.java)
            nIntent.putExtra("booktitle", booktitle)
            nIntent.putExtra("bookid", bookid)
            nIntent.putExtra("url", url)
            nIntent.putExtra("uid", uid)
            c.startActivity(nIntent)

        }

    }

    override fun getItemCount(): Int {
        return bookList2.size
    }
}

