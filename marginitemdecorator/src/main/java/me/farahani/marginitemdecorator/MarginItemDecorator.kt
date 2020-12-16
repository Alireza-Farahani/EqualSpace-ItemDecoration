package me.farahani.marginitemdecorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import kotlin.math.ceil

/**
 * @param margin Margin/Gap/Space in pixel
 * @
 */
class MarginItemDecorator(
    private val margin: Int,
    private val spanCount: Int = 1,
    @Orientation private val orientation: Int,
//    private val includeEdge: Boolean = true
) : RecyclerView.ItemDecoration() {

    init {
        require(spanCount >= 1) { "Span count must be greater than zero" }
        require(margin >= 0) { "Item margins can not be negative" }
    }

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
//        val orientation = findOrientation(
//            parent.layoutManager ?: throw IllegalStateException("Adapter must not be null")
//        )
        setItemMargin(outRect, itemCount, itemPosition, layoutDirection)
    }

    /*@Orientation
    private fun findOrientation(layoutManager: RecyclerView.LayoutManager): Int {
        return when (layoutManager) {
            is LinearLayoutManager -> layoutManager.orientation // GridLM is child of Linear
            is StaggeredGridLayoutManager -> layoutManager.orientation
            else -> throw IllegalStateException("Only Linear, Grid and StaggeredGrid LayoutManagers are accepted")
        }
    }*/

    interface Decorator {
        fun isAtScrollingStart(itemPosition: Int, spanCount: Int): Boolean {
            return itemPosition < spanCount
        }

        fun setScrollingStartMargin(rect: Rect, itemPosition: Int, spanCount: Int, margin: Int)

        fun isAtScrollingEnd(itemPosition: Int, spanCount: Int, itemCount: Int): Boolean {
            return row(itemPosition, spanCount) == rows(itemCount, spanCount)
        }

        fun setScrollingEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        )

        fun isAtSideStart(itemPosition: Int, spanCount: Int): Boolean {
            return itemPosition % spanCount == 0
        }

        fun setSideStartMargin(rect: Rect, itemPosition: Int, spanCount: Int, margin: Int)

        fun isAtSideEnd(itemPosition: Int, spanCount: Int, itemCount: Int): Boolean {
            return itemCount == 1 || itemPosition % spanCount == (spanCount - 1)
        }

        fun setSideEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        )

        private fun row(position: Int, spanCount: Int): Int {
            return ceil((position + 1).toFloat() / spanCount.toFloat()).toInt()
        }

        private fun rows(itemCount: Int, spanCount: Int): Int {
            return ceil(itemCount.toFloat() / spanCount.toFloat()).toInt()
        }
    }

    abstract class VerticalDecorator : Decorator {
        override fun setScrollingStartMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            margin: Int
        ) {
            if (isAtScrollingStart(itemPosition, spanCount))
                rect.top = margin
        }

        override fun setScrollingEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        ) {
            if (isAtScrollingEnd(itemPosition, spanCount, itemCount))
                rect.bottom = margin
        }
    }

    class VerticalLTRDecorator : VerticalDecorator() {
        override fun setSideStartMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            margin: Int
        ) {
            if (isAtSideStart(itemPosition, spanCount))
                rect.left = margin
        }

        override fun setSideEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        ) {
            if (isAtSideEnd(itemPosition, spanCount, itemCount))
                rect.right = margin
        }
    }

    class VerticalRTLDecorator : VerticalDecorator() {
        override fun setSideStartMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            margin: Int
        ) {
            if (isAtSideStart(itemPosition, spanCount))
                rect.right = margin
        }

        override fun setSideEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        ) {
            if (isAtSideEnd(itemPosition, spanCount, itemCount))
                rect.left = margin
        }
    }

    abstract class HorizontalDecorator : Decorator {
        override fun setSideStartMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            margin: Int
        ) {
            if (isAtSideStart(itemPosition, spanCount))
                rect.top = margin
        }

        override fun setSideEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        ) {
            if (isAtSideEnd(itemPosition, spanCount, itemCount))
                rect.bottom = margin
        }
    }

    class HorizontalLTRDecorator : HorizontalDecorator() {
        override fun setScrollingStartMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            margin: Int
        ) {
            if (isAtScrollingStart(itemPosition, spanCount))
                rect.left = margin
        }

        override fun setScrollingEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        ) {
            if (isAtScrollingEnd(itemPosition, spanCount, itemCount))
                rect.right = margin
        }

    }

    class HorizontalRTLDecorator : HorizontalDecorator() {

        override fun setScrollingStartMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            margin: Int
        ) {
            if (isAtScrollingStart(itemPosition, spanCount))
                rect.right = margin
        }

        override fun setScrollingEndMargin(
            rect: Rect,
            itemPosition: Int,
            spanCount: Int,
            itemCount: Int,
            margin: Int
        ) {
            if (isAtScrollingEnd(itemPosition, spanCount, itemCount))
                rect.left = margin
        }
    }

    internal fun setItemMargin(
        outRect: Rect,
        itemPosition: Int,
        itemCount: Int,
        layoutDirection: Int
    ): Rect {
        require(itemPosition < itemCount) { "position must be less than item count" }
        val decorator: Decorator = findProperDecorator(
            layoutDirection = layoutDirection,
            orientation = orientation
        )
//        val isRTL = layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
//        val isAtStart = itemPosition % spanCount == 0
//        val isAtEnd = itemPosition % spanCount == (spanCount - 1)
//        val isAtTop = itemPosition < spanCount
//        val isAtBottom = row(itemPosition, spanCount) == rows(itemCount, spanCount)

        outRect.left = margin / 2
        outRect.right = margin / 2
        outRect.top = margin / 2
        outRect.bottom = margin / 2

        decorator.setScrollingStartMargin(outRect, itemPosition, spanCount, margin)
        decorator.setScrollingEndMargin(outRect, itemPosition, spanCount, itemCount, margin)
        decorator.setSideStartMargin(outRect, itemPosition, spanCount, margin)
        decorator.setSideEndMargin(outRect, itemPosition, spanCount, itemCount, margin)
//        if (decorator.isAtScrollingStart(itemPosition, ))
//            outRect.top = spacing
//
//        if (isAtBottom)
//            outRect.bottom = spacing
//
//        if (isAtStart)
//            if (isRTL) outRect.right = spacing
//            else outRect.left = spacing
//
//        if (isAtEnd)
//            if (isRTL) outRect.left = spacing
//            else outRect.right = spacing

        return outRect
    }

    // LTR = 0, RTL = 1, Horizontal = 0, Vertical = 1
    private val decorators = arrayOf(
        HorizontalLTRDecorator(),
        HorizontalRTLDecorator(),
        VerticalLTRDecorator(),
        VerticalRTLDecorator(),
    )

    private fun findProperDecorator(layoutDirection: Int, orientation: Int): Decorator {
        return decorators[layoutDirection + 2 * orientation]
    }

}