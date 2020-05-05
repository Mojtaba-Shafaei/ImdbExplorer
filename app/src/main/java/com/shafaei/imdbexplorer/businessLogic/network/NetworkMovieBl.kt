package com.shafaei.imdbexplorer.businessLogic.network

import com.shafaei.imdbexplorer.businessLogic.dto.NetworkMovie
import com.shafaei.imdbexplorer.businessLogic.dto.NetworkSearch
import com.shafaei.imdbexplorer.businessLogic.entity.param.MovieParam
import com.shafaei.imdbexplorer.businessLogic.entity.param.SearchParams
import com.shafaei.imdbexplorer.businessLogic.network.service.MovieService
import com.shafaei.imdbexplorer.businessLogic.network.util.RetrofitHelper
import com.shafaei.imdbexplorer.businessLogic.util.RetrofitUtil
import io.reactivex.Single

class NetworkMovieBl(retrofitHelper: RetrofitHelper) {
  private val mMovieService by lazy { retrofitHelper.retrofit.create(MovieService::class.java) }

  fun search(params: SearchParams): Single<kotlin.Result<List<NetworkSearch>>> {
    return Single.defer {
      mMovieService.search(
        search = params.movieTitle,
        type = params.type?.toString(),
        year = params.year,
        page = params.page.toString()
      )
        .flatMap { retrofitResult ->
          if (RetrofitUtil.hasError(retrofitResult)) {
            val exception = RetrofitUtil.getErrors(retrofitResult)
            Single.just(Result.failure<List<NetworkSearch>>(exception))
          } else {
            retrofitResult.response()!!.body()!!.run {
              if (this.response.not()) {
                Single.just(Result.failure<List<NetworkSearch>>(Throwable(this.error!!)))
              } else
                Single.just(Result.success<List<NetworkSearch>>(this.movies))
            }
          }
        }
        .onErrorReturn { t: Throwable -> Result.failure<List<NetworkSearch>>(t) }
    }
  }

  fun getMovie(params: MovieParam): Single<kotlin.Result<NetworkMovie>> {
    return Single.defer {
      mMovieService.getMovie(id = params.id, plot = params.plot.toString())
        .flatMap { retrofitResult ->
          if (RetrofitUtil.hasError(retrofitResult)) {
            val exception = RetrofitUtil.getErrors(retrofitResult)
            Single.just(Result.failure<NetworkMovie>(exception))
          } else {
            retrofitResult.response()?.body()!!.run {
              if (this.response) {
                Single.just(Result.success<NetworkMovie>(this))
              } else {
                Single.just(Result.failure<NetworkMovie>(Exception(this.error)))
              }
            }
          }
        }
        .onErrorReturn { t: Throwable -> Result.failure<NetworkMovie>(t) }
    }
  }
}