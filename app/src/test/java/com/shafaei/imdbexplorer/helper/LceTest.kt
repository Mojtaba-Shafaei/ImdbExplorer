package com.shafaei.imdbexplorer.helper

import org.junit.Assert.*
import org.junit.Test

class LceTest {

  @Test
  fun isFailure_Failure_returnsExceptionAndNotLoading() {
    val lce = Lce.failure<String>(Exception("THIS IS ERROR TEXT"))
    assertTrue(lce.isFailure())
    assertFalse(lce.loading)
  }

  @Test
  fun isFailure_Success_ReturnsNoFailureAndNoLoading() {
    val lce = Lce.success("THIS IS DATA")
    assertFalse(lce.isFailure())
    assertFalse(lce.loading)
    assertEquals("THIS IS DATA", lce.data)
  }

  @Test
  fun getFirstLoading_FirstLoading_ReturnsFirstLoadingNoFailureNoDataNoLoading() {
    val lce = Lce.firstLoading<String>()
    assertFalse(lce.isFailure())
    assertFalse(lce.loading)
    assertNull(lce.data)
    assertTrue(lce.firstLoading)
  }

  @Test
  fun getLoading_Loading_NoFirstLoadingNoDataNoException() {
    val lce = Lce.loading<String>()
    assertFalse(lce.isFailure())
    assertNull(lce.data)
    assertFalse(lce.firstLoading)
    assertTrue(lce.loading)
  }
}