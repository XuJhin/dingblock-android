package cool.dingstock.foundation

import android.app.Application

object Foundation {
    lateinit var application: Application
    fun initialize(application: Application) {
        this.application = application
    }
}