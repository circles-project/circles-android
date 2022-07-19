package org.futo.circles.glide

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import org.futo.circles.model.ImageContent
import org.futo.circles.model.MediaContentData
import java.io.InputStream

class CirclesGlideModelLoaderFactory(private val context: Context) :
    ModelLoaderFactory<MediaContentData, InputStream> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaContentData, InputStream> {
        return CirclesGlideModelLoader(context)
    }

    override fun teardown() {}
}

class CirclesGlideModelLoader(private val context: Context) :
    ModelLoader<MediaContentData, InputStream> {

    override fun handles(model: MediaContentData): Boolean {
        return true
    }

    override fun buildLoadData(
        model: MediaContentData,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> =
        ModelLoader.LoadData(ObjectKey(model), CirclesGlideDataFetcher(context, model))

}