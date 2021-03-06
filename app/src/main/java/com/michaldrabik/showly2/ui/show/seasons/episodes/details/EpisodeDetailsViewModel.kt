package com.michaldrabik.showly2.ui.show.seasons.episodes.details

import androidx.lifecycle.viewModelScope
import com.michaldrabik.network.Cloud
import com.michaldrabik.showly2.common.images.EpisodeImagesProvider
import com.michaldrabik.showly2.model.Episode
import com.michaldrabik.showly2.model.IdTrakt
import com.michaldrabik.showly2.model.IdTvdb
import com.michaldrabik.showly2.model.Ids
import com.michaldrabik.showly2.ui.common.base.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EpisodeDetailsViewModel @Inject constructor(
  private val imagesProvider: EpisodeImagesProvider,
  private val cloud: Cloud
) : BaseViewModel<EpisodeDetailsUiModel>() {

  fun loadImage(tvdb: IdTvdb) {
    viewModelScope.launch {
      try {
        uiState = EpisodeDetailsUiModel(imageLoading = true)
        val ids = Ids.EMPTY.copy(tvdb = tvdb)
        val episode = Episode.EMPTY.copy(ids = ids)
        val episodeImage = imagesProvider.loadRemoteImage(episode)
        uiState = EpisodeDetailsUiModel(image = episodeImage, imageLoading = false)
      } catch (t: Throwable) {
        uiState = EpisodeDetailsUiModel(imageLoading = false)
      }
    }
  }

  fun loadComments(idTrakt: IdTrakt, season: Int, episode: Int) {
    viewModelScope.launch {
      try {
        uiState = EpisodeDetailsUiModel(commentsLoading = true)
        val comments = cloud.traktApi.fetchEpisodeComments(idTrakt.id, season, episode)
          .filter { it.parentId <= 0 }
          .sortedByDescending { it.id }
        uiState = EpisodeDetailsUiModel(comments = comments, commentsLoading = false)
      } catch (t: Throwable) {
        Timber.w("Failed to load comments. ${t.message}")
        uiState = EpisodeDetailsUiModel(commentsLoading = false)
      }
    }
  }
}
