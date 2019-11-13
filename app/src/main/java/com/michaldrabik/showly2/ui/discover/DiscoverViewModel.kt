package com.michaldrabik.showly2.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.michaldrabik.showly2.Config
import com.michaldrabik.showly2.model.Image
import com.michaldrabik.showly2.model.ImageType.FANART
import com.michaldrabik.showly2.model.ImageType.FANART_WIDE
import com.michaldrabik.showly2.model.ImageType.POSTER
import com.michaldrabik.showly2.model.Show
import com.michaldrabik.showly2.ui.UiCache
import com.michaldrabik.showly2.ui.common.base.BaseViewModel
import com.michaldrabik.showly2.ui.discover.recycler.DiscoverListItem
import com.michaldrabik.showly2.utilities.extensions.nowUtcMillis
import com.michaldrabik.showly2.utilities.extensions.replaceItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
  private val interactor: DiscoverInteractor,
  private val uiCache: UiCache
) : BaseViewModel<DiscoverUiModel>() {

  private val _showsStream = MutableLiveData<List<DiscoverListItem>>()
  val showsStream: LiveData<List<DiscoverListItem>> = _showsStream

  private var lastPullToRefreshMs = 0L

  fun loadDiscoverShows(
    resetScroll: Boolean = false,
    skipCache: Boolean = false,
    pullToRefresh: Boolean = false
  ) {
    if (pullToRefresh && nowUtcMillis() - lastPullToRefreshMs < Config.PULL_TO_REFRESH_COOLDOWN_MS) {
      _uiStream.value = DiscoverUiModel(showLoading = false)
      return
    }
    _uiStream.value = DiscoverUiModel(applyUiCache = uiCache)
    viewModelScope.launch {
      val progressJob = launch {
        delay(if (pullToRefresh) 0 else 750)
        _uiStream.value = DiscoverUiModel(showLoading = true)
      }
      try {
        val shows = interactor.loadDiscoverShows(skipCache)
        val myShowsIds = interactor.loadMyShowsIds()
        onShowsLoaded(shows, myShowsIds)
        _uiStream.value = DiscoverUiModel(resetScroll = resetScroll)
        if (pullToRefresh) lastPullToRefreshMs = nowUtcMillis()
      } catch (t: Throwable) {
        onError(Error(t))
      } finally {
        _uiStream.value = DiscoverUiModel(showLoading = false)
        progressJob.cancel()
      }
    }
  }

  private suspend fun onShowsLoaded(
    shows: List<Show>,
    followedShowsIds: List<Long>
  ) {
    val items = shows.mapIndexed { index, show ->
      val itemType =
        when (index) {
          in (0..500 step 14) -> FANART_WIDE
          in (5..500 step 14), in (9..500 step 14) -> FANART
          else -> POSTER
        }
      val image = interactor.findCachedImage(show, itemType)
      DiscoverListItem(show, image, isFollowed = show.ids.trakt.id in followedShowsIds)
    }
    _showsStream.value = items
  }

  fun loadMissingImage(item: DiscoverListItem, force: Boolean) {

    fun updateShowsItem(new: DiscoverListItem) {
      val currentItems = _showsStream.value?.toMutableList()
      currentItems?.let { items ->
        items.find { it.show.ids.trakt == new.show.ids.trakt }?.let {
          items.replaceItem(it, new)
        }
      }
      _showsStream.value = currentItems
    }

    viewModelScope.launch {
      updateShowsItem(item.copy(isLoading = true))
      try {
        val image = interactor.loadMissingImage(item.show, item.image.type, force)
        updateShowsItem(item.copy(isLoading = false, image = image))
      } catch (t: Throwable) {
        updateShowsItem(item.copy(isLoading = false, image = Image.createUnavailable(item.image.type)))
      }
    }
  }

  fun saveUiPositions(searchPosition: Float, filtersPosition: Float) {
    uiCache.discoverSearchPosition = searchPosition
    uiCache.discoverFiltersPosition = filtersPosition
  }

  fun clearCache() = uiCache.clear()

  private fun onError(error: Error) {
    _uiStream.value = DiscoverUiModel(error = error)
  }
}