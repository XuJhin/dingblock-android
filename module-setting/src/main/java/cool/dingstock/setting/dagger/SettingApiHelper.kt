package cool.dingstock.setting.dagger

object SettingApiHelper {
	val apiSettingComponent: SettingComponent by lazy {
		DaggerSettingComponent.create()
	}
}