package cool.dingstock.appbase.entity.bean.upload

import java.io.File

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/4  12:13
 */
data class UploadFileEntity (val file: File,var url : String,var type:String="")