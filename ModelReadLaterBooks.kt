package com.example.libraryappcodex.model

class ModelReadLaterBooks {
    //variables
    var bookid:String=""
    var uid:String=""
    var id:String=""
    var booktitle:String=""
    var url:String=""
    var addedToReadLaterDate:String=""


    constructor(){}

    constructor(
        bookid: String,
        uid: String,
        id: String,
        booktitle: String,
        url: String,
        addedToReadLaterDate: String
    ) {
        this.bookid = bookid
        this.uid = uid
        this.id = id
        this.booktitle = booktitle
        this.url = url
        this.addedToReadLaterDate = addedToReadLaterDate
    }

}