package cool.dingstock.appbase.widget.tablayout

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import java.lang.reflect.Field

class MyTabLayout@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
): TabLayout(context, attributeSet, defStyleAttr) {
    init {
        val tabMinWidthField: Field
        try {
            tabMinWidthField = TabLayout::class.java.getDeclaredField("scrollableTabMinWidth").apply {
                isAccessible = true
            }
            tabMinWidthField.set(this, 0)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}