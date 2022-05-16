package cool.dingstock.appbase.serviceloader

import androidx.fragment.app.FragmentManager
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean

interface IAuthDialogServer {
    fun show(
        fragmentManager: FragmentManager,
        seriesId: String?,
        seriesName: String?,
        seriesScore: Float,
        id: String?
    )
}