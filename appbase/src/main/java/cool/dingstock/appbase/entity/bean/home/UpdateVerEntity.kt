package cool.dingstock.appbase.entity.bean.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/12  10:09
 */
@Parcelize
data class UpdateVerEntity(var reviewingVersion: String? = null//当前审核版本
                           , var updateContent: String? = null//更新了啥修复了啥
                           , var forceMinVersion: String? = null//更新了啥修复了啥
                           , var strongMinVersion: String? = null//更新了啥修复了啥
                           , var weakMinVersion: String? = null
                           , var weight: Int? = null//更新了啥修复了啥
                           , var updateType: UpdateType? = null
                           , var monitorVersion: Long? = 0
                           , var adVersion: Long? = 0
) : Parcelable

enum class UpdateType {
    force,//强更新
    strong,//强提示更新
    weak//弱提示更新
}