package cool.dingstock.appbase.entity.bean.circle

data class CircleDynamicVoteEntity(
        var title: String
)

data class PushVoteEntity(
        var arrayList: MutableList<CircleDynamicVoteEntity> = arrayListOf()
)