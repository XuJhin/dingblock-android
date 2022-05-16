package cool.dingstock.monitor.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cool.dingstock.monitor.R

class ShareItemView @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(mContext, attrs, defStyleAttr) {
    var shareTitle: String? = ""
    var shareIcon: Drawable? = null
    var root: View = View.inflate(context, R.layout.view_share_item, this)
    val ivShare: ImageView = root.findViewById(R.id.iv_share_icon)
    val tvShare: TextView = root.findViewById(R.id.tv_share_title)

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShareItemView, defStyleAttr, 0)
            shareIcon = typedArray.getDrawable(R.styleable.ShareItemView_share_icon)
            shareTitle = typedArray.getString(R.styleable.ShareItemView_share_title)
            typedArray.recycle()
        }
        initView()
    }
    private fun initView() {
        tvShare.text = shareTitle
        ivShare.setImageDrawable(shareIcon)
    }

}