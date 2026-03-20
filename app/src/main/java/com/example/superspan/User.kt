package com.example.superspan

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue

val ListOfUser = mutableListOf<User>(
    User("Daniela", "Tinti", "d.tinti@superspan.it", "caccacacca")
)

data class User(
    private var _nome: String = "",
    private var _cognome: String = "",
    private var _email: String = "",
    private var _password: String = ""
) {
    private var _admin = false
    //TODO: inserire una reference al CV
    var nome get() = _nome
        set(value) {_nome = value}

    var cognome get() = _cognome
        set(value) {_cognome=value}

    var email get() = _email
        set(value) {_email=value}

    var password get() = _password
        set(value) {_password=value}

    var admin get() = _admin
        set(value) {_admin = value}
}
