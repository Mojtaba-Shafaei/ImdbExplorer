package com.shafaei.imdbexplorer.businessLogic.exception

import com.shafaei.imdbexplorer.R

class MovieNotFoundException : BaseException(R.string.movieNotFoundException)

class TooManyResultException : BaseException(R.string.tooManyResultException)

class InternetException : BaseException(R.string.internetException)

class UnknownException : BaseException(R.string.unknownException)

