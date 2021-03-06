package com.michaldrabik.network.tvdb.model.json

data class TvdbImageJson(
  val id: Long?,
  val fileName: String?,
  val thumbnail: String?,
  val keyType: String?,
  val ratingsInfo: TvdbImageRatingJson?
)
