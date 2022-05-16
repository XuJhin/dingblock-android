package cool.dingstock.monitor.dagger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object MonitorApiHelper {
	val apiMonitorComponent: MonitorComponent by lazy {
		DaggerMonitorComponent.create()
	}
}