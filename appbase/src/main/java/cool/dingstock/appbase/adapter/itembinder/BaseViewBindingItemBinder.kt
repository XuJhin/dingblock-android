package cool.dingstock.appbase.adapter.itembinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType


/**
 * 类名：BaseViewBindingItemBinder
 * 包名：cool.dingstock.appbase.adapter.itembinder
 * 创建时间：2021/7/20 5:42 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class BaseViewBindingItemBinder<T, VB : ViewBinding> :
    DcBaseItemBinder<T, ViewBindingVH<VB>>() {

    protected var holder: ViewBindingVH<VB>? = null

    override fun onConvert(holder: ViewBindingVH<VB>, data: T) {
        this.holder = holder
        onConvert(holder.vb, data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingVH<VB> {
        return ViewBindingVH(provideViewBinding(parent, viewType))
    }

    abstract fun provideViewBinding(parent: ViewGroup, viewType: Int): VB

    abstract fun onConvert(vb: VB, data: T)

}

class ViewBindingVH<VB : ViewBinding>(val vb: VB) : BaseViewHolder(vb.root)

