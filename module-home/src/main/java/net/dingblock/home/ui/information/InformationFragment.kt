package net.dingblock.home.ui.information

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import cool.dingstock.home.databinding.BlockFragmentInformationBinding
import cool.dingstock.lib_base.util.Logger
import net.dingblock.home.ui.raffle.RaffleFragment
import net.dingblock.mobile.base.fragment.BaseBindingFragment

class InformationFragment : BaseBindingFragment<BlockFragmentInformationBinding>() {
    val informationVM: InformationVM by viewModels()
companion object{
    const val TAG="Information"
}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.d(TAG, "onViewCreated")
    }

    override fun onVisibleFirst() {
        super.onVisibleFirst()
        Logger.d(TAG, "onVisibleFirst")
    }

    override fun onVisibleExceptFirst() {
        super.onVisibleExceptFirst()
        Logger.d(TAG, "onVisible")
    }
}