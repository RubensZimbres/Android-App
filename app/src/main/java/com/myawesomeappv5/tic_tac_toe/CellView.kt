package com.myawesomeappv5.tic_tac_toe

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.min

/**
 * One board cell. Draws an X or O as thick rounded strokes with a soft glow and a drop shadow,
 * animates the mark being drawn in, and can pulse to highlight a winning line.
 */
class CellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var mark: Char = TicTacToeGame.EMPTY
        private set

    private val xColor = ContextCompat.getColor(context, R.color.mark_x)
    private val oColor = ContextCompat.getColor(context, R.color.mark_o)
    private val shadowColor = ContextCompat.getColor(context, R.color.mark_shadow)

    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val markBounds = RectF()

    /** 0..1, how much of the mark has been drawn. */
    private var drawProgress = 1f
    /** 0..1, strength of the winning-line pulse. */
    private var highlight = 0f

    private var drawAnimator: ValueAnimator? = null
    private var pulseAnimator: ValueAnimator? = null

    init {
        // Blur mask filters and shadows on strokes need software rendering on older devices.
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        updateContentDescription()
    }

    fun setMark(newMark: Char, animate: Boolean) {
        mark = newMark
        drawAnimator?.cancel()
        if (animate && newMark != TicTacToeGame.EMPTY) {
            drawProgress = 0f
            drawAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = DRAW_DURATION_MS
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    drawProgress = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            drawProgress = 1f
        }
        updateContentDescription()
        invalidate()
    }

    fun setHighlighted(highlighted: Boolean) {
        pulseAnimator?.cancel()
        pulseAnimator = null
        if (highlighted) {
            pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = PULSE_DURATION_MS
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    highlight = it.animatedValue as Float
                    val scale = 1f + 0.08f * highlight
                    scaleX = scale
                    scaleY = scale
                    invalidate()
                }
                start()
            }
        } else {
            highlight = 0f
            scaleX = 1f
            scaleY = 1f
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = min(w, h).toFloat()
        val inset = size * 0.24f
        val left = (w - size) / 2f + inset
        val top = (h - size) / 2f + inset
        markBounds.set(left, top, left + size - 2 * inset, top + size - 2 * inset)

        val stroke = size * 0.11f
        markPaint.strokeWidth = stroke
        markPaint.setShadowLayer(stroke * 0.5f, stroke * 0.3f, stroke * 0.6f, shadowColor)
        glowPaint.strokeWidth = stroke * 1.6f
        glowPaint.maskFilter = BlurMaskFilter(stroke * 1.1f, BlurMaskFilter.Blur.NORMAL)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val color = when (mark) {
            TicTacToeGame.PLAYER -> xColor
            TicTacToeGame.COMPUTER -> oColor
            else -> return
        }
        markPaint.color = color
        glowPaint.color = color
        glowPaint.alpha = (110 + 145 * highlight).toInt()

        // Glow first, then the crisp stroke with its drop shadow on top.
        drawMark(canvas, glowPaint)
        drawMark(canvas, markPaint)
    }

    private fun drawMark(canvas: Canvas, paint: Paint) {
        val b = markBounds
        if (mark == TicTacToeGame.COMPUTER) {
            canvas.drawArc(b, -90f, 360f * drawProgress, false, paint)
            return
        }
        // X: first stroke during the first half of the animation, second stroke during the rest.
        val first = (drawProgress * 2f).coerceAtMost(1f)
        val second = (drawProgress * 2f - 1f).coerceAtLeast(0f)
        canvas.drawLine(b.left, b.top, b.left + b.width() * first, b.top + b.height() * first, paint)
        if (second > 0f) {
            canvas.drawLine(b.right, b.top, b.right - b.width() * second, b.top + b.height() * second, paint)
        }
    }

    private fun updateContentDescription() {
        contentDescription = context.getString(
            when (mark) {
                TicTacToeGame.PLAYER -> R.string.cell_x
                TicTacToeGame.COMPUTER -> R.string.cell_o
                else -> R.string.cell_empty
            }
        )
    }

    override fun onDetachedFromWindow() {
        drawAnimator?.cancel()
        pulseAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    private companion object {
        const val DRAW_DURATION_MS = 260L
        const val PULSE_DURATION_MS = 450L
    }
}
