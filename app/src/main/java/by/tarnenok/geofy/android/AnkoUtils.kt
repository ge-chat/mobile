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

inline fun ViewManager.circleImage(imageRes: Int) = circleImage(imageRes) {}
inline fun ViewManager.circleImage(imageRes: Int, init: de.hdodenhof.circleimageview.CircleImageView.() -> Unit) : de.hdodenhof.circleimageview.CircleImageView {
    return ankoView({ de.hdodenhof.circleimageview.CircleImageView(it) }) {
        setImageResource(imageRes)
        init()
    }
}

inline fun ViewManager.slider() = slider() {}
inline fun ViewManager.slider(init: com.rey.material.widget.Slider.() -> Unit) = ankoView({ com.rey.material.widget.Slider(it) }, init)