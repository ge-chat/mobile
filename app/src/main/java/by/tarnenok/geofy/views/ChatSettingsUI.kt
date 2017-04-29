package by.tarnenok.geofy.views

import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.Gravity
import by.tarnenok.geofy.ChatSettingsActivity
import by.tarnenok.geofy.R
import by.tarnenok.geofy.android.circleImage
import by.tarnenok.geofy.android.flatButton
import by.tarnenok.geofy.android.slider
import by.tarnenok.geofy.services.api.UpdateChatModel
import com.rey.material.widget.Slider
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputLayout

/**
 * Created by tarne on 12.06.16.
 */
class ChatSettingsUI(val chatInfo:UpdateChatModel, val update: () -> Unit) : AnkoComponent<ChatSettingsActivity> {

    private val MARGIN_BOTTOM = 5f

    override fun createView(ui: AnkoContext<ChatSettingsActivity>) = with(ui){
        coordinatorLayout{
            lparams(width = matchParent, height = matchParent)
            fitsSystemWindows = true
            appBarLayout {
                toolbar{
                    id = R.id.toolbar
                    backgroundResource = R.color.colorPrimary
                    setTitleTextColor(ContextCompat.getColor(ctx, R.color.white))
                    setNavigationIcon(R.drawable.ic_close)
                    title = resources.getString(R.string.action_settings)
                    flatButton(resources.getString(R.string.update)){
                        textColor = ContextCompat.getColor(ctx, R.color.white)
                        setAllCaps(true)
                        onClick {update()}
                    }.lparams(width = wrapContent, height = wrapContent){
                        gravity = Gravity.RIGHT
                    }
                }.lparams(width = matchParent, height = android.R.attr.actionBarSize){
                    val tv = TypedValue()
                    if (ui.owner.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                        height = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics);
                    }
                }
            }.lparams(width = matchParent, height = wrapContent)
            scrollView{
                verticalLayout{
                    circleImage(R.drawable.default_logo){
                    }.lparams(width = dip(150f), height = dip(150)){
                        this.gravity = Gravity.CENTER_HORIZONTAL
                        margin = dip(20f)
                        bottomMargin = dip(MARGIN_BOTTOM)
                    }
                    textInputLayout {
                        setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout)
                        editText(){
                            hint = resources.getString(R.string.hint_title)
                            setText(chatInfo.title)
                        }
                    }.lparams(width = matchParent, height = wrapContent){
                        bottomMargin = dip(MARGIN_BOTTOM)
                    }
                    textView(resources.getString(R.string.radius)){
                        textColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                    }.lparams(width = wrapContent, height = wrapContent){
                        leftMargin = dip(10f)
                        this.gravity = Gravity.LEFT
                    }
                    slider {
                        applyStyle(R.style.Material_Widget_Slider)
                        setValue(chatInfo.radius.toFloat(), false)
                        setValueRange(5, 100, false)
                    }.lparams(width = matchParent, height = wrapContent){
                        bottomMargin = dip(MARGIN_BOTTOM)
                    }
                    textView(resources.getString(R.string.description)){
                        textColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                    }.lparams(width = wrapContent, height = wrapContent){
                        leftMargin = dip(10f)
                        this.gravity = Gravity.LEFT
                    }
                    editText(){
                        lines = 5
                        maxLines = 5
                        singleLine = false
                        backgroundDrawable = ContextCompat.getDrawable(ctx, R.drawable.border)
                        setText(chatInfo.description)
                    }.lparams(width = matchParent, height = wrapContent){
                        this.gravity = Gravity.TOP.or(Gravity.LEFT)
                        verticalGravity = Gravity.TOP
                        bottomMargin = dip(MARGIN_BOTTOM)
                    }
                }.lparams(width = matchParent, height = matchParent){
                    margin = dip(5f)
                }
            }.lparams(width = matchParent, height = matchParent){
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }
}