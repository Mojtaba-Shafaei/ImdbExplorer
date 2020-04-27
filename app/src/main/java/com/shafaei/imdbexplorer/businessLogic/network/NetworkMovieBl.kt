package com.shafaei.imdbexplorer.businessLogic.network

import android.util.Log
import com.shafaei.imdbexplorer.businessLogic.dto.NetworkMovie
import com.shafaei.imdbexplorer.businessLogic.dto.NetworkSearch
import com.shafaei.imdbexplorer.businessLogic.entity.param.SearchParams
import com.shafaei.imdbexplorer.businessLogic.entity.param.MovieParam
import com.shafaei.imdbexplorer.businessLogic.util.getErrors
import com.shafaei.imdbexplorer.businessLogic.util.hasError
import com.shafaei.imdbexplorer.businessLogic.network.service.MovieService
import com.shafaei.imdbexplorer.businessLogic.network.util.RetrofitHelper
import io.reactivex.Single

class NetworkMovieBl {
  private val mMovieService by lazy { RetrofitHelper.retrofit.create(MovieService::class.java) }

  fun search(params: SearchParams): Single<kotlin.Result<List<NetworkSearch>>> {
    return Single.defer {
      mMovieService.search(
        search = params.movieTitle,
        type = params.type?.toString(),
        year = params.year,
        page = params.page.toString()
      )
        .flatMap { retrofitResult ->
          if (hasError(retrofitResult)) {
            val exception = getErrors(retrofitResult)
            Single.just(Result.failure<List<NetworkSearch>>(exception))
          } else {
            retrofitResult.response()?.body()!!.run {
              if (this.response.not()) {
                if (this.error == "Movie not found!") {
                  Single.just(Result.success<List<NetworkSearch>>(emptyList()))
                } else
                  Single.just(Result.failure<List<NetworkSearch>>(Exception(this.error)))
              } else
                Single.just(Result.success<List<NetworkSearch>>(this.movies))
            }
          }
        }
        .onErrorReturn { t: Throwable ->
          Log.e("NetworkBl", "SHOW a suitable ERROR to user, end user must not see develop side error.", t)
          kotlin.Result.failure<List<NetworkSearch>>(Exception("An Error Happened, RETRY again"))
        }
    }
  }

  fun getMovie(params: MovieParam): Single<kotlin.Result<NetworkMovie>> {
    return Single.defer {
      mMovieService.getMovie(id = params.id, plot = params.plot.toString())
        .flatMap { retrofitResult ->
          if (hasError(retrofitResult)) {
            val exception = getErrors(retrofitResult)
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
        .onErrorReturn { t: Throwable ->
          Log.e("NetworkBl", "SHOW a suitable ERROR to user, end user must not see develop side error.", t)
          kotlin.Result.failure<NetworkMovie>(Exception("An Error Happened, RETRY again"))
        }
    }
  }
}