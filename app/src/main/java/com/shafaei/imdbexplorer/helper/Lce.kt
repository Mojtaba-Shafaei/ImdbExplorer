package com.shafaei.imdbexplorer.helper

/**
 * Lce means Loading, Content, and Error
 * this is a (wrapper) class to keep the state of view(activity,fragment,view,...)
 *
 * << Its better to have a generic class to keep the view state data rather than writing a View state data class for each View. >>
 *
 * @sample Lce.data means the state of the View has correct data, and its neither loading nor error
 */
data class Lce<T>(
  val firstLoading: Boolean = false,
  val loading: Boolean = false,
  val error: Throwable? = null,
  val data: T? = null
) {

  companion object {
    fun <T> firstLoading(): Lce<T> {
      return Lce<T>(firstLoading = true)
    }

    fun <T> loading(): Lce<T> {
      return Lce<T>(loading = true)
    }

    fun <T> failure(exception: Throwable): Lce<T> {
      return Lce<T>(error = exception)
    }

    fun <T> success(data: T): Lce<T> {
      return Lce<T>(data = data)
    }
  }

  fun isFailure(): Boolean {
    return error != null
  }
}