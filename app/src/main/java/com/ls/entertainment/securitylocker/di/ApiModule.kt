package com.ls.entertainment.securitylocker.di


import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
	private const val BASE_URL = "https://restcountries.eu/rest/v2/"

	@Singleton
	@Provides
	fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

	@Singleton
	@Provides
	fun provideRetrofit(): Retrofit {
		val dispatcher = Dispatcher()
		dispatcher.maxRequests = 1
		val builder: OkHttpClient.Builder = OkHttpClient.Builder().connectTimeout(
			30 /*Constants.CONNECT_TIMEOUT*/, TimeUnit.SECONDS
		).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
			.dispatcher(dispatcher)
		val gson = GsonBuilder().create()
		return Retrofit.Builder().baseUrl("https://www.google.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.addConverterFactory(GsonConverterFactory.create(gson))
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(builder.build())
			.build()
	}

	@Singleton
	@Provides
	fun provideApiService(retrofit: Retrofit): ApiInterface =
		retrofit.create(ApiInterface::class.java)

}