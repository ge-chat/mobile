package by.tarnenok.geofy.models

data class UserRegisterModel(val email: String, val userName: String, val password: String, var confirmPassword: String)
