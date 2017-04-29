package by.tarnenok.geofy.services.api

//View models
data class UserRegisterModel(val email: String, val userName: String, val password: String, var confirmPassword: String)
data class TokenModel(val token_type: String, val access_token: String, val expires_in: String)
data class CreateChartModel(val title: String, val latitude: Double, val longitude: Double, val radius: Double, val description: String?)

//Read models
data class ChartReadModel(val  Id: String, val Title: String, val Description: String?, val Location: Location, val Radius: Double, val OwnerId: String, val AdminIds: Array<String>, val Participants : Array<Participant>)
data class ChartReadModelShort(val  Id: String, val Title: String, val Location: Location, val Radius: Double, val OwnerId: String, val AdminIds: Array<String>, val Participants : Array<Participant>)
data class Location(val Longitude: Double, val Latitude: Double)
data class Participant(val UserId: String, val UserName: String)