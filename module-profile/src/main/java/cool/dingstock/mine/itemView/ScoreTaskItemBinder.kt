package cool.dingstock.mine.itemView

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.score.ScoreTaskItemEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.mine.R
import cool.dingstock.mine.dialog.ScoreCommonDescDialog
import javax.inject.Inject

class ScoreTaskItemBinder : DcBaseItemBinder<ScoreTaskItemEntity, ScoreTaskVH>() {
    @Inject
    lateinit var mineApi: MineApi
    var taskReceiveClickListener: TaskReceiveClickListener? = null

    override fun onConvert(holder: ScoreTaskVH, data: ScoreTaskItemEntity) {
        holder.taskIv.load(data.imageUrl)
        holder.taskName.text = data.name
        holder.taskDesc.text = data.desc
        holder.taskAction.text = data.buttonStr
        holder.taskHelp.hide((TextUtils.isEmpty(data.specificationBody) && TextUtils.isEmpty(data.specificationTitle)))
        updateTaskAction(holder, data, data.stage, data.id, data.link)
        holder.taskHelp.setOnClickListener {
            ScoreCommonDescDialog(context).setData(data.specificationTitle
                    ?: "", data.specificationBody ?: "")
                    .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreTaskVH {
        return ScoreTaskVH(LayoutInflater.from(context).inflate(R.layout.score_task_item_layout, parent, false))
    }

    override fun onConvert(holder: ScoreTaskVH, data: ScoreTaskItemEntity, payloads: List<Any>) {
        super.onConvert(holder, data, payloads)
        if (payloads.isNotEmpty()) {
            (payloads[0] as? Bundle)?.let {
                val imageUrl = it.getString("imageUrl")
                if (imageUrl != null) {
                    holder.taskIv.load(data.imageUrl)
                }

                val name = it.getString("name")
                if (name != null) {
                    holder.taskName.text = data.name
                }

                val desc = it.getString("desc")
                if (desc != null) {
                    holder.taskDesc.text = data.desc
                }

                val buttonStr = it.getString("buttonStr")
                if (buttonStr != null) {
                    holder.taskAction.text = data.buttonStr
                }

                val link = it.getString("link")
                val stage = it.getString("stage")
                if (stage != null || link != null) {
                    updateTaskAction(holder, data, data.stage, data.id, data.link)
                }

                if (it.getString("help") != null) {
                    holder.taskHelp.hide((!TextUtils.isEmpty(data.specificationBody) && !TextUtils.isEmpty(data.specificationTitle)))
                }
            }
        }
    }


    private fun updateTaskAction(holder: ScoreTaskVH, data: ScoreTaskItemEntity, stage: ScoreTaskItemEntity.Stage, id: String, link: String?) {
        when (stage) {
            ScoreTaskItemEntity.Stage.onOpen -> {
                holder.taskAction.setBackgroundResource(R.drawable.mine_task_uncomplete_bg)
                val color = ContextCompat.getColor(context, R.color.color_blue)
                holder.taskAction.setTextColor(color)
                holder.taskAction.setOnClickListener {
                    if (data.name == "开通/续费会员") {
                        UTHelper.commonEvent(UTConstant.Mine.VipP_ent, "source", "积分任务入口")
                    }
                    if (data.name.contains("观看广告视频")) {
                        UTHelper.commonEvent(UTConstant.Score.IntegralP_click_WatchAdTask, "frequency", data.name)
                    }
                    UTHelper.commonEvent(if (data.type == ScoreTaskItemEntity.TaskType.daily) UTConstant.Score.IntegralP_click_DailyTask else UTConstant.Score.IntegralP_click_NoviceTask, "task", holder.taskName.text.toString())

                    link?.let {
                        if (it.startsWith(HomeConstant.Uri.RECOMMEND_FOLLOW)) {
                            DcUriRequest(context, link)
                                    .dialogBottomAni()
                                    .start()
                        } else {
//                            if (it.contains(CommonConstant.Uri.CSJ_ADV)) {
//                                UTHelper.commonEvent(UTConstant.Score.IntegralP_click_WatchAdTask, "frequency", data.name)
//                            }
//                            if (it.contains(MineConstant.Uri.VIP_CENTER)) {
//                                UTHelper.commonEvent(UTConstant.Mine.VipP_ent, "source", "积分任务入口")
//                            }
                            DcUriRequest(context, link)
                                    .start()
                        }
                    }
                }
            }
            ScoreTaskItemEntity.Stage.receivable -> {
                holder.taskAction.setBackgroundResource(R.drawable.mine_task_receive_bg)
                val color = ContextCompat.getColor(context, R.color.white)
                holder.taskAction.setTextColor(color)
                holder.taskAction.setOnClickListener {
                    taskReceiveClickListener?.onTaskReceiveClick(data)
                    UTHelper.commonEvent(UTConstant.Score.IntegralP_click_ClaimButton, "task", holder.taskName.text.toString())
                }
            }
            ScoreTaskItemEntity.Stage.completed -> {
                holder.taskAction.setBackgroundResource(R.drawable.mine_task_complete_bg)
                val color = ContextCompat.getColor(context, R.color.color_text_black5)
                holder.taskAction.setTextColor(color)
                holder.taskAction.setOnClickListener {

                }
            }
        }
    }

    interface TaskReceiveClickListener {
        fun onTaskReceiveClick(data: ScoreTaskItemEntity)
    }

}


class ScoreTaskVH(view: View) : BaseViewHolder(view) {
    val taskIv = view.findViewById<ImageView>(R.id.task_iv)
    val taskName = view.findViewById<TextView>(R.id.task_name)
    val taskHelp = view.findViewById<View>(R.id.help_iv)
    val taskDesc = view.findViewById<TextView>(R.id.task_desc)
    val taskAction = view.findViewById<TextView>(R.id.task_action)
}

class ScoreTaskItemDiffCallback : DiffUtil.ItemCallback<ScoreTaskItemEntity>() {
    override fun getChangePayload(oldData: ScoreTaskItemEntity, newData: ScoreTaskItemEntity): Any? {
        val bundle = Bundle()
        if (oldData.link != newData.link) {
            bundle.putString("link", newData.link)
        }
        if (oldData.imageUrl != newData.imageUrl) {
            bundle.putString("imageUrl", newData.imageUrl)
        }
        if (oldData.name != newData.name) {
            bundle.putString("name", newData.name)
        }
        if (oldData.desc != newData.desc) {
            bundle.putString("desc", newData.desc)
        }
        if (oldData.buttonStr != newData.buttonStr) {
            bundle.putString("buttonStr", newData.buttonStr)
        }
        if (oldData.stage != newData.stage) {
            bundle.putString("stage", newData.stage.name)
        }
        if (oldData.specificationBody != newData.specificationBody || oldData.specificationTitle != oldData.specificationTitle) {
            bundle.putString("help", "needChange")
        }
        return bundle
    }

    override fun areContentsTheSame(oldData: ScoreTaskItemEntity, newData: ScoreTaskItemEntity): Boolean {
        if (oldData.link != newData.link) {
            return false
        }
        if (oldData.imageUrl != newData.imageUrl) {
            return false
        }
        if (oldData.name != newData.name) {
            return false
        }
        if (oldData.desc != newData.desc) {
            return false
        }
        if (oldData.buttonStr != newData.buttonStr) {
            return false
        }
        if (oldData.stage != newData.stage) {
            return false
        }
        if (oldData.specificationBody != newData.specificationBody || oldData.specificationTitle != oldData.specificationTitle) {
            return false
        }
        return true
    }

    override fun areItemsTheSame(oldItem: ScoreTaskItemEntity, newItem: ScoreTaskItemEntity): Boolean {
        return oldItem.id == newItem.id
    }


}
