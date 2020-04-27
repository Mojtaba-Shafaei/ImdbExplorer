package com.shafaei.imdbexplorer.ui.main.movieInfo

import androidx.lifecycle.ViewModel
import com.shafaei.imdbexplorer.businessLogic.entity.Movie
import com.shafaei.imdbexplorer.businessLogic.entity.param.MovieParam
import com.shafaei.imdbexplorer.businessLogic.network.NetworkMovieBl
import com.shafaei.imdbexplorer.helper.Lce
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextLceFailure
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MovieInfoViewModel(private val movieBl: NetworkMovieBl) : ViewModel() {
  private val mDisposables = CompositeDisposable()
  private val mSubjectState: PublishSubject<Lce<Movie>> = PublishSubject.create()
  val states = mSubjectState.replay(1).autoConnect()
  //////////////////////////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////////////////////////
  fun loadMovieInfo(param: MovieParam) {
    mDisposables +=
      movieBl.getMovie(param)
        .toObservable()
        .observeOn(Schedulers.io())
        .map { result ->
          if (result.isSuccess) {
            Lce.success<Movie>(Movie.of(result.getOrNull()!!))
          } else {
            prepareLceException(result.exceptionOrNull()!!)
          }
        }
        .startWith(Lce.firstLoading())
        .onErrorResumeNextLceFailure()
        .subscribeOn(Schedulers.io())
        .subscribe { mSubjectState.onNext(it) }

  }

  private fun prepareLceException(throwable: Throwable): Lce<Movie> {
    return Lce.failure<Movie>(if (throwable is Exception) throwable else Exception(throwable))
  }

  override fun onCleared() {
    mDisposables.clear()
    super.onCleared()
  }
}