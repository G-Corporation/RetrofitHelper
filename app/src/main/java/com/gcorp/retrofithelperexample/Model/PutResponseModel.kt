package com.gcorp.retrofithelperexample.Model

data class PutResponseModel(
    val job: String,
    val name: String,
    val updatedAt: String
){

    override fun toString(): String {
        return "name : $name\nJob : $job\nupdatedAt : $updatedAt "
    }
}