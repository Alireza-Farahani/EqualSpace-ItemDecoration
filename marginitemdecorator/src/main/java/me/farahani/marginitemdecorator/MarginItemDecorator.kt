package me.farahani.marginitemdecorator

import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import me.farahani.marginitemdecorator.MarginItemDecorator.Direction.*
import kotlin.math.ceil

/**
 * @author Alireza Farahani. Contact me at ar.d.farahani@gmail.com
 *
 * @param margin Margin/Gap/Space between items in pixel
 * @param spanCount Number of LayoutManager spans (column for vertical layout, rows for horizontal)
 * @param orientation LayoutManager orientation. One of [VERTICAL] or [HORIZONTAL]
 */
class MarginItemDecorator(
    private val margin: Int,
    private val spanCount: Int,
    @Orientation private val orientation: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    init {
        require(spanCount >= 1) { "Span count must be greater than zero" }
        require(margin >= 0) { "Item margins can not be negative" }
    }

    // LTR = 0, RTL = 1, Horizontal = 0, Vertical = 1
    private val strategies = arrayOf(
        HorizontalLTRStrategy(),
        HorizontalRTLStrategy(),
        VerticalLTRStrategy(),
        VerticalRTLStrategy(),
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemCount =
            parent.adapter?.itemCount ?: throw IllegalStateException("Adapter must not be null")
        val itemPosition = parent.getChildAdapterPosition(view)
        val layoutDirection = parent.layoutDirection

        setItemMargin(outRect, itemCount, itemPosition, layoutDirection, includeEdge)
    }

    internal fun setItemMargin(
        outRect: Rect,
        itemPosition: Int,
        itemCount: Int,
        layoutDirection: Int,
        includeEdge: Boolean,
    ): Rect {
        val strategy: LinearListMarginStrategy = findProperStrategy(
            layoutDirection = layoutDirection,
            orientation = orientation
        )

        outRect.left = margin / 2
        outRect.right = margin / 2
        outRect.top = margin / 2
        outRect.bottom = margin / 2

        val borderMargin = if (includeEdge) margin else 0

        val params = ListItemParams(
            itemPosition, spanCount, itemCount
        )

        val itemPositionFinder: LinearListItemPosition = LinearListItemPositionImpl()

        if (itemPositionFinder.isAtScrollingStart(params))
            setRectMargin(outRect, borderMargin, strategy.scrollingStart())
        if (itemPositionFinder.isAtScrollingEnd(params))
            setRectMargin(outRect, borderMargin, strategy.scrollingEnd())
        if (itemPositionFinder.isAtSideStart(params))
            setRectMargin(outRect, borderMargin, strategy.sideStart())
        if (itemPositionFinder.isAtSideEnd(params))
            setRectMargin(outRect, borderMargin, strategy.sideEnd())

        return outRect
    }

    private fun setRectMargin(rect: Rect, margin: Int, direction: Direction) {
        when (direction) {
            Top -> rect.top = margin
            Right -> rect.right = margin
            Bottom -> rect.bottom = margin
            Left -> rect.left = margin
        }
    }

    internal fun findProperStrategy(
        layoutDirection: Int,
        orientation: Int
    ): LinearListMarginStrategy {
        return strategies[layoutDirection + (2*orientation)]
    }

    internal enum class Direction {
        Top, Right, Bottom, Left
    }

    // -----------------------------------------------------------------

    internal interface LinearListItemPosition {
        fun isAtScrollingStart(param: ListItemParams): Boolean
        fun isAtScrollingEnd(param: ListItemParams): Boolean
        fun isAtSideStart(param: ListItemParams): Boolean
        fun isAtSideEnd(param: ListItemParams): Boolean
    }

    internal class LinearListItemPositionImpl : LinearListItemPosition {
        override fun isAtScrollingStart(param: ListItemParams): Boolean {
            return param.itemPosition < param.spanCount
        }

        override fun isAtScrollingEnd(param: ListItemParams): Boolean {
            return row(param.itemPosition, param.spanCount) ==
                    rows(param.itemCount, param.spanCount)
        }

        override fun isAtSideStart(param: ListItemParams): Boolean {
            return param.itemPosition % param.spanCount == 0
        }

        override fun isAtSideEnd(param: ListItemParams): Boolean {
            return param.itemCount == 1 || param.itemPosition % param.spanCount == (param.spanCount - 1)
        }

        private fun row(position: Int, spanCount: Int): Int {
            return ceil((position + 1).toFloat() / spanCount.toFloat()).toInt()
        }

        private fun rows(itemCount: Int, spanCount: Int): Int {
            return ceil(itemCount.toFloat() / spanCount.toFloat()).toInt()
        }
    }

    internal interface LinearListMarginStrategy {
        fun scrollingStart(): Direction
        fun scrollingEnd(): Direction
        fun sideStart(): Direction
        fun sideEnd(): Direction
    }

    internal class HorizontalLTRStrategy : LinearListMarginStrategy {
        override fun scrollingStart() = Left
        override fun scrollingEnd() = Right
        override fun sideStart() = Top
        override fun sideEnd() = Bottom
    }
    internal class HorizontalRTLStrategy : LinearListMarginStrategy {
        override fun scrollingStart() = Right
        override fun scrollingEnd() = Left
        override fun sideStart() = Top
        override fun sideEnd() = Bottom
    }
    internal class VerticalLTRStrategy : LinearListMarginStrategy {
        override fun scrollingStart() = Top
        override fun scrollingEnd() = Bottom
        override fun sideStart() = Left
        override fun sideEnd() = Right
    }
    internal class VerticalRTLStrategy : LinearListMarginStrategy {
        override fun scrollingStart() = Top
        override fun scrollingEnd() = Bottom
        override fun sideStart() = Right
        override fun sideEnd() = Left
    }

    internal data class ListItemParams(
        val itemPosition: Int,
        val spanCount: Int,
        val itemCount: Int,
    )
}
