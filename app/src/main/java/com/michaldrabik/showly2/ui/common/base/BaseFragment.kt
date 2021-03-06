package com.michaldrabik.showly2.ui.common.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.michaldrabik.showly2.di.DaggerViewModelFactory
import com.michaldrabik.showly2.ui.common.UiModel
import com.michaldrabik.showly2.ui.main.MainActivity
import com.michaldrabik.showly2.utilities.extensions.showErrorSnackbar
import com.michaldrabik.showly2.utilities.extensions.showInfoSnackbar
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

abstract class BaseFragment<T : BaseViewModel<out UiModel>>(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

  @Inject lateinit var viewModelFactory: DaggerViewModelFactory
  protected abstract val viewModel: T

  protected fun hideNavigation(animate: Boolean = true) =
    mainActivity().hideNavigation(animate)

  protected fun showNavigation(animate: Boolean = true) =
    mainActivity().showNavigation(animate)

  protected fun showInfoSnackbar(@StringRes messageResId: Int) =
    getSnackbarHost().showInfoSnackbar(getString(messageResId))

  protected fun showErrorSnackbar(@StringRes errorResId: Int) =
    getSnackbarHost().showErrorSnackbar(getString(errorResId))

  protected open fun getSnackbarHost(): ViewGroup = mainActivity().snackBarHost

  protected fun mainActivity() = requireActivity() as MainActivity

  protected fun navigateTo(@IdRes destination: Int, bundle: Bundle? = null) =
    findNavController().navigate(destination, bundle)
}
