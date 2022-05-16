package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.post.databinding.ItemEmojiBinding

class EmojiItemBinder: BaseViewBindingItemBinder<String, ItemEmojiBinding>() {
    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemEmojiBinding {
        return ItemEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemEmojiBinding, data: String) {
        vb.emojiTv.text = data
    }
}