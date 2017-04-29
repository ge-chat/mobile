package by.tarnenok.geofy.services.api

import java.util.*

//View models
data class UserRegisterModel(val email: String, val userName: String, val password: String, var confirmPassword: String)
data class TokenModel(val token_type: String, val access_token: String, val expires_in: String)
data class CreateChartModel(val title: String, val latitude: Double, val longitude: Double, val radius: Double, val description: String?)
data class SendMessageModel(val message: String, val chartId: String)
data class ChangeNameModel(val chatId: String, val name: String)

//Read models
data class ChartReadModel(val id: String, val title: String, val description: String?, val location: Location, val radius: Double, val ownerId: String, val adminIds: MutableList<String>, val participants : MutableList<Participant>, val messages: MutableList<MessageReadModel>)
data class ChartReadModelShort(val  id: String, val title: String, val location: Location, val radius: Double, val ownerId: String, val adminIds: Array<String>, val participants : Array<Participant>, var lastMessage: ShortMessage?)
data class Location(val longitude: Double, val latitude: Double)
data class Participant(val userId: String, var userName: String)
data class ShortMessage(val messageId: String, val userId:String, val created: Date, val message: String)
data class MessageReadModel(val id: String, val userId:String, val created: Date, val message: String, val chartId: String)

//Signals
data class ParticipantAddedSignal(val chartId: String, val participant: Participant)
data class ParticipantNameChangedSignal(val userId: String, val name: String)

//Models
data class UserInfo(val id: String, val email: String)