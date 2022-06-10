package com.example.libraryappcodex.model

class ModelBook1 {
    //variables
    var uid:String=""
    var bookid:String=""
    var title:String=""
    var description:String=""
    var faculty:String=""
    var url:String=""
    var publishedDate:String=""
    var authors:String=""
    var bookaddedDate:String=""


    constructor(){}

    //constructor
    constructor(
        uid: String,
        bookid: String,
        title: String,
        description: String,
        faculty: String,
        url: String,
        publishedDate: String,
        authors:String,
        bookaddedDate:String

    ) {
        this.uid = uid
        this.bookid = bookid
        this.title = title
        this.description = description
        this.faculty = faculty
        this.url = url
        this.publishedDate = publishedDate
        this.authors = authors
        this.bookaddedDate = bookaddedDate

    }

}