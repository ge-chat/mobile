package by.tarnenok.geofy.android

import android.view.ViewManager
import com.rey.material.R
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.flatButton(title: String = "") = flatButton(title) {}
inline fun ViewManager.flatButton(title: String = "", init: com.rey.material.widget.Button.() -> Unit) : com.rey.material.widget.Button {
    return ankoView({ com.rey.material.widget.Button(it) }) {
        text = title
        backgroundResource = 0
        applyStyle(R.style.Material_Drawable_Ripple_Touch_Light)
        init()
    }
}