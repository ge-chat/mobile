package by.tarnenok.geofy

import android.content.res.Resources
import android.util.Base64
import by.tarnenok.geofy.services.api.TokenModel
import by.tarnenok.geofy.services.api.UserInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Created by tarne on 26.04.16.
 */
fun Resources.getStringByName(name: String, packageName: String) : String = getString(getIdentifier(name, "string", packageName))
fun Collection<String?>.toUnorderedList() : String?{
    val sb = StringBuilder("")
    forEach { sb.append("$it\n") }
    sb.append("")
    return sb.toString()
}
fun Array<String?>.toUnorderedListFromResource(resources: Resources, packageName: String) : String?
        = map { resources.getStringByName(it!!, packageName) }.toUnorderedList()

fun TokenModel.userInfo(): UserInfo{
    val idKey = "sub"
    val emailKey = "unique_name"
    val base64Str = access_token.split(".")[1]
    val str = String(Base64.decode(base64Str, Base64.DEFAULT))
    var map: Map<String,Object> = HashMap<String, Object>();
    map = Gson().fromJson(str, map.javaClass)
    return UserInfo(map[idKey] as String, "")
}