package by.tarnenok.geofy.services.api

data class UserRegisterModel(val email: String, val userName: String, val password: String, var confirmPassword: String)
data class UserLoginModel(val email: String, val password: String)
data class TokenModel(val token: String)
data class ChartCreateModel(val title: String, val latitude: Double, val longitude: Double)
