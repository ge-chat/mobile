package by.tarnenok.geofy.services

import microsoft.aspnet.signalr.client.Logger
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import java.util.*


object SignalRService{
    fun createConnection(apiHost: String, tokenService: TokenService, params : HashMap<String, String>? = null): HubConnection{
        Platform.loadPlatformComponent(AndroidPlatformComponent());

        val connection = HubConnection(apiHost,
                "${prepareParams(params)}authorization=${tokenService.get()?.access_token}",
                true,
                Logger { p0, p1 -> print(p0) })
        return connection
    }

    private fun prepareParams(params: HashMap<String, String>?): String{
        if(params != null){
            val sb = StringBuilder()
            for((key, value) in params){
                sb.append("$key=$value&")
            }
            return sb.toString()
        }
        return "";
    }

    object Hubs{
        object Chart{
            val Name = "ChartHub"
            val MessagePosted = "messagePosted"
            val ParticipantAdded = "participantAdded"
            val ParticipantNameChanged = "participantNameChanged"

            val AddConnection = "connectToChat"
            val RemoveConnection = "disconnectFromChat"
        }
        object User{
            val Name = "UserHub"
            val ChartCreated = "chartCreated"
        }
    }
}