package com.example.libraryappcodex.model

class ModelReservedBooks {
    //variables
    var bookid:String=""
    var uid:String=""
    var id:String=""
    var booktitle:String=""
    var url:String=""
    var borrowdate:String=""
    var returndate:String=""
    var bookreservedDate:String=""

    constructor(){}

    constructor(
        bookid: String,
        uid: String,
        id: String,
        booktitle: String,
        url: String,
        borrowdate: String,
        returndate: String,
        bookreservedDate: String
    ) {
        this.bookid = bookid
        this.uid = uid
        this.id = id
        this.booktitle = booktitle
        this.url = url
        this.borrowdate = borrowdate
        this.returndate = returndate
        this.bookreservedDate = bookreservedDate
    }

}