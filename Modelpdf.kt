package com.example.libraryappcodex

class Modelpdf {

    //variables
    var uid:String=""
    var id:String=""
    var bookname:String=""
    var bookdescription:String=""
    var categoryname:String=""
    var url:String=""
    var timestamp:Long=0
    var viewsCount:Long=0
    var downloadsCount:Long=0


    //empty constructor (required by firebase)
    constructor()

    //parameterized constructor
    constructor(
        uid: String,
        id: String,
        bookname: String,
        bookdescription: String,
        categoryname: String,
        url: String,
        temestamp: Long,
        viewsCount: Long,
        downloadsCount: Long

    ) {
        this.uid = uid
        this.id = id
        this.bookname = bookname
        this.bookdescription = bookdescription
        this.categoryname = categoryname
        this.url = url
        this.timestamp = temestamp
        this.viewsCount = viewsCount
        this.downloadsCount = downloadsCount

    }


}