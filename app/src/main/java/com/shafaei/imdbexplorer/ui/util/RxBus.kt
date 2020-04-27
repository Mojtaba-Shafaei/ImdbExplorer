package com.shafaei.imdbexplorer.ui.util

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import java.util.*

object RxBus {
  private val mSubjectEvents: MutableMap<EventEnum, PublishSubject<Any>> = EnumMap(EventEnum::class.java)
  private val mDisposablesMap: MutableMap<LifeCyclesEnum, CompositeDisposable> = EnumMap(LifeCyclesEnum::class.java)

  private fun mergeSubject(event: EventEnum): PublishSubject<Any> {
    var s = mSubjectEvents[event]
    if (s == null) {
      s = PublishSubject.create()
      mSubjectEvents[event] = s
      return s
    }
    return s
  }

  private fun mergeCompositeDisposable(lifeCycle: LifeCyclesEnum): CompositeDisposable {
    var s = mDisposablesMap[lifeCycle]
    if (s == null) {
      s = CompositeDisposable()
      mDisposablesMap[lifeCycle] = s
      return s
    }
    return s
  }

  fun subscribe(event: EventEnum, lifeCycle: LifeCyclesEnum, action: (data: Any) -> Unit) {
    mergeCompositeDisposable(lifeCycle) +=
      mergeSubject(event).subscribe(action)
  }

  fun unSubscribe(lifeCycle: LifeCyclesEnum) {
    mDisposablesMap[lifeCycle]?.clear()
    mDisposablesMap.remove(lifeCycle)
  }

  fun publish(event: EventEnum, message: Any) {
    mergeSubject(event).onNext(message)
  }
}

enum class EventEnum {
  MOVIE_ITEM_CLICK,
  MOVIE_INFO_BACK_CLICK, // its a bridge action between fragments
  SHOW_MOVIE_INFO
}

enum class LifeCyclesEnum {
  MAIN_ACTIVITY,
  FRAGMENT_MOVIE_INFO
}