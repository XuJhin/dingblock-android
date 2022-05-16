package cool.mobile.account.ui.country

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.account.CountryBean
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.mobile.account.R
import cool.mobile.account.country.CountryListAdapter
import cool.mobile.account.country.CountryListView
import cool.mobile.account.databinding.AccountActivityCountryPickerBinding
import cool.mobile.account.ui.login.fragment.mobile.AccountLoginFragment
import java.util.*
import java.util.regex.Pattern

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [AccountConstant.Path.COUNTRYCODE]
)
class CountryPickerActivity : VMBindingActivity<BaseViewModel, AccountActivityCountryPickerBinding>(), TextWatcher {
    private var lv: CountryListView? = null
    private var searchBox: EditText? = null
    private var searchString: String? = null
    private val searchLock = Any()
    var inSearchMode = false
    private var contactList: List<CountryBean>? = null
    private var filterList: MutableList<CountryBean>? = null
    private var adapter: CountryListAdapter? = null
    private var curSearchTask: SearchListTask? = null
    override fun afterTextChanged(s: Editable) {
        searchString = searchBox!!.text.toString().trim { it <= ' ' }.uppercase(Locale.getDefault())
        if (curSearchTask != null && curSearchTask!!.status != AsyncTask.Status.FINISHED) {
            try {
                curSearchTask!!.cancel(true)
            } catch (e: Exception) {
            }
        }
        curSearchTask = SearchListTask()
        curSearchTask!!.execute(searchString)
    }

    override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        filterList = ArrayList()
        contactList = AccountHelper.getInstance().countryList
        adapter = CountryListAdapter(
            this@CountryPickerActivity,
            R.layout.account_item_country,
            contactList
        )
        lv = findViewById<CountryListView?>(R.id.account_country_picker_lv).apply {
            isFastScrollEnabled = true
            adapter = this@CountryPickerActivity.adapter
            onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                    val searchList: List<CountryBean>? = if (inSearchMode) filterList else contactList
                    val countryViewBean = searchList?.get(position)
                    if (countryViewBean != null) {
                        val intent = Intent()
                        intent.putExtra(COUNTRY_CODE, countryViewBean.code)
                        setResult(AccountLoginFragment.ZONE_RESULT_OK, intent)
                    } else {
                        setResult(AccountLoginFragment.ZONE_RESULT_CANCEL)
                    }
                    finish()
                }
        }
        searchBox = findViewById<EditText?>(R.id.account_country_picker_search_edit).apply {
            addTextChangedListener(this@CountryPickerActivity)
        }
        adapter!!.updateData(contactList)
    }

    override fun initListeners() {}
    override fun moduleTag(): String {
        return ModuleConstant.LOGIN_MODULE
    }

    private inner class SearchListTask : AsyncTask<String?, Void?, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean {
            filterList!!.clear()
            val keyword = params[0]
            keyword?.let {
                inSearchMode = keyword.isNotEmpty()
                if (inSearchMode) {
                    for (item in contactList!!) {
                        if (item.name!!.uppercase(Locale.getDefault()).contains(keyword) ||
                            item.spell!!.uppercase(Locale.getDefault()).contains(keyword) ||
                            item.code!!.uppercase(Locale.getDefault()).contains(keyword)
                        ) {
                            filterList!!.add(item)
                        }
                    }
                }
                val mer = Pattern.compile("^[0-9]+$").matcher(keyword)
                return mer.find()
            }
            return false
        }

        override fun onPostExecute(result: Boolean) {
            synchronized(searchLock) {
                if (inSearchMode) {
                    val adapter = CountryListAdapter(
                        this@CountryPickerActivity,
                        R.layout.account_item_country, filterList
                    )
                    adapter.isInSearchMode = true
                    lv!!.isInSearchMode = true
                    lv!!.adapter = adapter
                    if (result) {
                        filterList?.sortWith { (_, code), (_, code1) ->
                            val num1 = code!!.toInt()
                            val num2 = code1!!.toInt()
                            num1 - num2
                        }
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val adapter = CountryListAdapter(
                        this@CountryPickerActivity,
                        R.layout.account_item_country, contactList
                    )
                    adapter.isInSearchMode = false
                    lv!!.isInSearchMode = false
                    lv!!.adapter = adapter
                }
            }
        }
    }

    companion object {
        const val COUNTRY_CODE = "country_code"
    }
}