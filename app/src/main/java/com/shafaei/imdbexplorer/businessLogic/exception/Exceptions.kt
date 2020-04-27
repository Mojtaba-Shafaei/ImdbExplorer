package com.shafaei.imdbexplorer.businessLogic.exception

/**
 * @property networkException the exception that returns from server
 * @property description description that would displayed to user
 */
sealed class MyException(private val networkException: String, private val description: String)

class NotFoundException() : MyException("Movie not found!", "NOT FOUND.")

