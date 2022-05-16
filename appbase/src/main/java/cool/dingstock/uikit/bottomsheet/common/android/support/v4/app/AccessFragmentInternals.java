package cool.dingstock.uikit.bottomsheet.common.android.support.v4.app;

import androidx.fragment.app.Fragment;

public final class AccessFragmentInternals {
    private AccessFragmentInternals() {
        throw new AssertionError("No instances.");
    }

    public static int getContainerId(Fragment fragment) {
        return fragment.getId();
    }
}
