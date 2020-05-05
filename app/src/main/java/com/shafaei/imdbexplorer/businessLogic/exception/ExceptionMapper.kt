package com.shafaei.imdbexplorer.businessLogic.exception

import android.accounts.NetworkErrorException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

object ExceptionMapper {
  /**
   * map a [Throwable] to [BaseException]
   */
  fun map(throwable: Throwable): BaseException {
    return when (throwable) {
      is SocketTimeoutException, is NetworkErrorException, is UnresolvedAddressException, is UnknownHostException -> InternetException()
      else -> {
        throwable.message?.let { map(it) } ?: UnknownException()
      }
    }
  }

  /**
   * map API`s errors to [BaseException]
   * these error description return from API
   */
  fun map(exceptionCod: String): BaseException {
    return when (exceptionCod) {
      "Movie not found!" -> MovieNotFoundException() // this error description returns from API
      "Too many results." -> TooManyResultException() //"Enter movie name, more precisely "
      "Something went wrong." -> UnknownException() // maybe user enters empty string("") as movie name
      else -> UnknownException()
    }
  }
}