package cool.dingstock.appbase.entity.bean.setting

data class SettingSection(
        /**
         * head : 关于
         * items : [("name":"关于盯链","type":"aboutUs","actionType":"link","value":""),("name":"加入我们","type":"aboutUs","actionType":"link","value":""),("name":"隐私政策","type":"aboutUs","actionType":"link","value":"")]
         */
        var head: String?,
        var items: List<SettingItemBean>?,
        var foot: String?
)
