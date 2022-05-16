package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.home.RecommendKolUserEntity
import cool.dingstock.appbase.ext.loadAvatar
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.post.databinding.HomeItemRecommendUserBinding

/**
 * @author wangjiang
 *  CreateAt Time 2021/9/23  16:44
 */
class RecommendUserItemBinder :
    BaseViewBindingItemBinder<RecommendKolUserEntity, HomeItemRecommendUserBinding>() {

    private var onFollowClickFun: ((id: String, followed: Boolean) -> Unit)? = null

    override fun provideViewBinding(
        parent: ViewGroup,
        viewType: Int
    ): HomeItemRecommendUserBinding {
        return HomeItemRecommendUserBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onConvert(vb: HomeItemRecommendUserBinding, data: RecommendKolUserEntity) {
        vb.apply {
            iv.loadAvatar(data.avatarUrl)
            tvName.text = data.nickName
            tvDesc.text = data.briefIntro
            tvBtn.isSelected = data.followed
            tvBtn.text = if (data.followed) "已关注" else "关注"
            tvBtn.setOnShakeClickListener {
                onFollowClickFun?.invoke(data.id,data.followed)
            }
        }
    }

    fun setOnFollowClick(f: (id: String, followed: Boolean) -> Unit) {
        onFollowClickFun = f
    }


}


