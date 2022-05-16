package cool.dingstock.appbase.adapter.multiadapter.core

class MutableTypes constructor(
        private val initialCapacity: Int = 0,
        private val types: MutableList<Type<*>> = ArrayList(initialCapacity)
) : Types {
    override val size: Int
        get() = types.size

    override fun <T> register(type: Type<T>) {
        types.add(type)
    }

    override fun unregister(clazz: Class<*>): Boolean {
        return types.removeAll { it.clazz == clazz }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getType(index: Int): Type<T> {
        return types[index] as Type<T>
    }

    override fun firstIndexOf(clazz: Class<*>): Int {
        val index = types.indexOfFirst { it.clazz == clazz }
        if (index != -1) {
            return index
        }
        return types.indexOfFirst { it.clazz.isAssignableFrom(clazz) }
    }
}