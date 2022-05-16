package cool.dingstock.tide.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.annotations.LoadMoreStatus
import cool.dingstock.appbase.databinding.HomeItemFooterLayoutBinding
import cool.dingstock.appbase.entity.bean.tide.TideItemEntity
import cool.dingstock.appbase.entity.bean.tide.TideSectionsEntity
import cool.dingstock.appbase.widget.stickyheaders.SectioningAdapter
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.tide.R
import cool.dingstock.tide.adapter.viewholder.FooterItemViewHolder
import cool.dingstock.tide.adapter.viewholder.HomeTideHeaderViewHolder
import cool.dingstock.tide.adapter.viewholder.HomeTideItemViewHolder
import cool.dingstock.tide.adapter.viewholder.OnTideItemActionClickListener
import cool.dingstock.tide.databinding.HomeItemTideLayoutBinding
import cool.dingstock.tide.databinding.HomeTideHeaderLayoutBinding
import cool.dingstock.uicommon.vh.CommonEmptyViewHolder
import cool.dingstock.uicommon.vh.CommonLoadMoreHolder


/**
 * 类名：TideSectionAdapter
 * 包名：cool.dingstock.tide.adapter
 * 创建时间：2021/7/20 4:54 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class TideSectionAdapter : SectioningAdapter() {
    private val NORMAL_TYPE = 0
    private val LOADING_TYPE = 1
    private val EMPTY = 2
    private val FOOTER_TYPE = 3

    @LoadMoreStatus
    private var mLoadMoreStatus = 0

    val data: ArrayList<TideSectionsEntity> = ArrayList()
    private var loadMoreHolder: CommonLoadMoreHolder? = null
    var listener: OnTideItemActionClickListener? = null


    fun setData(list: ArrayList<TideSectionsEntity>?) {
        data.clear()
        list?.let {
            if (!it.isEmpty()) {
                it[it.size - 1].tides?.add(TideItemEntity.newInstance())
            }
            data.addAll(it)
        }
        notifyAllSectionsDataSetChanged()
    }

    override fun getNumberOfSections(): Int {
        return if (CollectionUtils.isEmpty(data)) {
            0
        } else data.size + 1
    }

    override fun getNumberOfItemsInSection(sectionIndex: Int): Int {
        if (CollectionUtils.isEmpty(data)) {
            return 0
        }
        return if (sectionIndex == data.size) {
            1
        } else data[sectionIndex].tides?.size ?: 0
    }

    override fun doesSectionHaveHeader(sectionIndex: Int): Boolean {
        if (CollectionUtils.isEmpty(data)) {
            return false
        }
        return if (sectionIndex > data.size - 1) {
            false
        } else data[sectionIndex].header > 0
    }

    override fun getSectionItemUserType(sectionIndex: Int, itemIndex: Int): Int {
        if (data.size == 0) {
            return EMPTY
        }
        return if (sectionIndex == data.size && itemIndex == 0) {
            LOADING_TYPE
        } else if (data[sectionIndex].tides?.get(itemIndex)?.isFooter == true) {
            FOOTER_TYPE
        } else NORMAL_TYPE
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, itemType: Int): ItemViewHolder? {
        when (itemType) {
            EMPTY -> return CommonEmptyViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.common_recycler_empty, parent, false)
            )
            NORMAL_TYPE -> {
                val vh = HomeTideItemViewHolder(
                        HomeItemTideLayoutBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                        )
                )
                vh.listener = listener
                return vh
            }
            FOOTER_TYPE -> {
                val vh = FooterItemViewHolder(
                        HomeItemFooterLayoutBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                        )
                )
                return vh
            }
            LOADING_TYPE -> return CommonLoadMoreHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(
                                    R.layout.common_recycler_load_more,
                                    parent, false
                            )
            )
            else -> {
            }
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    override fun onBindItemViewHolder(
            viewHolder: ItemViewHolder,
            sectionIndex: Int,
            itemIndex: Int,
            itemType: Int
    ) {
        when (itemType) {
            NORMAL_TYPE -> {
                data[sectionIndex].tides?.get(itemIndex)?.let {
                    val homeSneakerItemViewHolder = viewHolder as HomeTideItemViewHolder
                    homeSneakerItemViewHolder.bind(it)
                }
            }
            LOADING_TYPE -> {
                loadMoreHolder = viewHolder as CommonLoadMoreHolder
                loadMoreHolder!!.bind(mLoadMoreStatus)
                Logger.d("onBindItemViewHolder Loading ")
            }
            EMPTY -> {
            }
            else -> {
            }
        }
    }

    override fun onCreateHeaderViewHolder(
            parent: ViewGroup,
            headerType: Int
    ): HeaderViewHolder? {
        return HomeTideHeaderViewHolder(
                HomeTideHeaderLayoutBinding.inflate(
                        LayoutInflater.from(
                                parent.context
                        ), parent, false
                )
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onBindHeaderViewHolder(
            viewHolder: HeaderViewHolder,
            sectionIndex: Int,
            headerType: Int
    ) {
        val childEntity = data[sectionIndex]
        if (null == childEntity) {
            return
        }
        val sneakerHeadViewHolder = viewHolder as HomeTideHeaderViewHolder
        sneakerHeadViewHolder.bindData(childEntity.header)
    }

    fun showLoadMore() {
        if (loadMoreHolder != null) {
            loadMoreHolder!!.startAnim()
        }
        mLoadMoreStatus = LoadMoreStatus.LOADING
    }


    fun hideLoadMore() {
        if (loadMoreHolder != null) {
            loadMoreHolder!!.stopAnim()
        }
        mLoadMoreStatus = LoadMoreStatus.IDLE
    }

    fun endLoadMore() {
        if (loadMoreHolder != null) {
            loadMoreHolder!!.end()
        }
        mLoadMoreStatus = LoadMoreStatus.END
    }


}