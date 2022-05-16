package cool.dingstock.appbase.uikit.widget.magicindicator

interface ScrollState {
    companion object {
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SETTLING = 2
    }
}