package org.futo.circles.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.futo.circles.R
import org.futo.circles.databinding.ViewCreatePostFabMenuBinding
import org.futo.circles.extensions.visible

interface CreatePostMenuListener {
    fun onCreatePoll()
    fun onCreatePost()
}

class CreatePostFabVerticalMenuView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewCreatePostFabMenuBinding.inflate(LayoutInflater.from(context), this)

    private val openedPlusRotationLeft = -90f - 45f
    private val animationDuration = 300L
    private val animationDelayPerItem = 50L

    private val itemShowAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_scale_up)
    private val itemHideAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_scale_down)
    private val labelShowAnimation =
        AnimationUtils.loadAnimation(context, R.anim.fab_slide_in_from_right)
    private val labelHideAnimation =
        AnimationUtils.loadAnimation(context, R.anim.fab_slide_out_to_right)

    private val openAnimatorSet = AnimatorSet()
    private val closeAnimatorSet = AnimatorSet()
    private val uiHandler: Handler = Handler(Looper.getMainLooper())

    private val menuItems = listOf(
        binding.fbCreatePost to binding.cvCretePost,
        binding.fbCreatePoll to binding.cvCretePoll,
    )

    private var isMenuOpened = false

    private var listener: CreatePostMenuListener? = null

    init {
        createMainFabAnimation()
        binding.fbCreatePost.setOnClickListener {
            listener?.onCreatePost()
            forceClose()
        }
        binding.fbCreatePoll.setOnClickListener {
            listener?.onCreatePoll()
            forceClose()
        }
    }

    fun setUp(callback: CreatePostMenuListener, recycler: RecyclerView, isThread: Boolean) {
        listener = callback
        bindToRecyclerView(recycler)
        setupMainFabButton(isThread)
    }

    private fun setupMainFabButton(isThread: Boolean) {
        binding.fbMain.apply {
            setOnClickListener {
                if (isThread) listener?.onCreatePost()
                else toggle()
            }
            setImageResource(if (isThread) R.drawable.ic_create else R.drawable.ic_add)
        }
    }

    private fun bindToRecyclerView(recycler: RecyclerView) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fbMain.isVisible) {
                    forceClose()
                    binding.fbMain.hide()
                } else if (dy < 0 && binding.fbMain.visibility != View.VISIBLE) {
                    binding.fbMain.show()
                }
            }
        })
    }

    private fun toggle() {
        if (isMenuOpened) close()
        else open()
    }

    private fun createMainFabAnimation() {
        val collapseAnimator = ObjectAnimator.ofFloat(
            binding.fbMain,
            "rotation",
            openedPlusRotationLeft,
            0f
        )
        val expandAnimator = ObjectAnimator.ofFloat(
            binding.fbMain,
            "rotation",
            0f,
            openedPlusRotationLeft
        )
        openAnimatorSet.apply {
            play(expandAnimator)
            interpolator = OvershootInterpolator()
            duration = animationDuration
        }
        closeAnimatorSet.apply {
            play(collapseAnimator)
            interpolator = AnticipateInterpolator()
            duration = animationDuration
        }
    }

    private fun open() {
        if (isMenuOpened) return
        closeAnimatorSet.cancel()
        openAnimatorSet.start()
        menuItems.forEachIndexed { i, pair ->
            uiHandler.postDelayed({
                playMenuItemShowAnimation(pair)
                if (i == menuItems.size - 1) isMenuOpened = true
            }, (i + 1) * animationDelayPerItem)
        }
    }

    private fun close() {
        if (!isMenuOpened) return
        closeAnimatorSet.start()
        openAnimatorSet.cancel()
        menuItems.forEachIndexed { i, pair ->
            uiHandler.postDelayed({
                playMenuItemHideAnimation(pair)
                if (i == menuItems.size - 1) isMenuOpened = false
            }, (i + 1) * animationDelayPerItem)
        }
    }

    private fun forceClose() {
        if (!isMenuOpened) return
        binding.fbMain.rotation = 0f
        menuItems.forEach { pair ->
            pair.first.visibility = View.INVISIBLE
            pair.second.visibility = View.INVISIBLE
        }
        isMenuOpened = false
    }

    private fun playMenuItemShowAnimation(pair: Pair<FloatingActionButton, CardView>) {
        itemHideAnimation.cancel()
        labelHideAnimation.cancel()
        pair.first.apply {
            startAnimation(itemShowAnimation)
            visible()
        }
        pair.second.apply {
            startAnimation(labelShowAnimation)
            visible()
        }
    }

    private fun playMenuItemHideAnimation(pair: Pair<FloatingActionButton, CardView>) {
        itemShowAnimation.cancel()
        labelShowAnimation.cancel()
        pair.first.apply {
            startAnimation(itemHideAnimation)
            visibility = View.INVISIBLE
        }
        pair.second.apply {
            startAnimation(labelHideAnimation)
            visibility = View.INVISIBLE
        }
    }

    override fun onDetachedFromWindow() {
        uiHandler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }

}