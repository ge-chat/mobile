package by.tarnenok.geofy.services.api

data class UserRegisterModel(val email: String, val userName: String, val password: String, var confirmPassword: String)
data class TokenModel(val token_type: String, val access_token: String, val expires_in: String)
data class ChartCreateModel(val title: String, val latitude: Double, val longitude: Double)
