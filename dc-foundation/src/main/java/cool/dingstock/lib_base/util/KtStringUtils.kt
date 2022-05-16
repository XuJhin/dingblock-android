package cool.dingstock.lib_base.util

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/19  16:17
 */
object KtStringUtils {
    fun compareVersion(v1: String, v2: String): Int {
        try {
            val arr1 = ArrayList(v1.split("."))
            val arr2 = ArrayList(v2.split("."))
            if (arr1.size > arr2.size) {
                repeat(arr2.size - arr1.size) { arr2.add("0") }
            } else if (arr1.size < arr2.size) {
                repeat(arr2.size - arr1.size) { arr1.add("0") }
            }
            for (index in arr1.indices) {
                val v1Int = arr1[index].toInt()
                var v2Int = 0
                try {
                    v2Int = arr2[index].toInt()
                } catch (e: Exception) {
                }
                if (v1Int > v2Int) {
                    return 1
                } else if (v1Int < v2Int) {
                    return -1
                }
            }
        } catch (e: Exception) {
        }
        return 0
    }

}