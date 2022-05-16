package cool.dingstock.appbase.adapter.multiadapter.core

interface Types {
    val size: Int
    fun <T> register(types: Type<T>)
    fun unregister(clazz: Class<*>): Boolean
    fun <T> getType(index: Int): Type<T>
    fun firstIndexOf(clazz: Class<*>): Int
}