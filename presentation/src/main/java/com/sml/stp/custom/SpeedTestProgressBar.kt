package com.sml.stp.custom

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.sml.stp.R
import com.sml.stp.utils.CanvasUtils
import com.sml.stp.utils.UiUtils
import java.util.*

class SpeedTestProgressBar : View {

    companion object {
        const val PB_LINE_WIDTH_DP = 18F
        const val PB_SCALE_NUM_TEXT_SIZE_SP = 12F
        const val PB_CURRENT_SPEED_TEXT_SIZE_SP = 24F
        const val PB_CURRENT_SPEED_VALUE_TEXT_SIZE_SP = 10F
        const val PB_CURRENT_SPEED_OFFSET_DP = 8F
        const val START_ANGLE = 135F
        const val SWEEP_ANGLE = 270F
        const val START_GRADIENT_ANGLE = 90F //Make the gradient start from 90 degrees
        const val ARROW_OFFSET = START_ANGLE

        const val MEASURED_TEXT_1_SYMBOL = "0"
        const val MEASURED_TEXT_3_SYMBOL = "000"

        //Hardcoded Angle
        private val measures: MutableMap<Int, Float> = mutableMapOf(
                0 to 135f,
                1 to 168f,
                5 to 200f,
                10 to 233f,
                20 to 270f,
                30 to 307f,
                50 to 340f,
                75 to 372f,
                100 to 405F
        )
    }

    private var backPaint: Paint
    private var progressFillPaint: Paint
    private var textPaint: Paint
    private var currentSpeedTextPaint: Paint
    private var currentSpeedValueTextPaint: Paint

    private var baseArcRect: RectF

    private var progressGradientColourStart: Int = 0
    private var progressGradientColourMid: Int = 0
    private var progressGradientColourEnd: Int = 0

    private val gradientColors = mutableListOf<Int>()

    private var widthCanvas: Float = 0F
    private var heightCanvas: Float = 0F
    private var radiusCanvas: Float = 0F
    private var x0: Float = 0F
    private var y0: Float = 0F
    private var textLength: Float = 0F
    private var viewWidth: Int = 0

    //Thickness of the arc
    private var thickness: Float = 0F

    private var arrowAngle = ARROW_OFFSET
    private var currentSpeed = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initGradientColors()

        thickness = UiUtils.dpToPx(context, PB_LINE_WIDTH_DP)

        progressFillPaint = CanvasUtils.makeStrokePaint(UiUtils.dpToPx(context, PB_LINE_WIDTH_DP), -1)

        backPaint = CanvasUtils.makeStrokePaint(UiUtils.dpToPx(context, PB_LINE_WIDTH_DP), -1)
        backPaint.color = ContextCompat.getColor(context, R.color.stProgressBarBackground)

        textPaint = CanvasUtils.makeText(ContextCompat.getColor(context, R.color.stValueText), UiUtils.spToPx(context, PB_SCALE_NUM_TEXT_SIZE_SP))

        currentSpeedTextPaint = CanvasUtils.makeText(ContextCompat.getColor(context, R.color.stValueText), UiUtils.spToPx(context, PB_CURRENT_SPEED_TEXT_SIZE_SP))
        currentSpeedValueTextPaint = CanvasUtils.makeText(ContextCompat.getColor(context, R.color.stValueText), UiUtils.spToPx(context, PB_CURRENT_SPEED_VALUE_TEXT_SIZE_SP))

