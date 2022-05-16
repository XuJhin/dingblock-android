package cool.dingstock.foundation.base.fragment

internal interface IFragmentVisibility {

    /**
     * Called when the fragment is visible.
     */
    fun onVisible() {}

    /**
     * Called when the Fragment is not visible.
     */
    fun onInvisible() {}

    /**
     * Called when the fragment is visible for the first time.
     */
    fun onVisibleFirst() {}

    /**
     * Called when the fragment is visible except first time.
     */
    fun onVisibleExceptFirst() {}

    /**
     * Return true if the fragment is currently visible to the user.
     */
    fun isVisibleToUser(): Boolean
}