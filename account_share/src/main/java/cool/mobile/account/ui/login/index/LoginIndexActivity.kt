package cool.mobile.account.ui.login.index

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.kongzue.dialogx.dialogs.TipDialog
import com.qipeng.yp.onelogin.QPOneLogin
import com.qipeng.yp.onelogin.callback.QPResultCallback
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.*
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.lib_base.util.Logger
import cool.mobile.account.R
import cool.mobile.account.databinding.ActivityLoginIndexBinding
import cool.mobile.account.ui.login.fragment.index.LoginIndexFragment
import cool.mobile.account.ui.login.fragment.index.YPHelper
import cool.mobile.account.ui.login.fragment.mobile.AccountLoginFragment
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [AccountConstant.Path.INDEX, AccountConstant.Path.INDEX1]
)
class LoginIndexActivity : VMBindingActivity<LoginAcVm, ActivityLoginIndexBinding>() {
    private val gravityIvArr = arrayListOf<ImageView>()

    private var accountFragment: AccountLoginFragment? = null
    private lateinit var indexFragment: Fragment

    private var subscribe: Disposable? = null

    private var enableType = YPHelper.EnableType.UNKNOW

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0] * 2.2f
                val y = it.values[1] * 2.2f
                viewBinding.jboxView.onSensorChanged(-x, y)
            }
        }
    }

    override fun enablePartyVerify(): Boolean {
        return false
    }

    override fun moduleTag(): String = ModuleConstant.LOGIN_MODULE

    override fun onStart() {
        super.onStart()
        sensorManager?.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(sensorListener)
    }

    override fun finish() {
        super.finish()
        LoginUtils.removeAllLoginListener()
    }

    override fun setSystemStatusBar() {
//        super.setSystemStatusBar()
        StatusBarUtil.setColor(this, Color.TRANSPARENT)
        StatusBarUtil.transparentStatus(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        UTHelper.commonEvent(UTConstant.Login.Login_PAGE, "进入登录页面")
        viewModel.loginAction =
            intent.getIntExtra(AccountConstant.LOGIN_ACTION, AccountConstant.LOGIN_FINISH)
        //由账号登录页面返回的情况就不用重置取号状态，由其他地方唤起的登录需要重置取号状态
        initView()
        initObserver()
        initYPOneLogin()
    }

    private fun initObserver() {
        viewModel.enableOneLoginLiveData.observe(this) {
            it?.let {
                hideYunPPB()
            }
            subscribe?.dispose()
            enableType = if (it) YPHelper.EnableType.ENABLE else YPHelper.EnableType.NOT
        }
        viewModel.wechatBindPhoneLogin.observe(this) {
            routeToLogin(it.userId, it.token)
        }
        viewModel.phoneLogin.observe(this) {
            if (it) {
                phoneLogin()
            }
        }
        viewModel.phoneBack.observe(this) {
            phoneBack()
        }

    }

    private fun initView() {
        viewBinding.apply {
            gravityIvArr.add(loginIv1)
            gravityIvArr.add(loginIv2)
            gravityIvArr.add(loginIv3)
            gravityIvArr.add(loginIv4)
            gravityIvArr.add(loginIv5)
            gravityIvArr.add(loginIv6)
            gravityIvArr.add(loginIv7)
            gravityIvArr.add(loginIv8)
            gravityIvArr.add(loginIv9)
            gravityIvArr.add(loginIv10)
            gravityIvArr.add(loginIv11)
            gravityIvArr.add(loginIv12)
            gravityIvArr.add(loginIv13)
            loginIv5.setTag(R.id.wd_view_circle_tag, true)
        }

        for (iv in gravityIvArr) {
            iv.setTag(R.id.wd_view_body_need_tag, "need")
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        indexFragment = LoginIndexFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, indexFragment)
            .setPrimaryNavigationFragment(indexFragment)
            .commit()

    }

    override fun initListeners() {
    }

    /**
     * 初始化云片预先取号
     * */
    private fun initYPOneLogin() {
        showYunPPB()
        postTimeout()
        QPOneLogin.getInstance()
            .init(this, ThirdPartyKeyConstant.QPOneLoginKey, object : QPResultCallback {
                override fun onSuccess(message: String?) {
                    viewModel.yPOneLoginPrePhone()
                    Logger.d(
                        "getSimOperator: ",
                        QPOneLogin.getInstance().getSimOperator(this@LoginIndexActivity)
                    )
                }

                override fun onFail(message: String?) {
                    enableType = YPHelper.EnableType.NOT
                    viewModel.enableOneLogin = false
                    viewModel.enableOneLoginLiveData.postValue(false)
                }
            })
    }


    private fun phoneLogin() {
        accountFragment = AccountLoginFragment.newInstance(null, null, null)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.on_activity_open_enter,
                R.anim.on_activity_open_exit,
                R.anim.on_activity_close_enter,
                R.anim.on_activity_close_exit
            )
            .hide(indexFragment)
            .add(R.id.fragment, accountFragment!!)
            .addToBackStack(null)
            .commit()
    }

    private fun phoneBack() {
        accountFragment?.let {
            supportFragmentManager.popBackStack()
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
    }

    private fun routeToLogin(userId: String?, token: String?) {
        accountFragment =
            AccountLoginFragment.newInstance(AccountConstant.AuthType.WECHAT, userId, token)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.on_activity_open_enter,
                R.anim.on_activity_open_exit,
                R.anim.on_activity_close_enter,
                R.anim.on_activity_close_exit
            )
            .hide(indexFragment)
            .add(R.id.fragment, accountFragment!!)
            .addToBackStack(null)
            .commit()
    }

    /**
     * 容错处理，发送一个超时时间处理
     * */
    private fun postTimeout() {
        fun postNotEnable() {
            hideYunPPB()
            when (enableType) {
                YPHelper.EnableType.UNKNOW -> {
                    viewModel.enableOneLoginLiveData.postValue(false)
                }
                else -> {}
            }
        }
        subscribe = Flowable.timer(5, TimeUnit.SECONDS)
            .subscribe({
                postNotEnable()
            }, {
                postNotEnable()
            })
        addDisposable(subscribe)
    }

    private fun showYunPPB() {
        showLoadingDialog()
    }

    private fun hideYunPPB() {
        hideLoadingDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        accountFragment?.onActivityResult(requestCode, resultCode, data)
    }


}