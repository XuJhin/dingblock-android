package cool.dingstock.appbase.exception

import java.io.IOException

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/29  15:19
 */
data class DcException(val code: Int, val msg: String) : IOException(msg)

data class ServiceException(val msg: String = "数据错误") : IOException(msg)