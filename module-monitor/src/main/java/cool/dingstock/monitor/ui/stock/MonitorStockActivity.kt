package cool.dingstock.monitor.ui.stock

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.multiadapter.core.MultiTypeAdapter
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.InfoEntity
import cool.dingstock.appbase.entity.bean.monitor.MonitorStockEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.share.*
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.PhotoUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.widget.recyclerview.decotation.GridSpacingItemDecoration
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.lib_base.util.FileUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.monitor.R
import cool.dingstock.monitor.databinding.ActivityMonitorShareBinding
import cool.mobile.account.share.ShareDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 监控货量分享页面
 */
@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [MonitorConstant.Path.MONITOR_SHARE])
class MonitorStockActivity : VMBindingActivity<MonitorStockViewModel,ActivityMonitorShareBinding>() {
	// TODO: 2020/7/7 优化权限管理
	private val shareHelper: cool.mobile.account.share.ShareHelper by lazy { cool.mobile.account.share.ShareHelper() }
	private var sizeList: MutableList<Any> = arrayListOf()
	private var sizeAdapter: MultiTypeAdapter = MultiTypeAdapter(sizeList)
	private var stock: MonitorStockEntity? = null
	override fun moduleTag(): String {
		return ModuleConstant.MONITOR
	}

	companion object {
		const val PERMISSION_SHARE_QQ = 10012
		const val PERMISSION_SAVE = 10211
	}

	override fun initBundleData() {
		stock = intent?.getParcelableExtra(MonitorConstant.DataParam.STOCK_DATA)
	}

	fun initData(savedInstanceState: Bundle?) {
		stock?.let {
			viewModel.createLinkQRCode(it.downloadLink)
			viewBinding.monitorShareListLayer.tvPageDate.text = TimeUtils.formatTimestampCurrent()
			viewBinding.ivCover.load(it.imageUrl)
			viewBinding.tvName.text = it.title
			viewBinding.monitorShareListLayer.tvTotalAmount.text = "全部货量(${it.total})"
			viewBinding.layoutMonitorShareDescLayer.layoutDesc.removeAllViews()
			when {
				it.infos.size <= 0 -> {
					viewBinding.layoutMonitorShareDescLayer.layoutDesc.visibility = View.GONE
				}
				else -> {
					viewBinding.layoutMonitorShareDescLayer.layoutDesc.visual()
					addSizeItem(it.infos)
				}
			}
			sizeList.clear()
			when (it.skus.size) {
				0 -> {
					viewBinding.monitorShareListLayer.layoutSize.hide()
					viewBinding.monitorShareListLayer.rvSizeList.hide()
				}
				1 -> {
					viewBinding.monitorShareListLayer.layoutRightDesc.hide()
					viewBinding.monitorShareListLayer.layoutSize.visual()
				}
				else -> {
					viewBinding.monitorShareListLayer.layoutRightDesc.visual()
					viewBinding.monitorShareListLayer.layoutSize.visual()
				}
			}
			sizeList.addAll(it.skus)
			sizeAdapter.notifyDataSetChanged()
		}
	}

	private fun addSizeItem(infos: MutableList<InfoEntity>) {
		for (info in infos) {
			val descView =
					LayoutInflater.from(this).inflate(R.layout.item_monitor_desc, viewBinding.layoutMonitorShareDescLayer.layoutDesc, false)
			descView.findViewById<TextView>(R.id.desc_title).text = info.title
			descView.findViewById<TextView>(R.id.desc_content).text = info.value
			viewBinding.layoutMonitorShareDescLayer.layoutDesc.addView(descView)
		}
	}

