package com.gcorp.retrofithelperexample.Model

data class GetResponseModel(
    val data : Data
){

    override fun toString(): String {
        return "id : ${data.id}\nfirst_name : ${data.first_name}\nlast_name : ${data.last_name}\n" +
                "email : ${data.email}\navatar : ${data.avatar}"

    }

    data class Data(
        val avatar: String,
        val email: String,
        val first_name: String,
        val id: Int,
        val last_name: String
    )

}

