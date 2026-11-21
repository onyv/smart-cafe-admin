package com.example.mycafeadminap.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OrderStatusTest {

    @Test
    fun ordered_movesToPreparing() {
        assertEquals(OrderStatus.Preparing, OrderStatus.next(OrderStatus.Ordered))
    }

    @Test
    fun preparing_movesToCompleted() {
        assertEquals(OrderStatus.Completed, OrderStatus.next(OrderStatus.Preparing))
    }

    @Test
    fun completed_hasNoNextStatus() {
        assertNull(OrderStatus.next(OrderStatus.Completed))
    }

    @Test
    fun unknownStatus_hasNoNextStatus() {
        assertNull(OrderStatus.next("Cancelled"))
    }
}