        baseArcRect = RectF(0f, 0f, 0f, 0f)
        setProgressColourAsGradient(false)
    }

    private fun initGradientColors() {
        progressGradientColourStart = ContextCompat.getColor(context, R.color.stProgressBarGradientStart)
        progressGradientColourMid = ContextCompat.getColor(context, R.color.stProgressBarGradientMid)
        progressGradientColourEnd = ContextCompat.getColor(context, R.color.stProgressBarGradientEnd)

        gradientColors.add(progressGradientColourStart)
        gradientColors.add(progressGradientColourMid)
        gradientColors.add(progressGradientColourEnd)
    }

    private fun setProgressColourAsGradient(invalidateNow: Boolean) {
        val sweepGradient = SweepGradient(baseArcRect.centerX(), baseArcRect.centerY(), gradientColors.toIntArray(), null)

        val matrix = Matrix()
        matrix.setRotate(START_GRADIENT_ANGLE, baseArcRect.centerX(), baseArcRect.centerY())
        sweepGradient.setLocalMatrix(matrix)

        progressFillPaint.shader = sweepGradient
        if (invalidateNow) {
            invalidate()
        }
    }

    fun setArrowAngleBySpeed(speed: Float) {
        currentSpeed = speed
        arrowAngle = getMeterAngle(speed)
        invalidate()
    }

    private fun getMeterAngle(inputSpeed: Float): Float {
        if (inputSpeed <= 0) {
            return measures.getValue(0)
        }
        if (inputSpeed >= 100) {
            return measures.getValue(100)
        }

        var result = 0f

        var endAngle = 0F
        var endValue = 0

        val reversed = measures.asIterable().reversed()
        for (i in 0 until reversed.size) {
            val speed = reversed[i].key
            val angle = reversed[i].value

            if (inputSpeed == speed.toFloat()) {
                result = angle
                break
            }

            if (inputSpeed > speed.toFloat()) {
                val startAngle = angle
                val startValue = speed

                val diffAngle = endAngle - startAngle
                val diffValue = (endValue - startValue).toFloat()
                val deltaSpeed = inputSpeed - startValue

                result = diffAngle / diffValue * deltaSpeed + startAngle
                break
            }
            endAngle = angle
            endValue = speed
        }
        return result
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //Ensures arc is within the rectangle
        initExtraVariables(width, height)

        //I do radius - thickness so that the arc is within the rectangle
        val baseArcLeft = width / 2 - (radiusCanvas - thickness)
        val baseArcTop = height / 2 - (radiusCanvas - thickness)
        val baseArcRight = width / 2 + (radiusCanvas - thickness)
        val baseArcBottom = height / 2 + (radiusCanvas - thickness)

        baseArcRect.set(baseArcLeft, baseArcTop, baseArcRight, baseArcBottom)
        //Recalculate the gradient
        setProgressColourAsGradient(true)
    }

    private fun initExtraVariables(width: Int, height: Int) {
        val radius = (Math.min(width, height) / 2F)//radius

        widthCanvas = width.toFloat()
        heightCanvas = height.toFloat()
        radiusCanvas = radius
        x0 = widthCanvas / 2
        y0 = heightCanvas / 2
        textLength = textPaint.measureText(MEASURED_TEXT_3_SYMBOL)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        this.viewWidth = widthSize

        //Measure Width
        width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> widthSize
            View.MeasureSpec.UNSPECIFIED -> widthSize
            else -> widthSize
        }

        height = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> widthSize
            View.MeasureSpec.UNSPECIFIED -> widthSize
            else -> widthSize
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawArc(baseArcRect, START_ANGLE, SWEEP_ANGLE, false, backPaint)
        canvas.drawArc(baseArcRect, START_ANGLE, calculateAngleForProgressBar(arrowAngle), false, progressFillPaint)

        drawArrow(canvas)
        drawNumberScale(canvas)
        drawCurrentSpeed(canvas)
    }

    private fun calculateAngleForProgressBar(origin: Float): Float {
        return origin - ARROW_OFFSET
    }


    private fun drawArrow(canvas: Canvas) {
        val bitmap = CanvasUtils.getBitmapFromVectorDrawable(context, R.drawable.pb_speedtest_arrow)

        val matrix = Matrix()

        val rotation = arrowAngle
        val extraLength = textLength / 2

        //sacled
        val scaledWidth = viewWidth / 3
        val scaledHeight = Math.round(scaledWidth / 8.5f)

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

        matrix.postTranslate(0f, (-scaledBitmap.height / 2).toFloat())

        matrix.postRotate(rotation)
        matrix.postTranslate(x0, y0)


        canvas.drawBitmap(scaledBitmap, matrix, null)
    }

    private fun drawNumberScale(canvas: Canvas) {
        val extraLength = textLength
        val calculatedRadius = radiusCanvas - thickness - extraLength

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        drawStyledNumberScale(canvas, x0, y0, calculatedRadius)
    }

    private fun drawStyledNumberScale(canvas: Canvas, x0: Float, y0: Float, radius: Float) {
        val fontMetrics = textPaint.fontMetrics
        val height = thickness / 2 - (fontMetrics.descent - fontMetrics.leading)

        val testPaint = Paint()
        testPaint.color = ContextCompat.getColor(context, R.color.yellow)
        testPaint.strokeWidth = 20f

        for ((key, value) in measures) {
            canvas.drawText(key.toString(), getCalculatedX(x0, radius, value.toDouble(), getXOffset(key.toString())), getCalculatedY(y0, radius, value.toDouble(), height), textPaint)
        }
    }

    private fun drawCurrentSpeed(canvas: Canvas) {
        currentSpeedTextPaint.textAlign = Paint.Align.CENTER
        currentSpeedTextPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        currentSpeedValueTextPaint.textAlign = Paint.Align.CENTER
        currentSpeedValueTextPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val fontMetrics = currentSpeedTextPaint.fontMetrics
        val height = fontMetrics.descent - fontMetrics.leading
        val y = getCalculatedY(y0, radiusCanvas - thickness, START_ANGLE.toDouble(), height)

        val yTextValue = y + height + UiUtils.dpToPx(context, PB_CURRENT_SPEED_OFFSET_DP)
        canvas.drawText(String.format(Locale.US, "%.2f", currentSpeed), x0, y, currentSpeedTextPaint)
        canvas.drawText(resources.getString(R.string.mbps), x0, yTextValue, currentSpeedValueTextPaint)
    }

    private fun getCalculatedX(x0: Float, radius: Float, angle: Double, offset: Float): Float {
        return (x0 + radius * Math.cos(Math.toRadians(angle)) - offset * Math.cos(Math.toRadians(angle))).toFloat()
    }

    private fun getCalculatedY(y0: Float, radius: Float, angle: Double, offsetY: Float): Float {
        return (y0 + radius * Math.sin(Math.toRadians(angle)) + offsetY).toFloat()
    }

    private fun getXOffset(string: String): Float {
        var offset = 0f
        if (string.length == 2) {
            offset = textPaint.measureText(MEASURED_TEXT_1_SYMBOL) / 2
        } else if (string.length == 3) {
            offset = textPaint.measureText(MEASURED_TEXT_1_SYMBOL)
        }
        return offset
    }
}