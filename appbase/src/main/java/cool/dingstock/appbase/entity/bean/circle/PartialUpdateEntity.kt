package cool.dingstock.appbase.entity.bean.circle

data class PartialUpdateEntity<T>(
        var data: T,
        var position: Int
)