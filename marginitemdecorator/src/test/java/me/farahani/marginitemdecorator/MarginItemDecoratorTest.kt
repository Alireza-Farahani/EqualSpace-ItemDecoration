package me.farahani.marginitemdecorator

import android.graphics.Rect
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@RunWith(Parameterized::class)
class MarginItemDecoratorTest(private val config: Config, private val result: Margin) {

    companion object {
        private const val ltr = ViewCompat.LAYOUT_DIRECTION_LTR
        private const val rtl = ViewCompat.LAYOUT_DIRECTION_RTL
        private const val vertical = LinearLayout.VERTICAL
        private const val horizontal = LinearLayout.HORIZONTAL

        @kotlin.jvm.JvmStatic
        @Parameters()//name = "{index}: isValid({0})={1}")
        fun data() = listOf(
            // Margin 0
            arrayOf(
                Config(margin = 0, spans = 1, pos = 0, count = 1, orient = vertical, dir = ltr),
                Margin(0, 0, 0, 0),
            ),
            arrayOf(
                Config(margin = 0, spans = 1, pos = 1, count = 3, orient = horizontal, dir = rtl),
                Margin(0, 0, 0, 0),
            ),

            // count < span (Edge case when RV is dimensions are WRAP_CONTENT
            arrayOf(
                Config(margin = 8, spans = 2, pos = 0, count = 1, orient = vertical, dir = ltr),
                Margin(8, 8, 8, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 3, pos = 0, count = 2, orient = horizontal, dir = ltr),
                Margin(8, 8, 4, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 3, pos = 1, count = 2, orient = horizontal, dir = ltr),
                Margin(4, 8, 4, 8),
            ),

            // span 1, vertical, ltr
            arrayOf(
                Config(margin = 8, spans = 1, pos = 0, count = 1, orient = vertical, dir = ltr),
                Margin(8, 8, 8, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 1, pos = 0, count = 2, orient = vertical, dir = ltr),
                Margin(8, 8, 4, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 1, pos = 1, count = 2, orient = vertical, dir = ltr),
                Margin(4, 8, 8, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 1, pos = 1, count = 3, orient = vertical, dir = ltr),
                Margin(4, 8, 4, 8),
            ),

            // span 1, vertical, rtl
            arrayOf(
                Config(margin = 8, spans = 1, pos = 1, count = 3, orient = vertical, dir = rtl),
                Margin(4, 8, 4, 8),
            ),

            // span 1, horizontal, ltr
            arrayOf(
                Config(margin = 8, spans = 1, pos = 1, count = 3, orient = horizontal, dir = ltr),
                Margin(8, 4, 8, 4),
            ),

            // span 1, horizontal, rtl
            arrayOf(
                Config(margin = 8, spans = 1, pos = 1, count = 3, orient = horizontal, dir = rtl),
                Margin(8, 4, 8, 4),
            ),

            // span 2, vertical, ltr
            arrayOf(
                Config(margin = 8, spans = 2, pos = 0, count = 2, orient = vertical, dir = ltr),
                Margin(8, 4, 8, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 1, count = 2, orient = vertical, dir = ltr),
                Margin(8, 8, 8, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 2, count = 4, orient = vertical, dir = ltr),
                Margin(4, 4, 8, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 3, count = 4, orient = vertical, dir = ltr),
                Margin(4, 8, 8, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 2, count = 5, orient = vertical, dir = ltr),
                Margin(4, 4, 4, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 3, count = 5, orient = vertical, dir = ltr),
                Margin(4, 8, 4, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 4, count = 5, orient = vertical, dir = ltr),
                Margin(4, 4, 8, 8),
            ),

            // span 2, vertical, rtl
            arrayOf(
                Config(margin = 8, spans = 2, pos = 0, count = 2, orient = vertical, dir = rtl),
                Margin(8, 8, 8, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 2, count = 5, orient = vertical, dir = rtl),
                Margin(4, 8, 4, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 3, count = 5, orient = vertical, dir = rtl),
                Margin(4, 4, 4, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 4, count = 5, orient = vertical, dir = rtl),
                Margin(4, 8, 8, 4),
            ),

            // span 2, horizontal, ltr
            arrayOf(
                Config(margin = 8, spans = 2, pos = 0, count = 2, orient = horizontal, dir = ltr),
                Margin(8, 8, 4, 8),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 2, count = 5, orient = horizontal, dir = ltr),
                Margin(8, 4, 4, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 3, count = 5, orient = horizontal, dir = ltr),
                Margin(4, 4, 8, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 4, count = 5, orient = horizontal, dir = ltr),
                Margin(8, 8, 4, 4),
            ),

            // span 2 horizontal, rtl
            arrayOf(
                Config(margin = 8, spans = 2, pos = 2, count = 5, orient = horizontal, dir = rtl),
                Margin(8, 4, 4, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 2, pos = 3, count = 5, orient = horizontal, dir = rtl),
                Margin(4, 4, 8, 4),
            ),

            // span >2
            arrayOf(
                Config(margin = 8, spans = 3, pos = 4, count = 7, orient = vertical, dir = ltr),
                Margin(4, 4, 4, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 3, pos = 3, count = 7, orient = vertical, dir = rtl),
                Margin(4, 8, 4, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 3, pos = 6, count = 7, orient = horizontal, dir = ltr),
                Margin(8, 8, 4, 4),
            ),
            arrayOf(
                Config(margin = 8, spans = 3, pos = 2, count = 7, orient = horizontal, dir = rtl),
                Margin(4, 8, 8, 4),
            ),
        )
    }


    @get:Rule
    var exceptionRule: ExpectedException = ExpectedException.none()

    private lateinit var rect: Rect

    @Before
    fun setUp() {
        rect = Rect()
    }

    data class Config(
        val margin: Int, val spans: Int,
        val pos: Int, val count: Int,
        val orient: Int, val dir: Int
    )

    data class Margin(val top: Int, val right: Int, val bottom: Int, val left: Int)


    @Test
    fun `test margins are calculated correctly`() {
        val decorator = MarginItemDecorator(config.margin, config.spans, config.orient)
        val res = decorator.setItemMargin(
            rect,
            itemPosition = config.pos, itemCount = config.count, layoutDirection = config.dir,
        )
        assertThat(result).isEqualTo(Margin(res.top, res.right, res.bottom, res.left))
    }

    /* @Test // TODO: parametrize this and next
     fun `expect exception when position is not less that item count`() {
         exceptionRule.expect(IllegalArgumentException::class.java)
         val decorator = MarginItemDecorator(8)
         decorator.setItemMargin(rect, 0, 0, vertical, ltr)
     }

     @Test
     fun `expect exception when position is not less that item count 2`() {
         exceptionRule.expect(IllegalArgumentException::class.java)
         val decorator = MarginItemDecorator(18, 5)
         decorator.setItemMargin(rect, 20, 14, vertical, ltr)
     }*/
}