package cool.dingstock.monitor.widget.drawer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.ExpandableRecyclerViewAdapter
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableGroup
import cool.dingstock.appbase.entity.bean.monitor.SubChannelGroupEntity
import cool.dingstock.appbase.entity.bean.monitor.SubChannelItemEntity
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.monitor.databinding.ItemMonitorDrawerGroupBinding
import cool.dingstock.monitor.databinding.MonitorDrawerItemChannelBinding
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.util.LoginUtils


/**
 * 类名：DrawerMonitorExpandAdapter
 * 包名：cool.dingstock.monitor.widget.drawer
 * 创建时间：2021/8/31 10:27 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class DrawerMonitorExpandAdapter(
    val context: Context,
    val list: ArrayList<ExpandableGroup<SubChannelGroupEntity, SubChannelItemEntity>>
) :
    ExpandableRecyclerViewAdapter<DrawerMonitorGVH, DrawerMonitorCVH, SubChannelGroupEntity, SubChannelItemEntity>(
        context,
        list
    ) {

    var onChildClickFun : ((position:Int,entity:SubChannelItemEntity)->Unit)? = null


    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): DrawerMonitorGVH {
        return DrawerMonitorGVH(
            ItemMonitorDrawerGroupBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): DrawerMonitorCVH {
        return DrawerMonitorCVH(
            MonitorDrawerItemChannelBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindChildViewHolder(
        holder: DrawerMonitorCVH,
        flatPosition: Int,
        group: ExpandableGroup<SubChannelGroupEntity, SubChannelItemEntity>,
        childIndex: Int
    ) {
        val entity = group.items[childIndex]
        holder.vb.nameTv.text = entity.name
        holder.vb.iv.load(entity.iconUrl)
        val count = if(entity.newFeedCount>999){
            "999+"
        }else{
            entity.newFeedCount.toString()
        }
        holder.vb.countTv.hide(entity.newFeedCount == 0)
        holder.vb.countTv.text = count
        holder.vb.root.setOnShakeClickListener {
            onChildClickFun?.invoke(flatPosition,entity)
        }
        holder.vb.ruleEffectTv.hide(!entity.customRuleEffective)
        holder.vb.maintainMarkTv.hide(!entity.maintaining)
    }

    override fun onBindGroupViewHolder(
        holder: DrawerMonitorGVH,
        flatPosition: Int,
        group: ExpandableGroup<SubChannelGroupEntity, SubChannelItemEntity>
    ) {
        holder.vb.titleTv.text = group.title
        holder.vb.expandedIcon.isSelected = group.isExpand
    }

    fun setOnChildClick(onClick:(position:Int,entity:SubChannelItemEntity)->Unit){
        onChildClickFun = onClick
    }

}