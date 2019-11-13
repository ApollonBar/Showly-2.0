package com.michaldrabik.showly2.ui.common.views.filters

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Note: some extra work is added because of an issue:
 * https://gist.github.com/erikhuizinga/edf408167b46eb5b1568424563ca4e59?ts=2
 */
class FiltersViewBehaviour : CoordinatorLayout.Behavior<View>() {

  override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
    return dependency is RecyclerView
  }

  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: View,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ) = when (axes) {
    ViewCompat.SCROLL_AXIS_VERTICAL -> true
    else -> super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
  }

  override fun onNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: View,
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
    consumed: IntArray
  ) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
    child.translationY = (child.translationY + dyConsumed.toFloat()).coerceAtLeast(0F)
  }
}
