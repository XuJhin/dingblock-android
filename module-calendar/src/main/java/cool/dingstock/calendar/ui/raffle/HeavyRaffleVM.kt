package cool.dingstock.calendar.ui.raffle

import android.graphics.Point
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity
import cool.dingstock.appbase.mvvm.SingleLiveEvent
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.lib_base.util.Logger

class HeavyRaffleVM : BaseViewModel() {

    val dataList: MutableList<CalenderProductEntity> = mutableListOf()
    var selectedEntity: CalenderProductEntity? = null

    private var lastPos: Int = -1
    var currentPost: Int = -1

    /**
     * 存入2个值，默认均为-1
     *  上次选中位置：  x
     *  此次选中位置：  y
     */
    val topRvPosition: SingleLiveEvent<Point> = SingleLiveEvent()
    val vpPosition: SingleLiveEvent<Int> = SingleLiveEvent()

    init {
        Logger.d("init viewmodel")
    }

    fun initData(list: MutableList<CalenderProductEntity>, item: CalenderProductEntity?) {
        lastPos = -1
        currentPost = -1
        this.dataList.clear()
        list.forEach { it.hasSelected = false }
        this.dataList.addAll(list)
        Logger.d("init data")
        if (this.selectedEntity == null) {
            this.selectedEntity = item
        }
        val position = dataList.indexOfFirst {
            it.id.equals(selectedEntity?.id)
        }
        currentPost = position
        dataList[currentPost].hasSelected = true
        vpPosition.postValue(currentPost)
        topRvPosition.postValue(Point(lastPos, position))
    }

    fun notifyRvScroll(position: Int) {
        if (position == currentPost) {
            return
        }
        lastPos = currentPost
        currentPost = position
        selectedEntity = dataList[currentPost]
        topRvPosition.postValue(Point(lastPos, position))
    }

    fun selectedNewPosition(item: CalenderProductEntity) {
        if (selectedEntity!!.id.equals(item.id)) {
            return
        }
        selectedEntity = item
        val position = dataList.indexOfFirst {
            it.id.equals(selectedEntity?.id)
        }
        if (position == currentPost) {
            return
        }
        lastPos = currentPost
        currentPost = position
        topRvPosition.postValue(Point(lastPos, position))
        vpPosition.postValue(currentPost)
    }
}