package by.tarnenok.geofy.services

import microsoft.aspnet.signalr.client.Logger
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection

fun createSignalRConnection(apiHost: String, tokenService: TokenService): HubConnection{
    Platform.loadPlatformComponent(AndroidPlatformComponent());
    return  HubConnection(apiHost,
            "authorization=${tokenService.get()?.access_token}",
            true,
            Logger { p0, p1 -> print(p0) })
}