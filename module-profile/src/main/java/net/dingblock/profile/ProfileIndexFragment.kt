package net.dingblock.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.mine.databinding.BlockProfileFragmentIndexBinding
import net.dingblock.mobile.base.fragment.BaseBindingFragment

class ProfileIndexFragment : BaseBindingFragment<BlockProfileFragmentIndexBinding>() {
    private val viewModel: ProfileIndexVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    companion object {
        fun instance(): ProfileIndexFragment {
            return ProfileIndexFragment()
        }
    }


    override fun onVisibleFirst() {
        super.onVisibleFirst()
        Logger.d("ProfileIndexFragment main First visible")
    }

    override fun onVisible() {
        super.onVisible()
        Logger.d("ProfileIndexFragment onVisible")
    }
}