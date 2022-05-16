package cool.dingstock.post.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import cool.dingstock.appbase.adapter.CommonSpanItemDecoration
import cool.dingstock.appbase.entity.bean.config.EmojiUrl
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.post.databinding.FragmentKusoEmojiBinding
import cool.dingstock.post.item.KusoEmojiItemBinder

class KosoEmojiFragment: BaseFragment() {
    companion object {
        private const val EMOJI_URL_LIST = "EMOJI_URL_LIST"

        fun getInstance(urlList: List<EmojiUrl>?, selectKusoEmoji: (EmojiUrl) -> Unit): KosoEmojiFragment {
            return KosoEmojiFragment().apply {
                this.selectKusoEmoji = selectKusoEmoji
                arguments = Bundle().apply {
                    putParcelableArrayList(EMOJI_URL_LIST, arrayListOf<EmojiUrl>().apply { urlList?.let { addAll(it) } })
                }
            }
        }
    }

    private lateinit var viewBinding: FragmentKusoEmojiBinding

    var selectKusoEmoji: ((EmojiUrl) -> Unit)? = null

    private val itemBinder by lazy {
        KusoEmojiItemBinder().apply {
            setOnItemClickListener { adapter, _, position ->
                selectKusoEmoji?.invoke(adapter.data[position] as EmojiUrl)
            }
        }
    }

    private val adapter by lazy {
        BaseBinderAdapter(arrayListOf()).apply {
            addItemBinder(EmojiUrl::class.java, itemBinder)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentKusoEmojiBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.kusoEmojiRv.apply {
            adapter = this@KosoEmojiFragment.adapter
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(CommonSpanItemDecoration(4, 10, 10, false))
        }
        arguments?.getParcelableArrayList<EmojiUrl>(EMOJI_URL_LIST)?.let {
            adapter.setList(it)
        }
    }
}