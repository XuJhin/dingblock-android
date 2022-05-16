package cool.dingstock.appbase.ext

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import cool.dingstock.appbase.R
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.SizeUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

private val drawableCrossFadeFactory = DrawableCrossFadeFactory
		.Builder(200)
		.setCrossFadeEnabled(true)
		.build()

fun getBlurOptions(radius: Float): RequestOptions {
	return RequestOptions.bitmapTransform(BlurTransformation(radius.toInt(), 4))
}

fun getCircleOptions(error: Int = R.drawable.load_failed): RequestOptions {
	return RequestOptions.circleCropTransform()
			.fallback(error)
			.placeholder(ContextCompat.getDrawable(BaseLibrary.getInstance().context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(BaseLibrary.getInstance().context, error))
}

fun getRadiusOptions(radius: Float = 0f,
					 error: Int = R.drawable.load_failed,
					 cornerType: RoundedCornersTransformation.CornerType, scaleType: BitmapTransformation = CenterCrop()): RequestOptions {
	val radiusPx = SizeUtils.dp2px(radius)
	val multi = MultiTransformation(
			scaleType,
			RoundedCornersTransformation(radiusPx, 0, cornerType))
	return RequestOptions.bitmapTransform(multi)
			.fallback(error)
			.placeholder(ContextCompat.getDrawable(BaseLibrary.getInstance().context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(BaseLibrary.getInstance().context, error))
}

fun ImageView.load(url: String?,
				   radius: Float = 0f,
				   cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL,
				   scaleType: BitmapTransformation = CenterCrop()) {
	Glide.with(this.context)
			.load(url ?: "")
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
			.apply(getRadiusOptions(radius = radius, cornerType = cornerType, scaleType = scaleType))
			.into(this)
}

fun ImageView.loadWithCache(url: String?) {
	Glide.with(context)
		.load(url)
		.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
		.fallback(ContextCompat.getDrawable(context, R.drawable.load_failed))
		.skipMemoryCache(false)
		.dontAnimate()
		.into(this)
}

fun ShapeableImageView.load(
		url: String?,
		radius: Float = 0f,
) {
	if (radius > 0) {
		shapeAppearanceModel = ShapeAppearanceModel
				.builder()
				.setAllCornerSizes(radius.dp)
				.build()
	}
	Glide.with(this.context)
			.load(url ?: "")
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
			.into(this)
}

fun ImageView.load(url: String?,
				   radius: Float = 0f,
				   cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
	Glide.with(this.context)
			.load(url ?: "")
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
			.apply(getRadiusOptions(radius = radius, cornerType = cornerType, scaleType = CenterCrop()))
			.into(this)
}

fun ImageView.load(url: String?) {
	val options = RequestOptions()
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
	Glide.with(this.context)
			.load(url ?: "")
			.apply(options)
			.into(this)
}

fun ImageView.loadWithOutCache(url: String?) {
	val options = RequestOptions()
			.skipMemoryCache(true) // 不使用内存缓存
			.diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
	Glide.with(this.context)
			.load(url ?: "")
			.apply(options)
			.into(this)
}

@SuppressLint("CheckResult")
fun ImageView.load(url: String?, needPlaceHolder: Boolean = true) {
	val options = RequestOptions()
	if (needPlaceHolder) {
		options.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
	} else {
		options.placeholder(R.color.transparent)
	}
	options.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
	Glide.with(this.context)
			.load(url ?: "")
			.apply(options)
			.into(this)
}

fun ImageView.loadAvatar(url: String?) {
	val options = RequestOptions.circleCropTransform()
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.default_avatar))
	Glide.with(this.context)
			.load(url ?: "")
			.apply(options)
			.into(this)
}

fun ImageView.loadBlur(url: String?, radius: Float = 0f) {
	Glide.with(this.context)
			.load(url ?: "")
			.apply(getBlurOptions(radius = radius))
			.into(this)
}

fun ImageView.loadBlur(url: Int, radius: Float = 0f) {
	Glide.with(this.context)
			.load(url)
			.apply(getBlurOptions(radius = radius))
			.into(this)
}

fun ImageView.loadCircle(url: String?) {
	Glide.with(this.context)
			.load(url ?: "")
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
			.apply(getCircleOptions())
			.into(this)
}

fun ImageView.loadCircleWithFragment(url: String?, fragment: Fragment) {
	Glide.with(fragment)
			.load(url ?: "")
			.apply(getCircleOptions())
			.into(this)
}

fun ImageView.loadWithFragment(url: String?, fragment: Fragment, radius: Float = 0f) {
	Glide.with(fragment)
			.load(url)
			.apply(getRadiusOptions(radius = radius, cornerType = RoundedCornersTransformation.CornerType.ALL))
			.into(this)
}

fun ImageView.load(res: Int) {
	Glide.with(this)
			.load(res)
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
			.into(this)
}

fun ImageView.load(res: Int, radius: Float = 0f) {
	Glide.with(this)
			.load(res)
			.placeholder(ContextCompat.getDrawable(context, R.drawable.img_load))
			.error(ContextCompat.getDrawable(context, R.drawable.load_failed))
			.apply(getRadiusOptions(radius = radius, cornerType = RoundedCornersTransformation.CornerType.ALL))
			.into(this)
}


