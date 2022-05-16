package cool.dingstock.tide.debug

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.VMActivity
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.tide.R
import cool.dingstock.tide.databinding.ActivityTideDeBugIndexBinding

class TideDeBugIndexActivity : VMBindingActivity<BaseViewModel, ActivityTideDeBugIndexBinding>() {

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
    }

    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.TIDE
    }
}