package com.shafaei.imdbexplorer.businessLogic.util

object RetrofitUtil {
  fun <T> hasError(result: retrofit2.adapter.rxjava2.Result<T>): Boolean {
    return (result.isError or (result.response()?.isSuccessful == false))
  }

  fun <T> getErrors(result: retrofit2.adapter.rxjava2.Result<T>): Throwable {
    return when {
      result.isError -> result.error()!!
      else -> {
        Throwable(result.response()!!.errorBody()!!.string())
      }
    }
  }
}