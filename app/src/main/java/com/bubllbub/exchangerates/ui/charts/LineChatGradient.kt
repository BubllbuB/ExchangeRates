package com.bubllbub.exchangerates.ui.charts

import android.content.Context
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import java.lang.Math.pow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LineChatGradient(ctx: Context, attrs: AttributeSet): LineChart(ctx, attrs) {
    fun setGridGradientColor(startColor: Int, finishColor: Int) {
        val angleInRadians = Math.toRadians(45.0)
        val length = sqrt(pow(this.height.toDouble(),2.0)+pow(this.width.toDouble(),2.0))

        val endX = cos(angleInRadians) * length
        val endY = sin(angleInRadians) * length


        mGridBackgroundPaint.shader = LinearGradient( 0f, endX.toFloat(), endY.toFloat(),0f,
            startColor, finishColor, Shader.TileMode.REPEAT)
    }

    fun setGridCornerRadius(radius: Float) {
        mGridBackgroundPaint.style = Paint.Style.FILL_AND_STROKE
        mGridBackgroundPaint.pathEffect = CornerPathEffect(radius)
    }
}