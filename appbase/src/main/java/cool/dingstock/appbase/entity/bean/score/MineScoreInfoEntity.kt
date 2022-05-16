package cool.dingstock.appbase.entity.bean.score

import com.google.gson.annotations.SerializedName
 class MineScoreInfoEntity(
        @SerializedName("score", alternate = ["credits"])
        var score: Int = 0
        , var unReceive: Boolean = false
){
     fun getNiceScore():Int{
         if(score<0){
             return 0
         }
         return score
     }

 }