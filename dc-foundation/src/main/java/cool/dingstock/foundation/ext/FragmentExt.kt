package cool.dingstock.foundation.ext

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.getCompatColor(resId: Int): Int {
    return ContextCompat.getColor(requireContext(), resId)
}

