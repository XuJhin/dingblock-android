package cool.dingstock.appbase.entity.bean.home

data class CalenderFeaturedEntity(
        val badge: BadgeEntity,
        val products: MutableList<CalenderProductEntity> = arrayListOf()
)

data class BadgeEntity(
        val width: String,
        val height: String,
        val imageUrl: String
)