package cool.mobile.account.share

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cool.dingstock.appbase.R
import cool.dingstock.appbase.share.SharePlatform

class ShareAdapter : RecyclerView.Adapter<ShareAdapter.ShareViewHolder>() {
    var utEventId = ""

    val shareList: MutableList<SharePlatform> = arrayListOf()
    var shareListener: ShareListener? = null

    inner class ShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shareIcon = itemView.findViewById<ImageView>(R.id.iv_share_cover)
        val shareTitle = itemView.findViewById<TextView>(R.id.tv_share_title)
        val shareLayout = itemView.findViewById<ConstraintLayout>(R.id.layout_share)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareViewHolder {
        return ShareViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_share, parent, false))
    }

    override fun getItemCount(): Int {
        return shareList.size
    }

    override fun onBindViewHolder(holder: ShareViewHolder, position: Int) {
        val shareItemEntity = shareList[position]
        Glide.with(holder.itemView)
                .load(shareItemEntity.iconRes)
                .into(holder.shareIcon)
        holder.shareTitle.text = shareItemEntity.shareName
        holder.shareLayout.setOnClickListener {
            shareListener?.onClickShare(sharePlatform = shareItemEntity)
        }
    }

    interface ShareListener {
        fun onClickShare(sharePlatform: SharePlatform)
    }

}
