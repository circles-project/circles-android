package org.futo.circles.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import org.futo.circles.extensions.coroutineScope
import org.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.model.MediaFileData
import java.io.IOException
import java.io.InputStream

class CirclesGlideDataFetcher(context: Context, private val data: MediaFileData) :
    DataFetcher<InputStream> {

    private val localFilesHelper = LocalFileHelper(context)
    private val matrixSession = MatrixSessionProvider.currentSession


    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    private var stream: InputStream? = null

    override fun cleanup() {
        cancel()
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    override fun cancel() {
        if (stream != null) {
            try {
                stream?.close()
                stream = null
            } catch (ignore: Throwable) {
                Log.e(this.javaClass.name, "Failed to close stream ${ignore.localizedMessage}")
            } finally {
                stream = null
            }
        }
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        if (localFilesHelper.isLocalFile(data.fileUrl)) {
            localFilesHelper.openInputStream(data.fileUrl)?.use { callback.onDataReady(it) }
            return
        }

        val fileService = matrixSession?.fileService() ?: return Unit.also {
            callback.onLoadFailed(IllegalArgumentException("No File service"))
        }

        matrixSession.coroutineScope.launch {
            val result = runCatching {
                fileService.downloadFile(
                    fileName = data.fileName,
                    mimeType = data.mimeType,
                    url = data.fileUrl,
                    elementToDecrypt = data.elementToDecrypt
                )
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    { callback.onDataReady(it.inputStream()) },
                    { callback.onLoadFailed(it as? Exception ?: IOException(it.localizedMessage)) }
                )
            }
        }
    }
}