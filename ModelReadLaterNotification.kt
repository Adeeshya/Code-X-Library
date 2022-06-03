package com.example.libraryappcodex.model

class ModelReadlaterNotification {

    //variables
    var bookid:String=""
    var uid:String=""
    var id:String=""
    var booktitle:String=""

    constructor()

    constructor(bookid: String, uid: String, id: String, booktitle: String) {
        this.bookid = bookid
        this.uid = uid
        this.id = id
        this.booktitle = booktitle
    }


}