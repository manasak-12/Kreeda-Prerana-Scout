package com.example.myapplication.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

/**
 * A custom Star Shape for Milestone Badges.
 */
class StarShape(private val numPoints: Int = 5, private val innerRadiusRatio: Float = 0.5f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val outerRadius = size.width / 2
            val innerRadius = outerRadius * innerRadiusRatio
            
            val angleStep = PI / numPoints
            
            moveTo(
                centerX + outerRadius * cos(-PI / 2).toFloat(),
                centerY + outerRadius * sin(-PI / 2).toFloat()
            )
            
            for (i in 1 until numPoints * 2) {
                val radius = if (i % 2 == 0) outerRadius else innerRadius
                val angle = -PI / 2 + i * angleStep
                lineTo(
                    centerX + radius * cos(angle).toFloat(),
                    centerY + radius * sin(angle).toFloat()
                )
            }
            close()
        }
        return Outline.Generic(path)
    }
}
