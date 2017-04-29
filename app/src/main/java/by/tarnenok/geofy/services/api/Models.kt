package by.tarnenok.geofy.services.api

import java.util.*

//View models
data class UserRegisterModel(val email: String, val userName: String, val password: String, var confirmPassword: String)
data class TokenModel(val token_type: String, val access_token: String, val expires_in: String)
data class CreateChartModel(val title: String, val latitude: Double, val longitude: Double, val radius: Double, val description: String?)

//Read models
data class ChartReadModel(val  id: String, val title: String, val description: String?, val location: Location, val radius: Double, val ownerId: String, val adminIds: Array<String>, val participants : Array<Participant>)
data class ChartReadModelShort(val  id: String, val title: String, val location: Location, val radius: Double, val ownerId: String, val adminIds: Array<String>, val participants : Array<Participant>, val lastMessage: ShortMessage?)
data class Location(val longitude: Double, val latitude: Double)
data class Participant(val userId: String, val userName: String)
data class ShortMessage(val messageId: String, val authorId:String, val createdDate: Date, val content: String)