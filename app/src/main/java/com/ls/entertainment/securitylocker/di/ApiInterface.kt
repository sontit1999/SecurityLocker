package com.ls.entertainment.securitylocker.di

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiInterface {
	@GET
	@Streaming
	suspend fun downloadImageSuspend(
		@Url url: String
	): ResponseBody

}