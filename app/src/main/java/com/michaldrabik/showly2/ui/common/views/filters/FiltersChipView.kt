package com.michaldrabik.showly2.ui.common.views.filters

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.michaldrabik.showly2.R

class FiltersChipView : Chip, CoordinatorLayout.AttachedBehavior {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  init {
    setChipBackgroundColorResource(R.color.colorAccent)
    chipIcon = ContextCompat.getDrawable(context, R.drawable.ic_filters)
    setChipIconTintResource(R.color.colorWhite)
    setText(R.string.textFilters)
    setChipStartPaddingResource(R.dimen.spaceMedium)
    setChipEndPaddingResource(R.dimen.spaceMedium)
  }

  override fun getBehavior() = FiltersViewBehaviour()
}