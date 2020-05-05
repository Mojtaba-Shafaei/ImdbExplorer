package com.shafaei.imdbexplorer.businessLogic.exception

import android.content.Context
import androidx.annotation.StringRes

open class BaseException(@StringRes val errorResId: Int, private val nextException: BaseException? = null) : Throwable() {

  fun getDescription(context: Context): String {
    return if (nextException == null) {
      context.getString(errorResId)
    } else {
      nextException.getDescription(context) + "\n" + context.getString(errorResId)
    }
  }

  fun getLastException(): BaseException {
    return if (nextException == null) this else getLastException()
  }

  private fun append(e: BaseException): BaseException {
    return getLastException().copy(nextException = e)
  }

  fun copy(nextException: BaseException? = null): BaseException {
    return BaseException(errorResId = this.errorResId, nextException = nextException)
  }

  private fun collectToList(list: MutableList<BaseException>, e: BaseException) {
    list += e
    nextException?.let { collectToList(list, it) }
  }

  fun toList(): List<BaseException> {
    val list: ArrayList<BaseException> = ArrayList()
    collectToList(list, this)
    return list
  }

  fun toBuilder(): Builder {
    return Builder.builder()
      .append(this)
  }

  ////////////////////////////  BUILDER //////////////////////////////////////////////////////////////////////
  class Builder {

    private val errors: MutableList<BaseException> = ArrayList()
    fun append(exception: BaseException): Builder {
      errors.add(exception)
      return this
    }

    fun isEmpty(): Boolean {
      return errors.isEmpty()
    }

    fun build(): BaseException? {
      if (errors.isEmpty()) return null

      var exception: BaseException? = null
      errors.forEachIndexed { index, baseException ->
        exception = if (index == 0) baseException else exception!!.copy(nextException = baseException)
      }
      return exception
    }

    companion object {
      fun builder(): Builder {
        return Builder()
      }
    }
  }
}