package cool.dingstock.appbase.helper

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cool.dingstock.appbase.service.RecordService
import cool.dingstock.lib_base.util.SizeUtils


class ScreenRecorder : ServiceConnection {

    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102
    private val AUDIO_REQUEST_CODE = 103

    private constructor()

    companion object {
        fun getInstance(): ScreenRecorder {
            return ScreenRecorder()
        }
    }


    private lateinit var projectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection
    private lateinit var recordService: RecordService
    private lateinit var activity: AppCompatActivity
    private lateinit var onConnected: (isRunning: Boolean) -> Unit
    private lateinit var onStartRecord: (success: Boolean) -> Unit
    private var isInitSuccess = false

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(activity: AppCompatActivity, onConnected: (isRunning: Boolean) -> Unit) {
        this.activity = activity
        projectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        this.onConnected = onConnected

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val intent = Intent(activity, RecordService::class.java)
        intent.putExtra("isStart",true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent)
        }else{
            activity.startService(intent)
        }
        activity.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }


    fun startRecord(onStartRecord: (success: Boolean) -> Unit) {
        relStartRecord(onStartRecord)
    }

    private fun relStartRecord(onStartRecord: (success: Boolean) -> Unit) {
        this.onStartRecord = onStartRecord
        if (!recordService.isRunning) {
            var captureIntent: Intent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureIntent = projectionManager.createScreenCaptureIntent()
            }
            activity.startActivityForResult(captureIntent, RECORD_REQUEST_CODE)
        }
    }

    fun stopRecord(): String? {
        if (recordService.isRunning) {
            val filPath = recordService.stopRecord()

            return filPath
        }
        return null
    }

    fun isRunning(): Boolean {
        return recordService.isRunning
    }

    fun onActivityDestroy(activity: AppCompatActivity){
        if(isInitSuccess){
            activity.unbindService(this)
            if(!recordService.isRunning){
                val intent = Intent(activity,RecordService::class.java)
                activity.stopService(intent)
            }
        }
    }

    /**
     * @return true 权限申请成功 false 权限申请失败 null不是录制功能相关权限
     * */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean? {
        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限申请成功
                return true
            }
            return false
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun onActivityResult(context: Context,requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recordService.setProjectionManager(projectionManager)
                val service = Intent(context,RecordService::class.java)
                service.putExtra("code",resultCode)
                service.putExtra("data",data)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(service)
//                }else{
                    context.startService(service)
//                }
                onStartRecord(true)
            } else {
                onStartRecord(false)
            }
        }
    }

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val binder = service as RecordService.RecordBinder
        recordService = binder.recordService
        var height = 1080
        var width = 720
        if(SizeUtils.getWidth()<720){
            width = 375
            height = 562
        }

        recordService.setConfig(width,height,metrics.densityDpi)
        isInitSuccess = true
        onConnected(recordService.isRunning)
    }

    fun isConned():Boolean{
        return ::recordService.isInitialized
    }

    fun isInitSuccess():Boolean{
        return isInitSuccess
    }

    override fun onServiceDisconnected(arg0: ComponentName) {}

}