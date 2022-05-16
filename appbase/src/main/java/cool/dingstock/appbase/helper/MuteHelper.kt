package cool.dingstock.appbase.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

class MuteHelper(private val context: Context) {
    companion object {
        private const val MUTE_CHANGE_ACTION = "android.media.STREAM_MUTE_CHANGED_ACTION"
        private const val VOLUME_CHANGE_ACTION = "android.media.VOLUME_CHANGED_ACTION"
        private const val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"
    }

    private var muteListener: MuteListener? = null

    private var mMuteReceiver: MuteReceiver? = null

    interface MuteListener {
        fun onMuteChange(mute: Boolean)
    }

    fun registerMuteListener(listener: MuteListener) {
        muteListener = listener
        mMuteReceiver = MuteReceiver()
        context.registerReceiver(mMuteReceiver, IntentFilter(MUTE_CHANGE_ACTION))
        context.registerReceiver(mMuteReceiver, IntentFilter(VOLUME_CHANGE_ACTION))
    }

    fun unregisterMuteListener() {
        mMuteReceiver?.let {
            context.unregisterReceiver(it)
            mMuteReceiver = null
        }
        muteListener = null
    }

    inner class MuteReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val audioManager = (context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)
            intent?.run {
                when(action) {
                    MUTE_CHANGE_ACTION -> {
                        audioManager?.let {
                            muteListener?.onMuteChange(it.isStreamMute(AudioManager.STREAM_MUSIC))
                        }
                    }
                    VOLUME_CHANGE_ACTION -> {
                        if (getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC) {
                            val currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: -1
                            muteListener?.onMuteChange(currentVolume <= 0)
                        }else{

                        }
                    }
                    else -> {}
                }

            }
        }
    }
}