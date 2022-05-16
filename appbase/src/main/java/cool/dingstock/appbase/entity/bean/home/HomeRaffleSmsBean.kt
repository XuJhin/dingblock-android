package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize

import java.util.ArrayList;






@Parcelize
data class HomeRaffleSmsBean (
    var shopPhoneNum  : String?,
    var shopName  : String?,
    var sections  : List<HomeRaffleSection>?


) : Parcelable
