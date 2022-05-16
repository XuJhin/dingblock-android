package cool.dingstock.appbase.util

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColorStateList
import cool.dingstock.appbase.R
import cool.dingstock.appbase.base.BaseDcActivity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.status.LoadingDialogStatus
import cool.dingstock.lib_base.util.TimeUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit

@JvmOverloads
fun View.setOnShakeProofClickListener(listener: View.OnClickListener,
									  windowDuration: Long = 1000,
									  unit: TimeUnit = TimeUnit.MILLISECONDS
) {
	setOnClickListener {
		val currentTime = TimeUtils.getCurrentTime()
		if ((currentTime - lastClckTimestamp) > unit.toMillis(windowDuration)) {
			lastClckTimestamp = currentTime
			listener.onClick(it)
		}
	}
}

@JvmOverloads
fun View.setOnShakeClickListener(listener: View.OnClickListener) {
	setOnShakeProofClickListener(listener = listener)
}

@JvmOverloads
fun View.setOnShakeClickListener(listener: (v: View) -> Unit) {
	setOnShakeProofClickListener(listener = {
		listener(it)
	})
}

private var View.lastClckTimestamp: Long
	set(value) {
		setTag(R.id.view_last_click_time, value)
	}
	get() {
		return ((getTag(R.id.view_last_click_time) as? Long) ?: 0)
	}

@SuppressLint("CheckResult")
fun <T> Flowable<T>.bindDialog(activity: BaseDcActivity?): Flowable<T> {
	if (Looper.getMainLooper().thread.id == Thread.currentThread().id) {
		activity?.showLoadingDialog()
	} else {
		activity?.runOnUiThread { activity.showLoadingDialog() }
	}
	return observeOn(AndroidSchedulers.mainThread())
			.filter {
				activity?.hideLoadingDialog()
				return@filter true
			}.doOnError {
				activity?.hideLoadingDialog()
			}
}

@SuppressLint("CheckResult")
fun <T> Flowable<T>.bindDialog(baseVm: BaseViewModel?): Flowable<T> {
	baseVm?.loadingDialogLiveData?.postValue(LoadingDialogStatus.Loading())
	return observeOn(AndroidSchedulers.mainThread())
			.filter {
				baseVm?.postAlertHide()
				return@filter true
			}.doOnError {
				baseVm?.postAlertHide()
			}
}

fun ImageView.setSvgColorRes(svgResId: Int, @ColorRes colorResId: Int) {
	try {
		this.setImageResource(svgResId)
		val colorStateList = getColorStateList(context, colorResId)
		this.imageTintList = colorStateList
	} catch (e: Exception) {
	}

}

fun ImageView.setSvgColorRes(svgResId: Int, colorResId: Int, alpha: Float) {
	try {
		this.setImageResource(svgResId)
		val colorStateList = getColorStateList(context, colorResId)
		this.imageTintList = colorStateList
		setAlpha(alpha)
	} catch (e: Exception) {
	}

}

fun ImageView.setSvgColor(svgResId: Int,@ColorInt color: Int) {
	try {
		this.setImageResource(svgResId)
		val colorStateList = ColorStateList.valueOf(color)
		this.imageTintList = colorStateList
	} catch (e: Exception) {
	}
}


