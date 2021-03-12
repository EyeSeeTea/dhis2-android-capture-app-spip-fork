package org.dhis2.animations

import android.view.View
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.Transformation
import org.dhis2.utils.idlingresource.CountingIdlingResourceSingleton.decrement
import org.dhis2.utils.idlingresource.CountingIdlingResourceSingleton.increment

fun View.collapse(callback: () -> Unit) {
    val initialHeight = measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                visibility = View.GONE
                callback.invoke()
            } else {
                layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = 200
    a.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            increment()
        }

        override fun onAnimationEnd(animation: Animation) {
            decrement()
        }

        override fun onAnimationRepeat(animation: Animation) {}
    })
    startAnimation(a)
}

fun View.expand(callback: () -> Unit) {
    callback.invoke()
    val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
        (parent as View).width,
        View.MeasureSpec.EXACTLY
    )
    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
        0,
        View.MeasureSpec.UNSPECIFIED
    )
    measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight: Int = measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1
    visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            layoutParams.height = (targetHeight * interpolatedTime).toInt()
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            increment()
        }

        override fun onAnimationEnd(animation: Animation) {
            decrement()
            callback.invoke()
        }

        override fun onAnimationRepeat(animation: Animation) {}
    })
    a.duration = 200
    startAnimation(a)
}

fun View.show(){
    if(visibility != View.VISIBLE){
        animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .withStartAction {
                scaleX = 0f
                scaleY = 0f
                visibility = View.VISIBLE
            }
            .start()
    }
}

fun View.hide(){
    if(visibility != View.GONE){
        animate()
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .withEndAction { visibility = View.GONE }
            .start()
    }
}