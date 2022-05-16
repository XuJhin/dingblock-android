package cool.dingstock.imagepicker.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.use
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import cool.dingstock.imagepicker.R
import cool.dingstock.imagepicker.databinding.AvaterLayoutBinding
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView

class AvatarView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {
    private val avatar: RoundImageView
    private val pendant: ImageView

    fun setDrawableAlpha(alpha: Float) {
        avatar.alpha = alpha
        pendant.alpha = alpha
    }

    init {
        clipToPadding = false
        clipChildren = false
        val viewBinding = AvaterLayoutBinding.inflate(LayoutInflater.from(context), this, false)
        avatar = viewBinding.avatarIv
        pendant = viewBinding.pendantIv
        context.obtainStyledAttributes(attributeSet, R.styleable.AvatarView).use {
            it.getDrawable(R.styleable.AvatarView_ava_src)?.let { ava ->
                setAvatarDrawable(ava)
            }
            it.getDrawable(R.styleable.AvatarView_pen_src)?.let { pen ->
                setPendentDrawable(pen)
            }
        }
        addView(viewBinding.root)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as ViewGroup).apply {
            clipChildren = false
            clipToPadding = false
        }
    }

    fun setAvatarDrawableRes(@DrawableRes resId: Int) {
        avatar.setImageResource(resId)
    }

    fun setPendentDrawableRes(@DrawableRes resId: Int) {
        pendant.setImageResource(resId)
    }

    fun setAvatarDrawable(drawable: Drawable) {
        avatar.setImageDrawable(drawable)
    }

    fun setPendentDrawable(drawable: Drawable) {
        pendant.setImageDrawable(drawable)
    }

    fun setAvatarUrl(url: String?) {
        Glide.with(context).load(url).apply(
            RequestOptions()
                .error(R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
        ).into(avatar)
    }

    fun setPendantUrl(url: String?) {
        Glide.with(context).load(url).into(pendant)
    }

    fun setBorderColor(@ColorInt color: Int) {
        avatar.setBorderColor(color)
    }

    fun setBorderWidth(width: Int) {
        avatar.setBorderWidth(width)
    }
}