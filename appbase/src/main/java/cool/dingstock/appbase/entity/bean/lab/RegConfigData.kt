package cool.dingstock.appbase.entity.bean.lab;

import android.text.TextUtils;



import cool.dingstock.lib_base.util.CollectionUtils;



data class RegConfigData (
    var rangeReg  : String?,
    var replaceArray  : List<String>?,
    var infoRegs  : List<InfoRegsBean>?
){
    fun isValid() : Boolean{
        return !TextUtils.isEmpty(rangeReg) && CollectionUtils.isNotEmpty(infoRegs)
    }
}


