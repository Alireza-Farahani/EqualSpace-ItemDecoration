package me.farahani.marginitemdecorator

import android.graphics.Rect
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.google.common.truth.Truth.assertThat
import me.farahani.marginitemdecorator.MarginItemDecorator.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


class MarginItemDecoratorExceptions {

    @get:Rule
    var exceptionRule: ExpectedException = ExpectedException.none()

    @Test
    fun `expect exception when margin is negative`() {
        exceptionRule.expect(IllegalArgumentException::class.java)
        exceptionRule.expectMessage("Item margins can not be negative")
        MarginItemDecorator(-2, 3, LinearLayout.HORIZONTAL, false)
    }

    @Test
    fun `expect exception when span count is less than 1`() {
        exceptionRule.expect(IllegalArgumentException::class.java)
        exceptionRule.expectMessage("Span count must be greater than zero")
        MarginItemDecorator(3, -2, LinearLayout.HORIZONTAL, false)
    }
}

// TODO: What to do about margin=0?
@RunWith(Parameterized::class)
class MarginItemDecoratorTest(private val p: P, private val result: Margin) {

    companion object {
        private const val ltr = ViewCompat.LAYOUT_DIRECTION_LTR
        private const val rtl = ViewCompat.LAYOUT_DIRECTION_RTL
        private const val V = LinearLayout.VERTICAL
        private const val H = LinearLayout.HORIZONTAL

        @kotlin.jvm.JvmStatic
        @Parameters()//name = "{index}: isValid({0})={1}")
        fun data() = listOf(

            // count < span (Edge case when RV is dimensions are WRAP_CONTENT
            arrayOf(
                P(spans = 2, pos = 0, count = 1, orient = V, dir = ltr, true),
                Margin(8, 8, 8, 8),
            ),
            arrayOf(
                P(spans = 3, pos = 0, count = 2, orient = H, dir = ltr, false),
                Margin(0, 0, 4, 0),
            ),

            // span 1
            arrayOf(
                P(spans = 1, pos = 0, count = 1, orient = V, dir = ltr, true),
                Margin(8, 8, 8, 8),
            ),
            arrayOf(
                P(spans = 1, pos = 0, count = 2, orient = V, dir = rtl, false),
                Margin(0, 0, 4, 0),
            ),
            arrayOf(
                P(spans = 1, pos = 1, count = 2, orient = H, dir = ltr, false),
                Margin(0, 0, 0, 4),
            ),
            arrayOf(
                P(spans = 1, pos = 1, count = 3, orient = H, dir = rtl, true),
                Margin(8, 4, 8, 4),
            ),

            // span >2
            arrayOf(
                P(spans = 3, pos = 4, count = 7, orient = V, dir = rtl, false),
                Margin(4, 4, 4, 4),
            ),
            arrayOf(
                P(spans = 3, pos = 3, count = 7, orient = V, dir = ltr, true),
                Margin(4, 4, 4, 8),
            ),
            arrayOf(
                P(spans = 3, pos = 6, count = 7, orient = H, dir = ltr, false),
                Margin(0, 0, 4, 4),
            ),
            arrayOf(
                P(spans = 3, pos = 2, count = 7, orient = H, dir = rtl, true),
                Margin(4, 8, 8, 4),
            ),
        )
    }

    private lateinit var rect: Rect

    @Before
    fun setUp() {
        rect = Rect()
    }

    data class P(
        val spans: Int,
        val pos: Int, val count: Int,
        val orient: Int, val dir: Int,
        val edge: Boolean
    )

    data class Margin(val top: Int, val right: Int, val bottom: Int, val left: Int)

    @Test
    fun `test margins are calculated correctly`() {
        val decorator = MarginItemDecorator(8, p.spans, p.orient, p.edge)
        val res = decorator.setItemMargin(
            rect,
            itemPosition = p.pos, itemCount = p.count,
            layoutDirection = p.dir,
            includeEdge = p.edge
        )
        assertThat(result).isEqualTo(Margin(res.top, res.right, res.bottom, res.left))
    }
}

@RunWith(Parameterized::class)
internal class CorrectMarginStrategy(
    private val orientation: Int,
    private val direction: Int,
    private val result: LinearListMarginStrategy
) {
    companion object {
        private const val ltr = ViewCompat.LAYOUT_DIRECTION_LTR
        private const val rtl = ViewCompat.LAYOUT_DIRECTION_RTL
        private const val vertical = LinearLayout.VERTICAL
        private const val horizontal = LinearLayout.HORIZONTAL

        @kotlin.jvm.JvmStatic
        @Parameters
        fun data() = listOf(
            arrayOf(vertical, ltr, VerticalLTRStrategy()),
            arrayOf(vertical, rtl, VerticalRTLStrategy()),
            arrayOf(horizontal, ltr, HorizontalLTRStrategy()),
            arrayOf(horizontal, rtl, HorizontalRTLStrategy()),
        )
    }

    @Test
    fun `test proper Decorator is picked based on orientation and direction`() {
        val marginDecorator = MarginItemDecorator(0, 2, orientation, false)
        assertThat(result).isInstanceOf(
            marginDecorator.findProperStrategy(direction, orientation)::class.java
        )
    }
}