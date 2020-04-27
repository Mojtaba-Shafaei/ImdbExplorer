package com.shafaei.imdbexplorer.ui.kotlinExt

import com.shafaei.imdbexplorer.helper.Lce
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS

fun <T> Observable<T>.onErrorResumeNextData(action: () -> T): Observable<T> {
  return this.compose { upstream ->
    upstream.onErrorResumeNext { _: Throwable ->
      Observable.just(action.invoke())
    }
  }
}

fun <T> Observable<Lce<T>>.onErrorResumeNextLceFailure(action: (() -> Lce<T>)? = null): Observable<Lce<T>> {
  return this.compose { upstream ->
    upstream.onErrorResumeNext { t: Throwable ->
      Observable.just(
        action?.invoke() ?: Lce.failure<T>(if (t is Exception) t else Exception(t))
      )
    }
  }
}

fun <T> Flowable<Lce<T>>.onErrorResumeNextLceFailure(): Flowable<Lce<T>> {
  return this.compose { upstream ->
    upstream.onErrorResumeNext { t: Throwable -> Flowable.just(Lce.failure<T>(if (t is Exception) t else Exception(t))) }
  }
}

fun <T> Observable<T>.composeClicks(): Observable<T> {
  return this.compose { upstream -> upstream.throttleFirst(1500, MILLISECONDS, Schedulers.computation()) }
}