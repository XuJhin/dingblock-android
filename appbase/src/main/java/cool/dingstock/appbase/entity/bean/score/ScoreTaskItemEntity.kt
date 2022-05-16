package cool.dingstock.appbase.entity.bean.score

/**
 *
 *   "createdAt": 1616986637735, // 创建日期 （可忽略）
"type": "daily", // 任务类型(日常 daily/新手 newbie)
"name": "测试日常任务1onOpen", // 任务名称
"imageUrl": "https://www.baidu.com/img/flexible/logo/pc/result.png", // 图片url
"desc": "测试日常任务1", // 任务说明
"link": "https://www.baidu.com/", // 跳转链接
"buttonStr": "去完成", // 按钮文字
"stage": "onOpen", // 任务阶段(onOpen receivable completed)
"blocked": false // 可忽略
 *
 * */
data class ScoreTaskItemEntity(val id: String
                               , val link: String?
                               , val imageUrl: String
                               , val name: String
                               , val desc: String
                               , val buttonStr: String
                               , val type: TaskType = TaskType.daily
                               , val stage: Stage = Stage.onOpen
                               , val specificationBody: String?
                               , val specificationTitle: String?
) {
    enum class TaskType {
        daily, newbie
    }

    enum class Stage {
        onOpen, receivable, completed
    }

}


