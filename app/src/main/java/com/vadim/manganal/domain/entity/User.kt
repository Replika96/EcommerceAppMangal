package com.vadim.manganal.domain.entity

data class User(
    val uid: String = "",
    val name: String = "",
    val surname: String? = null,
    val patronymic: String? = null,
    val phone: String? = null,
    val email: String = "",
    val city: String? = null,
    val region: String? = null,
    val street: String? = null,
    val postalCode: String? = null,
    val house: String? = null,
    val apartment: String? = null
)