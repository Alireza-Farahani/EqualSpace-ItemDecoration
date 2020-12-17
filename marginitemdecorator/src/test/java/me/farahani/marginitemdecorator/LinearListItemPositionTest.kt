package me.farahani.marginitemdecorator

import com.google.common.truth.Truth
import me.farahani.marginitemdecorator.MarginItemDecorator.LinearListItemPositionImpl
import me.farahani.marginitemdecorator.MarginItemDecorator.ListItemParams
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class IsAtScrollingStart(private val params: ListItemParams, private val result: Boolean) {
    private lateinit var positionFinder: LinearListItemPositionImpl

    @Before
    fun setUp() {
        positionFinder = LinearListItemPositionImpl()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(ListItemParams(0, 1, 1), true),
            arrayOf(ListItemParams(0, 1, 2), true),
            arrayOf(ListItemParams(1, 1, 2), false),

            arrayOf(ListItemParams(0, 3, 3), true),
            arrayOf(ListItemParams(2, 3, 3), true),
            arrayOf(ListItemParams(0, 3, 4), true),
            arrayOf(ListItemParams(3, 3, 4), false),
        )
    }

    @Test
    fun `test isAtScrollingStart works`() {
        Truth.assertThat(positionFinder.isAtScrollingStart(params)).isEqualTo(result)
    }
}

// ------------------

@RunWith(Parameterized::class)
internal class IsAtScrollingEnd(private val params: ListItemParams, private val result: Boolean) {
    private lateinit var positionFinder: LinearListItemPositionImpl

    @Before
    fun setUp() {
        positionFinder = LinearListItemPositionImpl()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(ListItemParams(0, 1, 1), true),
            arrayOf(ListItemParams(0, 1, 2), false),
            arrayOf(ListItemParams(1, 1, 2), true),

            arrayOf(ListItemParams(0, 3, 3), true),
            arrayOf(ListItemParams(2, 3, 3), true),
            arrayOf(ListItemParams(0, 3, 4), false),
            arrayOf(ListItemParams(3, 3, 4), true),
            arrayOf(ListItemParams(7, 4, 11), false),
            arrayOf(ListItemParams(9, 4, 11), true),
        )
    }

    @Test
    fun `test isAtScrollingEnd works`() {
        Truth.assertThat(positionFinder.isAtScrollingEnd(params)).isEqualTo(result)
    }
}

// -------------

@RunWith(Parameterized::class)
internal class IsAtSideStart(private val params: ListItemParams, private val result: Boolean) {
    private lateinit var positionFinder: LinearListItemPositionImpl

    @Before
    fun setUp() {
        positionFinder = LinearListItemPositionImpl()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(ListItemParams(0, 1, 1), true),
            arrayOf(ListItemParams(0, 1, 2), true),
            arrayOf(ListItemParams(1, 1, 2), true),

            arrayOf(ListItemParams(0, 3, 1), true),
            arrayOf(ListItemParams(0, 3, 3), true),
            arrayOf(ListItemParams(2, 3, 3), false),
            arrayOf(ListItemParams(0, 3, 4), true),
            arrayOf(ListItemParams(3, 3, 4), true),
        )
    }

    @Test
    fun `test isAtSideStart works`() {
        Truth.assertThat(positionFinder.isAtSideStart(params)).isEqualTo(result)
    }
}

// ------------------

@RunWith(Parameterized::class)
internal class IsAtSideEnd(private val params: ListItemParams, private val result: Boolean) {
    private lateinit var positionFinder: LinearListItemPositionImpl

    @Before
    fun setUp() {
        positionFinder = LinearListItemPositionImpl()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(ListItemParams(0, 1, 1), true),
            arrayOf(ListItemParams(0, 1, 2), true),
            arrayOf(ListItemParams(1, 1, 2), true),

            arrayOf(ListItemParams(1, 3, 1), true),
            arrayOf(ListItemParams(0, 3, 3), false),
            arrayOf(ListItemParams(2, 3, 3), true),
            arrayOf(ListItemParams(0, 3, 4), false),
            arrayOf(ListItemParams(3, 3, 4), false),
            arrayOf(ListItemParams(4, 3, 5), false),
            arrayOf(ListItemParams(5, 3, 6), true),
        )
    }

    @Test
    fun `test isAtSideEnd works`() {
        Truth.assertThat(positionFinder.isAtSideEnd(params)).isEqualTo(result)
    }
}