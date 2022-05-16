package cool.dingstock.appbase.adapter.multiadapter.core

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

open class MultiTypeAdapter @JvmOverloads constructor(
        open var items: List<Any> = arrayListOf(),
        open val initialCapacity: Int = 0,
        open var types: Types = MutableTypes(initialCapacity)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "MultiTypeAdapter"
    }

    private var onItemClickListener: OnItemClickListener<Any>? = null
    private var onLongClickListener: OnItemLongClickListener<Any>? = null

    /**
     * Registers a type class and its item view delegate. If you have registered the class,
     * it will override the original delegate(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter
     * again.
     *
     * @param clazz the class of a item
     * @param delegate the item view delegate
     * @param T the item data type
     * */
    fun <T> register(clazz: Class<T>, delegate: ItemViewDelegate<T, *>) {
        unregisterAllTypesIfNeeded(clazz)
        register(Type(clazz, delegate, DefaultLinker()))
    }

    inline fun <reified T : Any> register(delegate: ItemViewDelegate<T, *>) {
        register(T::class.java, delegate)
    }

    fun <T : Any> register(clazz: KClass<T>, delegate: ItemViewDelegate<T, *>) {
        register(clazz.java, delegate)
    }

    fun <T> register(clazz: Class<T>, binder: ItemViewBinder<T, *>) {
        register(clazz, binder as ItemViewDelegate<T, *>)
    }

    inline fun <reified T : Any> register(binder: ItemViewBinder<T, *>) {
        register(binder as ItemViewDelegate<T, *>)
    }

    fun <T : Any> register(clazz: KClass<T>, binder: ItemViewBinder<T, *>) {
        register(clazz, binder as ItemViewDelegate<T, *>)
    }

    internal fun <T> register(type: Type<T>) {
        types.register(type)
        type.delegate._adapter = this
    }

    /**
     * Registers a type class to multiple item view delegates. If you have registered the
     * class, it will override the original delegate(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter again.
     *
     * @param clazz the class of a item
     * @param <T> the item data type
     * @return [OneToManyFlow] for setting the delegates
     * @see [register]
     */
    @CheckResult
    fun <T> register(clazz: Class<T>): OneToManyFlow<T> {
        unregisterAllTypesIfNeeded(clazz)
        return OneToManyBuilder(this, clazz)
    }

    @CheckResult
    fun <T : Any> register(clazz: KClass<T>): OneToManyFlow<T> {
        return register(clazz.java)
    }

    /**
     * Registers all of the contents in the specified [Types]. If you have registered a
     * class, it will override the original delegate(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter
     * again.
     *
     * @param types a [Types] containing contents to be added to this adapter inner [Types]
     * @see [register]
     * @see [register]
     */
    fun registerAll(types: Types) {
        val size = types.size
        for (i in 0 until size) {
            val type = types.getType<Any>(i)
            unregisterAllTypesIfNeeded(type.clazz)
            register(type)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return indexInTypesOf(position, items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, indexViewType: Int): RecyclerView.ViewHolder {
        return types.getType<Any>(indexViewType).delegate.onCreateViewHolder(parent.context, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (position >= items.size) {
            return
        }
        val item = items[position]
        getOutDelegateByViewHolder(holder).onBindViewHolder(holder, item, payloads)
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(holder.itemView, holder, item, position) }
    }

    override fun getItemCount(): Int = items.size

    /**
     * Called to return the stable ID for the item, and passes the event to its associated delegate.
     *
     * @param position Adapter position to query
     * @return the stable ID of the item at position
     * @see ItemViewDelegate.getItemId
     * @see RecyclerView.Adapter.setHasStableIds
     * @since v3.2.0
     */
    override fun getItemId(position: Int): Long {
        val item = items[position]
        val itemViewType = getItemViewType(position)
        return types.getType<Any>(itemViewType).delegate.getItemId(item)
    }

    /**
     * Called when a view created by this adapter has been recycled, and passes the event to its
     * associated delegate.
     *
     * @param holder The ViewHolder for the view being recycled
     * @see RecyclerView.Adapter.onViewRecycled
     * @see ItemViewDelegate.onViewRecycled
     */
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        getOutDelegateByViewHolder(holder).onViewRecycled(holder)
    }

    /**
     * Called by the RecyclerView if a ViewHolder created by this Adapter cannot be recycled
     * due to its transient state, and passes the event to its associated item view delegate.
     *
     * @param holder The ViewHolder containing the View that could not be recycled due to its
     * transient state.
     * @return True if the View should be recycled, false otherwise. Note that if this method
     * returns `true`, RecyclerView *will ignore* the transient state of
     * the View and recycle it regardless. If this method returns `false`,
     * RecyclerView will check the View's transient state again before giving a final decision.
     * Default implementation returns false.
     * @see RecyclerView.Adapter.onFailedToRecycleView
     * @see ItemViewDelegate.onFailedToRecycleView
     */
    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return getOutDelegateByViewHolder(holder).onFailedToRecycleView(holder)
    }

    /**
     * Called when a view created by this adapter has been attached to a window, and passes the
     * event to its associated item view delegate.
     *
     * @param holder Holder of the view being attached
     * @see RecyclerView.Adapter.onViewAttachedToWindow
     * @see ItemViewDelegate.onViewAttachedToWindow
     */
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        getOutDelegateByViewHolder(holder).onViewAttachedToWindow(holder)
    }

    /**
     * Called when a view created by this adapter has been detached from its window, and passes
     * the event to its associated item view delegate.
     *
     * @param holder Holder of the view being detached
     * @see RecyclerView.Adapter.onViewDetachedFromWindow
     * @see ItemViewDelegate.onViewDetachedFromWindow
     */
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        getOutDelegateByViewHolder(holder).onViewDetachedFromWindow(holder)
    }

    private fun getOutDelegateByViewHolder(holder: RecyclerView.ViewHolder): ItemViewDelegate<Any, RecyclerView.ViewHolder> {
        @Suppress("UNCHECKED_CAST")
        return types.getType<Any>(holder.itemViewType).delegate as ItemViewDelegate<Any, RecyclerView.ViewHolder>
    }

    @Throws(DelegateNotFoundException::class)
    internal fun indexInTypesOf(position: Int, item: Any): Int {
        val index = types.firstIndexOf(item.javaClass)
        if (index != -1) {
            val linker = types.getType<Any>(index).linker
            return index + linker.index(position, item)
        }
        throw DelegateNotFoundException(item.javaClass)
    }

    private fun unregisterAllTypesIfNeeded(clazz: Class<*>) {
        if (types.unregister(clazz)) {
            Log.w(TAG, "The type ${clazz.simpleName} you originally registered is now overwritten.")
        }

    }

    open fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Any>) {
        this.onItemClickListener = onItemClickListener
    }

    open fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener<Any>) {
        this.onLongClickListener = onItemLongClickListener
    }


}