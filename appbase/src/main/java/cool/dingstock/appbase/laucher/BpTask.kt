package cool.dingstock.appbase.laucher

import cool.dingstock.appbase.helper.bp.BpInitHelper
import cool.dingstock.launch.DcITask

class BpTask: DcITask() {
    override fun run() {
        BpInitHelper.initBpHelper()
    }
}