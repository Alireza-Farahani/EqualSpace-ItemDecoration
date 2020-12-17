package me.farahani.marginitemdecorator

import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import me.farahani.marginitemdecorator.MarginItemDecorator.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Mockito
import org.mockito.Mockito.`when`


class MarginItemDecoratorExceptionsTest {

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
                P(spans = 2, pos = 0, count = 1, orient = V, layDir = ltr, true),
                Margin(8, 8, 8, 8),
            ),
            arrayOf(
                P(spans = 3, pos = 0, count = 2, orient = H, layDir = ltr, false),
                Margin(0, 0, 4, 0),
            ),

            // span 1
            arrayOf(
                P(spans = 1, pos = 0, count = 1, orient = V, layDir = ltr, true),
                Margin(8, 8, 8, 8),
            ),
            arrayOf(
                P(spans = 1, pos = 0, count = 2, orient = V, layDir = rtl, false),
                Margin(0, 0, 4, 0),
            ),
            arrayOf(
                P(spans = 1, pos = 1, count = 2, orient = H, layDir = ltr, false),
                Margin(0, 0, 0, 4),
            ),
            arrayOf(
                P(spans = 1, pos = 1, count = 3, orient = H, layDir = rtl, true),
                Margin(8, 4, 8, 4),
            ),

            // span >2
            arrayOf(
                P(spans = 3, pos = 4, count = 7, orient = V, layDir = rtl, false),
                Margin(4, 4, 4, 4),
            ),
            arrayOf(
                P(spans = 3, pos = 3, count = 7, orient = V, layDir = ltr, true),
                Margin(4, 4, 4, 8),
            ),
            arrayOf(
                P(spans = 3, pos = 6, count = 7, orient = H, layDir = ltr, false),
                Margin(0, 0, 4, 4),
            ),
            arrayOf(
                P(spans = 3, pos = 2, count = 7, orient = H, layDir = rtl, true),
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
        val orient: Int, val layDir: Int,
        val edge: Boolean
    )

    data class Margin(val top: Int, val right: Int, val bottom: Int, val left: Int)

    @Test
    fun `test margins are calculated correctly`() {
        val decorator = MarginItemDecorator(8, p.spans, p.orient, p.edge)
        val mockedView = Mockito.mock(View::class.java)
        val mockedRVState = Mockito.mock(RecyclerView.State::class.java)

        val mockedAdapter = Mockito.mock(RecyclerView.Adapter::class.java)
        `when`(mockedAdapter.itemCount).thenReturn(p.count)

        val mockedRV = Mockito.mock(RecyclerView::class.java)
        `when`(mockedRV.adapter).thenReturn(mockedAdapter)
        `when`(mockedRV.getChildAdapterPosition(mockedView)).thenReturn(p.pos)
        `when`(mockedRV.layoutDirection).thenReturn(p.layDir)

        decorator.getItemOffsets(rect, mockedView, mockedRV, mockedRVState)
        assertThat(result).isEqualTo(Margin(rect.top, rect.right, rect.bottom, rect.left))
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