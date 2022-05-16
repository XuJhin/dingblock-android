package cool.dingstock.appbase.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import cool.dingstock.appbase.R
import cool.dingstock.appbase.databinding.WidgetIconTextViewBinding
import cool.dingstock.appbase.ext.SP
import cool.dingstock.appbase.ext.dp

class SvgTextView @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(mContext, attrs, defStyleAttr) {

    val defaultColor = context.getColor(R.color.color_text_black1)
    val defaultIconSize = 16.dp.toInt()
    var iconWidth: Int = defaultIconSize
    var iconHeight: Int = defaultIconSize
    var iconRes: Int? = -1
    var titleSize: Int = 16.SP.toInt()
    var iconColor = context.getColor(R.color.color_text_black1)
    var titleColor = context.getColor(R.color.color_text_black1)

    var textContent: String = ""
    var binding: WidgetIconTextViewBinding = WidgetIconTextViewBinding.bind(
        View.inflate(
            context,
            R.layout.widget_icon_text_view,
            this
        )
    )

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SvgTextView)
        iconColor = typedArray.getColor(R.styleable.SvgTextView_svgColor, defaultColor)
        iconRes = typedArray.getResourceId(R.styleable.SvgTextView_svgRes, R.drawable.svg_comment)
        iconWidth =
            typedArray.getDimensionPixelSize(R.styleable.SvgTextView_svgWidth, defaultIconSize)
        iconHeight =
            typedArray.getDimensionPixelSize(R.styleable.SvgTextView_svgHeight, defaultIconSize)
//        textContent = typedArray.getString(R.styleable.SvgTextView_titleContent).toString()
//        titleSize = typedArray.getDimensionPixelSize(
//            R.styleable.SvgTextView_contentSize,
//            16.SP.toInt()
//        )
//        titleColor = typedArray.getResourceId(R.styleable.SvgTextView_titleColor, defaultColor)

        typedArray.recycle()
        setupView()
    }

    private fun setupView() {
        binding.ivIcon.apply {
            this.updateLayoutParams {
                this.height = iconHeight.toInt()
                this.width = iconWidth.toInt()
            }
            val svgIcon = iconRes?.let { context.getDrawable(it) }
            this.setImageDrawable(svgIcon)

        }
        binding.tvText.apply {
            text = textContent
            textSize = titleSize.toFloat()
            setTextColor(titleColor)
        }
    }

}