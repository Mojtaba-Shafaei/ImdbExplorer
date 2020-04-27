package com.shafaei.imdbexplorer.businessLogic.util

import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> hasError(result: retrofit2.adapter.rxjava2.Result<T>): Boolean {
  return (result.isError or (result.response()?.isSuccessful == false))
}

fun <T> getErrors(result: retrofit2.adapter.rxjava2.Result<T>): Exception {
  return when {
    result.isError -> {
      return when (result.error()) {
        is UnknownHostException, is SocketTimeoutException -> Exception("NO INTERNET CONNECTION!. PLEASE CHECK NETWORK")
        else -> {
          Exception(result.error())
        }
      }
    }
    else -> {
      Exception(result.error())
    }
  }
}
