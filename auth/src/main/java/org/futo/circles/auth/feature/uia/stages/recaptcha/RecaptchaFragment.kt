package org.futo.circles.auth.feature.uia.stages.recaptcha

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentRecaptchaBinding
import org.futo.circles.auth.feature.sign_up.uia.SignupUIADataSourceProvider
import org.futo.circles.auth.model.RecaptchaJavascriptResponse
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.visible
import org.matrix.android.sdk.api.extensions.tryOrNull
import java.net.URLDecoder
import java.util.Formatter

@AndroidEntryPoint
class RecaptchaFragment :
    ParentBackPressOwnerFragment<FragmentRecaptchaBinding>(FragmentRecaptchaBinding::inflate),
    HasLoadingState {

    private val viewModel by viewModels<RecaptchaViewModel>()
    override val fragment: Fragment = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    private fun setupObservers() {
        viewModel.recaptchaResultLiveData.observeResponse(this)
        viewModel.recaptchaParamsLiveData.observeData(this) { setupWebView(it) }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(siteKey: String) {
        binding.wvCaptcha.settings.javaScriptEnabled = true

        val reCaptchaPage =
            readRecaptchaAssetFile() ?: kotlin.run {
                showError("Missing asset reCaptchaPage.html")
                return
            }

        val html = Formatter().format(reCaptchaPage, siteKey).toString()

        binding.wvCaptcha.loadDataWithBaseURL(
            SignupUIADataSourceProvider.getDataSourceOrThrow().homeServerUrl,
            html,
            "text/html",
            "utf-8",
            null
        )
        binding.wvCaptcha.requestLayout()

        binding.wvCaptcha.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (!isAdded) return
                binding.vLoading.visible()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (!isAdded) return
                binding.vLoading.gone()
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                if (!isAdded) return
                showSslError(handler)
            }


            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url?.startsWith("js:") == true) {
                    var json = url.substring(3)
                    val javascriptResponse = tryOrNull {
                        json = URLDecoder.decode(json, "UTF-8")
                        Gson().fromJson(json, RecaptchaJavascriptResponse::class.java)
                    }

                    val response = javascriptResponse?.response
                    if (javascriptResponse?.action == "verifyCallback" && response != null)
                        viewModel.handleRecaptcha(response)
                }
                return true
            }
        }
    }

    private fun readRecaptchaAssetFile(): String? = tryOrNull {
        requireContext().assets.open("reCaptchaPage.html").use { asset ->
            buildString {
                var ch = asset.read()
                while (ch != -1) {
                    append(ch.toChar())
                    ch = asset.read()
                }
            }
        }
    }

    private fun showSslError(handler: SslErrorHandler) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.ssl_could_not_verify)
            .setPositiveButton(R.string.trust) { dialogInterface, _ ->
                handler.proceed()
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.do_not_trust) { dialogInterface, _ ->
                handler.cancel()
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .show()
    }

}