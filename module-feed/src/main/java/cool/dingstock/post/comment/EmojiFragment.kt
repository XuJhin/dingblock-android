package cool.dingstock.post.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.google.gson.Gson
import cool.dingstock.appbase.entity.bean.circle.EmojiBean
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.post.databinding.FragmentEmojiBinding
import cool.dingstock.post.item.EmojiItemBinder
import java.io.BufferedReader
import java.io.InputStreamReader

class EmojiFragment: BaseFragment() {
    companion object {
        fun getInstance(selectEmoji: (String) -> Unit): EmojiFragment {
            return EmojiFragment().apply {
                this.selectEmoji = selectEmoji
            }
        }
    }

    private lateinit var viewBinding: FragmentEmojiBinding

    var selectEmoji: ((String) -> Unit)? = null

    private val itemBinder by lazy {
        EmojiItemBinder().apply {
            setOnItemClickListener { adapter, _, position ->
                selectEmoji?.invoke(adapter.data[position] as String)
            }
        }
    }

    private val adapter by lazy {
        BaseBinderAdapter(arrayListOf()).apply {
            addItemBinder(String::class.java, itemBinder)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentEmojiBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.emojiRv.apply {
            adapter = this@EmojiFragment.adapter
            layoutManager = GridLayoutManager(context, 8)
        }
        getData()?.let {
            adapter.setList(it.list.map { str ->
                val ten = str.toInt(16)
                String(Character.toChars(ten))
            })
        }
    }

    private fun getData(): EmojiBean? {
        val sb = StringBuilder()
        return try {
            context?.assets?.let {
                BufferedReader(InputStreamReader(it.open("emoji.json"))).useLines { strs ->
                    strs.forEach { str ->
                        sb.append(str)
                    }
                }
            }
            Gson().fromJson(sb.toString(), EmojiBean::class.java)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}