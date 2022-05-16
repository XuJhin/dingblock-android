package cool.dingstock.appbase.adapter.delegation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/28  10:48
 */
interface HolderReloadDelegation:LifecycleEventObserver {

    fun onHolderConvert(entity:Any,holder: BaseViewHolder,lifecycle: Lifecycle?)

}