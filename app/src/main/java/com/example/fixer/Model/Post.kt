package com.example.fixer.Model

class Post {

    private var postid : String = ""
    private var postimage : String = ""
    private var publisher: String = ""
    private var descripcion : String = ""

    constructor()
    constructor(postid: String, postimage: String, publisher: String, descripcion: String) {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.descripcion = descripcion
    }

    fun getPostid() : String{
        return postid
    }

    fun setPostid(postid: String){
        this.postid = postid
    }

    fun getPostimage() : String{
        return postimage
    }

    fun setPostimage(postimage: String){
        this.postimage = postimage
    }

    fun getPublisher() : String{
        return publisher
    }

    fun setPublisher(publisher: String){
        this.publisher = publisher
    }

    fun getDescripcion() : String{
        return descripcion
    }

    fun setDescripcion(descripcion: String){
        this.descripcion = descripcion
    }
}