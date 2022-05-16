package cool.dingstock.appbase.adapter.multiadapter.core

data class Type<T>(
        val clazz: Class<out T>,
        val delegate: ItemViewDelegate<T, *>,
        val linker: Linker<T>
)