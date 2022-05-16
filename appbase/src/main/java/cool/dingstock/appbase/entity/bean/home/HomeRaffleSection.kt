package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize

import java.util.ArrayList;






@Parcelize
data class HomeRaffleSection (
    var fields  : List<HomeField>?,
    var header  : String?

) : Parcelable