	override fun initViewAndEvent(savedInstanceState: Bundle?) {
		val fakeView = findViewById<View?>(R.id.fake_status_bar)
		fakeView?.updateLayoutParams {
			this.height = SizeUtils.getStatusBarHeight(context)
		}

		findViewById<Button>(R.id.share).setOnClickListener {
			val shareDialog = ShareDialog(this)
			shareDialog.show()
		}
		viewBinding.commonTitlebarLeftIcon.setOnClickListener { finish() }

		viewBinding.layoutMonitorShareBottomLayer.shareWechat.setOnClickListener {
			MainScope().launch {
				val bitmap = createShareImage()
				val type = ShareType.Image
				val params = ShareParams()
				params.imageBitmap = bitmap
				type.params = params

				shareHelper.share(context = context, type = type, target = SharePlatform.WeChat)
				UTHelper.commonEvent(UTConstant.Monitor.VolumePopup_share,"SharePath","微信好友")
			}
		}
		viewBinding.layoutMonitorShareBottomLayer.shareMoment.setOnClickListener {
			MainScope().launch {
				val bitmap = createShareImage()
				val type = ShareType.Image
				val params = ShareParams()
				params.imageBitmap = bitmap
				type.params = params

				shareHelper.share(context = context, type = type, target = SharePlatform.WeChatMoments)
				UTHelper.commonEvent(UTConstant.Monitor.VolumePopup_share,"SharePath","微信朋友圈")
			}
		}
		viewBinding.layoutMonitorShareBottomLayer.shareQq.setOnClickListener {
			checkPermission(PERMISSION_SHARE_QQ, {
				shareToQQ()
				UTHelper.commonEvent(UTConstant.Monitor.VolumePopup_share,"SharePath","QQ")
			}, {
				showStorageDenied()
			})
		}
		viewBinding.layoutMonitorShareBottomLayer.saveImage.setOnClickListener {
			checkPermission(PERMISSION_SAVE, {
				saveImage()
				UTHelper.commonEvent(UTConstant.Monitor.VolumePopup_share,"SharePath","保存图片")
			}, {
				showStorageDenied()
			})
		}
		sizeAdapter.register(MonitorStockItemView())
		viewBinding.monitorShareListLayer.rvSizeList.apply {
			isNestedScrollingEnabled = false
			layoutManager = GridLayoutManager(this@MonitorStockActivity, 2)
			addItemDecoration(GridSpacingItemDecoration(SizeUtils.dp2px(12f), false))
			adapter = sizeAdapter
		}
		asyncUI()
		initData(savedInstanceState)
	}

	/**
	 * 保存图片
	 */
	private fun saveImage() {
		lifecycleScope.launch {
			val bitmap = createShareImage()
			saveBitmap(bitmap)
		}
	}

	private fun shareToQQ() {
		lifecycleScope.launch {
			val bitmap = createShareImage()
			val saveBitmap = saveBitmapToPath(bitmap)
			val type = ShareType.Image
			val params = ShareParams()
			params.imageBitmap = bitmap
			params.imagePath = saveBitmap
			type.params = params
			shareHelper.share(context = context, type = type, target = SharePlatform.QQ)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			when (requestCode) {
				PERMISSION_SAVE -> {
					saveImage()
				}
				PERMISSION_SHARE_QQ -> {
					shareToQQ()
				}
				else -> {
				}
			}
		} else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
			showStorageDenied()
		}
	}

	private fun checkPermission(code: Int, success: () -> Unit, failed: () -> Unit) {
		if (Build.VERSION.SDK_INT > 23) {
			val request = ContextCompat.checkSelfPermission(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
			if (request != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), code)
			} else {
				success()
			}
		} else {
			success()
		}
	}

	/**
	 * 同步网络请求数据
	 */
	@SuppressLint("SetTextI18n")
	private fun asyncUI() {
		viewModel.qrCodeLiveData.observe(this, Observer { bitmap ->
			bitmap?.let { it1 -> viewBinding.ivQrCode.setImageBitmap(it1) }
		})
	}

	override fun initListeners() {
	}

	override fun generateViewModel(): MonitorStockViewModel {
		return ViewModelProvider(this).get(MonitorStockViewModel::class.java)
	}

	override fun changeStatusBar() {
		StatusBarUtil.transparentStatus(this)
		window.navigationBarColor = resources.getColor(R.color.navigation_bar_dark)
	}

	/**
	 * 在线程中创建bitmap
	 */
	private suspend fun createShareImage(): Bitmap? {
		try {
			return withContext(Dispatchers.IO) {
				val photoUtils = PhotoUtils()
				val bitmap = photoUtils.createLongPhoto(viewBinding.parentLayout)
				Log.e("size", "${bitmap.byteCount}")
				return@withContext bitmap
			}
		} catch (e: Throwable) {
			return null
		}
	}

	/**
	 * 保存图片
	 */
	fun saveBitmap(bitmap: Bitmap?) : Uri? {
		if (bitmap == null) {
			hideLoadingDialog()
			return null
		}
		val saveBitmap = FileUtils.saveBitmap(bitmap)
		if (saveBitmap != null) {
			hideLoadingDialog()
			 ToastUtil.getInstance().makeTextAndShow(this, "保存图片成功", Toast.LENGTH_SHORT)
		}
		return saveBitmap
	}

	/**
	 * 保存图片
	 */
	fun saveBitmapToPath(bitmap: Bitmap?) : String? {
		if (bitmap == null) {
			return null
		}
		val saveBitmap = FileUtils.save2OwnerFile(bitmap)
		return saveBitmap
	}




	fun showStorageDenied() {
		showToastShort("未获取文件存取权限")
	}
}