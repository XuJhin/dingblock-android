package cool.dingstock.lib_base.util.image

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri

class SingleMediaScanner(
    context: Context?,
    private val path: String,
    private val listener: ScanListener?
) : MediaScannerConnection.MediaScannerConnectionClient {
    private val mMs: MediaScannerConnection = MediaScannerConnection(context, this)
    override fun onMediaScannerConnected() {
        mMs.scanFile(path, null)
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        mMs.disconnect()
        listener?.onScanFinish()
    }

    interface ScanListener {
        fun onScanFinish()
    }

    init {
        mMs.connect()
    }
}