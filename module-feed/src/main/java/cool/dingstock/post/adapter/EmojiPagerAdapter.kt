package cool.dingstock.post.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cool.dingstock.appbase.entity.bean.config.EmojiConfig
import cool.dingstock.appbase.entity.bean.config.EmojiUrl
import cool.dingstock.post.comment.EmojiFragment
import cool.dingstock.post.comment.KosoEmojiFragment

class EmojiPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val list: List<EmojiConfig>,
    private val selectEmoji: (String) -> Unit,
    private val selectKusoEmoji: (EmojiUrl) -> Unit,
): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return if (list[position].type == "emoji") {
            EmojiFragment.getInstance(selectEmoji)
        } else {
            KosoEmojiFragment.getInstance(list[position].urlList, selectKusoEmoji)
        }
    }
}