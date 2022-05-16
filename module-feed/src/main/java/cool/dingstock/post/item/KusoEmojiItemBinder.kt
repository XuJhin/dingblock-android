package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.config.EmojiUrl
import cool.dingstock.appbase.ext.load
import cool.dingstock.post.databinding.ItemKusoEmojiBinding

class KusoEmojiItemBinder: BaseViewBindingItemBinder<EmojiUrl, ItemKusoEmojiBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemKusoEmojiBinding {
        return ItemKusoEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemKusoEmojiBinding, data: EmojiUrl) {
        vb.emojiIv.load(data.imageUrl)
    }
}