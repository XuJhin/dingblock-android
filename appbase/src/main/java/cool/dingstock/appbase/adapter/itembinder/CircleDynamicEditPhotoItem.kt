package cool.dingstock.appbase.adapter.itembinder

import cool.dingstock.appbase.R
import cool.dingstock.appbase.databinding.CircleItemDynamicEditPhotoBinding
import cool.dingstock.appbase.imageload.GlideHelper.loadRadiusImage
import cool.dingstock.appbase.widget.photoselect.entity.PhotoBean
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder

class CircleDynamicEditPhotoItem(data: PhotoBean) :
    BaseItem<PhotoBean, CircleItemDynamicEditPhotoBinding>(data) {

    private var actionListener: ((CircleDynamicEditPhotoItem) -> Unit)? = null
    fun setActionListener(actionListener: (CircleDynamicEditPhotoItem) -> Unit) {
        this.actionListener = actionListener
    }

    override fun getViewType(): Int {
        return VIEW_TYPE
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.circle_item_dynamic_edit_photo
    }

    override fun onReleaseViews(
        holder: BaseViewHolder,
        sectionKey: Int,
        sectionViewPosition: Int
    ) {
    }

    override fun onSetViewsData(holder: BaseViewHolder, sectionKey: Int, sectionViewPosition: Int) {
        val data = data ?: return
        viewBinding?.let { binding ->
            if (data.imageUri != null) {
                loadRadiusImage(
                    data.imageUri,
                    binding.circleItemDynamicEditPhotoIv, holder.context, 5f
                )
            } else {
                if (data.path != null && (data.path!!.startsWith("https") || data.path!!.startsWith("http"))) {
                    loadRadiusImage(
                        data.path,
                        binding.circleItemDynamicEditPhotoIv, holder.context, 5f
                    )
                }
            }
            data.path?.let {

            }
            binding.circleItemDynamicEditPhotoDelIv.setOnClickListener {
                actionListener?.invoke(this@CircleDynamicEditPhotoItem)
            }
        }
    }

    companion object {
        const val VIEW_TYPE = 101
    }
}