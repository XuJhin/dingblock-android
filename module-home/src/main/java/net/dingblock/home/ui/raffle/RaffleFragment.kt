package net.dingblock.home.ui.raffle

import android.os.Bundle
import android.view.View
import cool.dingstock.home.databinding.BlockFragmentRaffleBinding
import cool.dingstock.lib_base.util.Logger
import net.dingblock.home.ui.index.HomeMainFragment
import net.dingblock.mobile.base.fragment.BaseBindingFragment

class RaffleFragment : BaseBindingFragment<BlockFragmentRaffleBinding>() {

    companion object {
        const val TAG = "RaffleFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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