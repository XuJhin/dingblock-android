package cool.dingstock.calendar.dagger



/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object CalendarApiHelper {
    val apiHomeComponent: CalendarComponent by lazy {
        DaggerCalendarComponent.create()
    }
}