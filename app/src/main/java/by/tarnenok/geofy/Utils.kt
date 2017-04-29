package by.tarnenok.geofy

import android.content.res.Resources

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