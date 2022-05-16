package cool.dingstock.appbase.entity.bean.home.bp

/**
 *
 * "balance":1.10, // 可提现
"rewards":2.20, //  累计奖励
"withdrawal":1.10, // 已提现
"ungiven":0.00 // 未发放
 * */
data class MineRewardEntity(val balance: Float,
                            val rewards: Float,
                            val withdrawal: Float,
                            val ungiven: Float
)