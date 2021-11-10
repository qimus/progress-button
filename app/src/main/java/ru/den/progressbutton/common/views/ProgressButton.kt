package ru.den.progressbutton.common.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import ru.den.progressbutton.R
import ru.den.progressbutton.utils.getColorFromAttr
import ru.den.progressbutton.utils.getTextWidth
import ru.den.progressbutton.utils.px

class ProgressButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ProgressButton"
    }

    private var buttonText: String = ""

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textSize = 16.px.toFloat()
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColorFromAttr(R.attr.colorPrimary)
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 2.px.toFloat()
    }

    private val buttonRect = RectF()
    private val progressRect = RectF()

    private var buttonRadius = 16.px.toFloat()

    private var offset = 0f

    private var rotationAnimator: ValueAnimator? = null
    private var widthAnimator: ValueAnimator? = null
    private var loading = false
    private var startAngle = 0f

    private var drawChecks = false

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton)
        buttonText = ta.getString(R.styleable.ProgressButton_progressButton_text) ?: ""
        ta.recycle()
    }

    fun done() {
        loading = false
        drawChecks = true
        rotationAnimator?.cancel()
        invalidate()
    }

    fun reset() {
        offset = 0f
        loading = false
        drawChecks = false
        rotationAnimator?.cancel()
        widthAnimator?.cancel()

        widthAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                offset = (measuredWidth - measuredHeight) / 2f * value
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    isClickable = true
                }
            })
            duration = 300
            start()
        }
    }

    fun startLoading() {
        widthAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                offset = (measuredWidth - measuredHeight) / 2f * value
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    startProgressAnimation()
                }
            })
            duration = 200
        }
        loading = true
        isClickable = false
        widthAnimator?.start()
    }

    private fun startProgressAnimation() {
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            addUpdateListener {
                startAngle = it.animatedValue as Float
                invalidate()
            }
            duration = 800
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    loading = false
                    invalidate()
                }
            })
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        widthAnimator?.cancel()
        rotationAnimator?.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        buttonRadius = measuredHeight / 2f
        buttonRect.apply {
            top = 0f
            left = 0f + offset
            right = measuredWidth.toFloat() - offset
            bottom = measuredHeight.toFloat()
        }
        canvas.drawRoundRect(buttonRect, buttonRadius, buttonRadius, backgroundPaint)

        if (offset < (measuredWidth - measuredHeight) / 2f) {
            val textX = measuredWidth / 2f - textPaint.getTextWidth(buttonText) / 2f
            val textY = measuredHeight / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(buttonText, textX, textY, textPaint)
        }

        if (loading && offset == (measuredWidth - measuredHeight) / 2f) {
            progressRect.left = measuredWidth / 2f - buttonRect.width() / 4
            progressRect.top = measuredHeight / 2f - buttonRect.width() / 4
            progressRect.right = measuredWidth / 2f + buttonRect.width() / 4
            progressRect.bottom = measuredHeight / 2f + buttonRect.width() / 4
            canvas.drawArc(progressRect, startAngle, 140f, false, progressPaint)
        }

        if (drawChecks) {
            canvas.save()
            canvas.rotate(45f, measuredWidth / 2f, measuredHeight / 2f)
            val x1 = measuredWidth / 2f - buttonRect.width() / 8
            val y1 = measuredHeight / 2f + buttonRect.width() / 4
            val x2 = measuredWidth / 2f + buttonRect.width() / 8
            val y2 = measuredHeight / 2f + buttonRect.width() / 4
            val x3 = measuredWidth / 2f + buttonRect.width() / 8
            val y3 = measuredHeight / 2f - buttonRect.width() / 4
            canvas.drawLine(x1, y1, x2, y2, progressPaint)
            canvas.drawLine(x2, y2, x3, y3, progressPaint)
            canvas.restore()
        }
    }
}
