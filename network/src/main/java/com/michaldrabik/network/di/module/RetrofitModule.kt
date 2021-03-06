package com.michaldrabik.network.di.module

import com.michaldrabik.network.BuildConfig
import com.michaldrabik.network.Config.TRAKT_BASE_URL
import com.michaldrabik.network.Config.TVDB_BASE_URL
import com.michaldrabik.network.di.CloudScope
import com.michaldrabik.network.trakt.TraktInterceptor
import com.michaldrabik.network.trakt.converters.CommentConverter
import com.michaldrabik.network.trakt.converters.EpisodeConverter
import com.michaldrabik.network.trakt.converters.SearchResultConverter
import com.michaldrabik.network.trakt.converters.SeasonConverter
import com.michaldrabik.network.trakt.converters.ShowConverter
import com.michaldrabik.network.trakt.converters.SyncProgressItemConverter
import com.michaldrabik.network.trakt.converters.TrendingResultConverter
import com.michaldrabik.network.trakt.converters.UserConverter
import com.michaldrabik.network.tvdb.converters.TvdbActorConverter
import com.michaldrabik.network.tvdb.converters.TvdbImageConverter
import com.michaldrabik.network.tvdb.converters.TvdbResultConverter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Named

@Module
object RetrofitModule {

  private const val TIMEOUT_DURATION = 30L

  @Provides
  @CloudScope
  @Named("retrofitTrakt")
  fun providesTraktRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
    Retrofit.Builder()
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .baseUrl(TRAKT_BASE_URL)
      .build()

  @Provides
  @CloudScope
  @Named("retrofitTvdb")
  fun providesTvdbRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
    Retrofit.Builder()
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .baseUrl(TVDB_BASE_URL)
      .build()

  @Provides
  @CloudScope
  fun providesMoshi(
    showConverter: ShowConverter,
    trendingResultConverter: TrendingResultConverter,
    tvdbImageConverter: TvdbImageConverter,
    actorConverter: TvdbActorConverter,
    episodeConverter: EpisodeConverter,
    userConverter: UserConverter,
    seasonConverter: SeasonConverter,
    syncProgressItemConverter: SyncProgressItemConverter,
    searchResultConverter: SearchResultConverter,
    commentConverter: CommentConverter
  ): Moshi {
    return Moshi.Builder()
      .add(showConverter)
      .add(actorConverter)
      .add(userConverter)
      .add(seasonConverter)
      .add(episodeConverter)
      .add(commentConverter)
      .add(tvdbImageConverter)
      .add(searchResultConverter)
      .add(trendingResultConverter)
      .add(syncProgressItemConverter)
      .add(TvdbResultConverter(actorConverter))
      .add(TvdbResultConverter(tvdbImageConverter))
      .build()
  }

  @Provides
  @CloudScope
  fun providesOkHttp(
    httpLoggingInterceptor: HttpLoggingInterceptor,
    traktInterceptor: TraktInterceptor
  ): OkHttpClient =
    OkHttpClient.Builder()
      .writeTimeout(TIMEOUT_DURATION, SECONDS)
      .readTimeout(TIMEOUT_DURATION, SECONDS)
      .callTimeout(TIMEOUT_DURATION, SECONDS)
      .addInterceptor(httpLoggingInterceptor)
      .addInterceptor(traktInterceptor)
      .build()

  @Provides
  @CloudScope
  fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
      level = when {
        BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
        else -> HttpLoggingInterceptor.Level.NONE
      }
    }
}
