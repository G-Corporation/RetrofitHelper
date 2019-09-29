package com.gcorp.retrofithelperexample.Model

data class PostResponseModel(
    val createdAt: String,
    val id: String,
    val job: String,
    val name: String
){

    override fun toString(): String {
        return "id : $id,\nname: $name\njob : $job\ncreatedAt : $createdAt "
    }
}