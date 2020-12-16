package com.example.fixer.Model

class User {

    private var nombre : String = ""
    private var apellido : String = ""
    private var descripcion : String = ""
    private var email : String = ""
    private var contraseña : String = ""
    private var validado : String = ""
    private var tecnico : String = ""
    private var image : String = ""
    private var uid : String = ""

    constructor()


    constructor(nombre : String, apellido : String, descripcion : String, email : String, contraseña : String, validado : String, tecnico : String, image : String, uid : String)
    {
        this.nombre = nombre
        this.apellido = apellido
        this.descripcion = descripcion
        this.email = email
        this.contraseña = contraseña
        this.validado = validado
        this.tecnico = tecnico
        this.image = image
        this.uid = uid
    }

    fun getNombre() : String
    {
        return nombre
    }

    fun setNombre(nombre : String)
    {
        this.nombre = nombre
    }

    fun getApellido() : String
    {
        return apellido
    }

    fun setApellido(apellido : String)
    {
        this.apellido = apellido
    }

    fun getDescripcion() : String
    {
        return descripcion
    }

    fun setDescripcion(descripcion : String)
    {
        this.descripcion = descripcion
    }

    fun getEmail() : String
    {
        return email
    }

    fun setEmail(email : String)
    {
        this.email = email
    }

    fun getValidado() : String
    {
        return validado
    }

    fun setValidado(validado : String)
    {
        this.validado = validado
    }

    fun getTecnico() : String
    {
        return tecnico
    }

    fun setTecnico(tecnico : String)
    {
        this.tecnico = tecnico
    }

    fun getImage() : String
    {
        return image
    }

    fun setImage(image : String)
    {
        this.image = image
    }

    fun getContraseña() : String
    {
        return contraseña
    }

    fun setContraseña(contraseña: String)
    {
        this.contraseña = contraseña
    }

    fun getUid() : String
    {
        return uid
    }

    fun setUid(uid : String)
    {
        this.uid = uid
    }
}