package by.tarnenok.geofy.services.api

fun ChartReadModel.changeNameForUser (userId: String, name: String){
    var participant = participants.find { it.userId == userId }
    participant?.userName = name
}
