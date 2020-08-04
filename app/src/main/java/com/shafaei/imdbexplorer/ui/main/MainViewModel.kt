package com.shafaei.imdbexplorer.ui.main

import androidx.lifecycle.ViewModel
import com.shafaei.imdbexplorer.businessLogic.entity.Movie
import com.shafaei.imdbexplorer.businessLogic.entity.MovieType
import com.shafaei.imdbexplorer.businessLogic.entity.Search
import com.shafaei.imdbexplorer.businessLogic.entity.param.*
import com.shafaei.imdbexplorer.businessLogic.network.NetworkMovieBl
import com.shafaei.imdbexplorer.helper.Lce
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextLceFailure
import com.shafaei.imdbexplorer.ui.main.search.SearchUiData
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class MainViewModel(private val networkMovieBl: NetworkMovieBl) : ViewModel() {
  private val mDisposables = CompositeDisposable()

  /////////////////////////  SEARCH FRAGMENT ///////////////////////////////////////////////////////
  // observe search emmit commands. for search should pass the parameters to this subject
  private val mSubjectSearch: PublishSubject<SearchParam> = PublishSubject.create()
  private val mPageIndicator = AtomicInteger(1)

  private val mSubjectSearchViewState: BehaviorSubject<Lce<SearchUiData>> = BehaviorSubject.create()

  // A Flowable to keep the last state of View and observe the changing of state
  val searchStates: Flowable<Lce<SearchUiData>> =
    mSubjectSearchViewState
      .toFlowable(LATEST) // if the stream is too fast and the View can NOT render new changes, previous states will be forbidden, and just the last state keep and show to user
      .distinctUntilChanged() // Wont emit new state until a difference there be in new state compare to the previous one

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // cache and keeps pair of IMDB_ID and Search
  private val mCache = HashMap<String, Search>()

  var mFirstItemInView: Int? = null // keeps ID of the first Search item, this ID will use to scroll to previous state

  /////////////////////////  INFO FRAGMENT /////////////////////////////////////////////////////////
  private val mSubjectInfoState: PublishSubject<Lce<Movie>> = PublishSubject.create()
  val infoStates = mSubjectInfoState.replay(1).autoConnect()

  //////////////////////////////////////////////////////////////////////////////////////////////////
  init {
    mDisposables +=
      mSubjectSearch
        .distinctUntilChanged()
        .scan { old: SearchParam, new: SearchParam ->
          when (new) {
            is SearchParams -> new
            is RetrySearchParam -> (old as SearchParams)
            else -> {
              (old as SearchParams).page = mPageIndicator.get() + 1
              old
            }
          }
        }
        .map { param -> param as SearchParams }
        .doOnNext { if (it.page == 1) mCache.clear() } // clear cache when request the first page of search
        .toFlowable(LATEST)
        .observeOn(Schedulers.io()) // observe the downstream on io()
        .flatMap { params ->
          networkMovieBl.search(params).toFlowable()
            .map { result ->
              if (result.isSuccess) {
                mPageIndicator.incrementAndGet() // on-success increase page indicator,
                result.getOrNull()!!.let { networkResultList ->
                  val searchList = networkResultList.map { Search.of(it) }
                  cache(searchList)
                  Lce.success<SearchUiData>(data = SearchUiData(searchParams = params, searchList = searchList))
                }
              } else
                Lce.failure<SearchUiData>(result.exceptionOrNull()!!)
            }.onErrorResumeNextLceFailure()
            .startWith(Lce.loading<SearchUiData>())
        }
        .onErrorResumeNextLceFailure() // this line is required to keep stream alive on errors
        .subscribeOn(Schedulers.io())
        .subscribe(
          { mSubjectSearchViewState.onNext(it) },
          { mSubjectSearchViewState.onNext(Lce.failure(it)) }
        )
  }

  private fun cache(networkSearchList: List<Search>) {
    mCache.putAll(networkSearchList.map { it.imdbId to it })
  }

  fun loadMovieInfo(param: MovieParam) {
    mDisposables +=
      networkMovieBl.getMovie(param)
        .toObservable()
        .observeOn(Schedulers.io())
        .map { result ->
          if (result.isSuccess) {
            Lce.success<Movie>(Movie.of(result.getOrNull()!!))
          } else {
            Lce.failure(result.exceptionOrNull()!!)
          }
        }
        .startWith(Lce.firstLoading())
        .onErrorResumeNextLceFailure()
        .subscribeOn(Schedulers.io())
        .subscribe { mSubjectInfoState.onNext(it) }

  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * @param text, the text, that should be there in movie's title
   *
   */
  fun search(text: String, year: String? = null, type: MovieType? = null) {
    mPageIndicator.set(1)
    mSubjectSearch.onNext(SearchParams(movieTitle = text, type = type, year = year, page = 1))
  }

  fun retryToSearch() {
    mSubjectSearch.onNext(RetrySearchParam())
  }

  fun searchNextPage() {
    mSubjectSearch.onNext(NextPageSearchParam())
  }

  override fun onCleared() {
    mDisposables.clear()
    super.onCleared()
  }
}
