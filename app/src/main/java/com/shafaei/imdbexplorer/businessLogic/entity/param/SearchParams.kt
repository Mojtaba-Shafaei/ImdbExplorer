package com.shafaei.imdbexplorer.businessLogic.entity.param

import com.shafaei.imdbexplorer.businessLogic.entity.MovieType

// s	     Yes	<empty>	Movie title to search for.
// type	   No	  movie, series, episode	<empty>	Type of result to return.
// y	     No		<empty>	Year of release.

interface SearchParam

// empty class to next page params
class NextPageSearchParam : SearchParam
class RetrySearchParam : SearchParam

/**
 * This class used for creating parameters of [com.shafaei.imdbexplorer.businessLogic.network.MovieService.getMovie] as a Map<String,String>.
 *
 * @property movieTitle Movie title to search for. Map as "s"
 * @property type       Type of result to return. (movie, series, episode) Map as "type"
 * @property year       Year of release. Map as "y"
 */
data class SearchParams(val movieTitle: String, val type: MovieType? = null, val year: String? = null, var page: Int = 1) : SearchParam