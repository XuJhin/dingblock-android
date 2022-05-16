package cool.dingstock.post.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleImageBean
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.lib_base.util.ScreenUtils
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.R
import java.lang.Exception

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/20  16:29
 */
class PostImgView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    private val itemView: View = LayoutInflater.from(context).inflate(R.layout.post_img_view_layout, this, true)
    private val moreImageLayout: ViewGroup = itemView.findViewById(R.id.circle_item_dynamic_image_container)
    private val singleImageView: RoundImageView = itemView.findViewById(R.id.circle_item_dynamic_iv)
    private val moreImages: ArrayList<ArrayList<ImageView>> = ArrayList()

    var onPostImgViewClickListener: OnPostImgViewClickListener? = null
    var onImageItemClickListener: OnImageItemClickListener? = null

    //距离屏幕的距离，用来计算图片的宽度
    private var marginStartScreen = 27
    private var marginEndScreen = 27
    private var imageSpace = 3

    // 高/宽
    private var goldenRatio = 0.495

    init {
        val imgListLine1 = arrayListOf(itemView.findViewById(R.id.post_img_11), itemView.findViewById(R.id.post_img_12), itemView.findViewById<ImageView>(R.id.post_img_13))
        val imgListLine2 = arrayListOf(itemView.findViewById(R.id.post_img_21), itemView.findViewById(R.id.post_img_22), itemView.findViewById<ImageView>(R.id.post_img_23))
        val imgListLine3 = arrayListOf(itemView.findViewById(R.id.post_img_31), itemView.findViewById(R.id.post_img_32), itemView.findViewById<ImageView>(R.id.post_img_33))
        moreImages.add(imgListLine1)
        moreImages.add(imgListLine2)
        moreImages.add(imgListLine3)
        for (al in moreImages) {
            for (iv in al) {
                iv.setOnClickListener {
                    val tag = it.getTag(R.id.iv_entity)
                    val position = it.getTag(R.id.iv_position) as? Int
                    onImageItemClickListener?.onImgClick(tag, position ?: 0)
                }
            }
        }
    }


    /**
     * 设置图片
     */
    fun setImage(item: CircleDynamicBean?) {
        try {
            var images = item?.images
            if (images.isNullOrEmpty()) {
                itemView.visibility = View.GONE
                return
            }
            moreImageLayout.setOnClickListener {
                onPostImgViewClickListener?.onItemClick()
            }
            itemView.visibility = View.VISIBLE
            if (images.size > 9) {
                images = images.subList(0, 9)
            }
            singleImageView.visibility = View.GONE
            moreImageLayout.visibility = View.GONE
            if (images.size == 1) {
                singleImageView.visibility = View.VISIBLE
            } else {
                moreImageLayout.visibility = View.VISIBLE
                //先隐藏所有
                hideAllLine()
            }
            when (images.size) {
                1 -> {
                    singleImg(images)
                    return
                }
                2 -> {
                    towImg(images)
                    return
                }
                3 -> {
                    threeImg(images)
                    return
                }
                4 -> {
                    fourImg(images)
                    return
                }
                5 -> {
                    fiveImg(images)
                    return
                }
                6 -> {
                    sixImg(images)
                    return
                }
                7 -> {
                    sevenImg(images)
                    return
                }
                8 -> {
                    eightImg(images)
                    return
                }
                9 -> {
                    nineImg(images)
                    return
                }
            }
        } catch (e: Exception) {
            itemView.hide(true)
        }
    }

    private fun singleImg(images: MutableList<CircleImageBean>) {
        singleImageView.visibility = View.VISIBLE

        val circleImageBean: CircleImageBean = images[0]
        val width = circleImageBean.width
        val height = circleImageBean.height
        val imageIvLayoutParams: ViewGroup.LayoutParams = singleImageView.layoutParams
        if (width <= 0 || height <= 0) {
            imageIvLayoutParams.width = SizeUtils.dp2px(220f)
            imageIvLayoutParams.height = SizeUtils.dp2px(220f)
        } else if (width > height) {
            imageIvLayoutParams.width = SizeUtils.dp2px(190f)
            imageIvLayoutParams.height = (SizeUtils.dp2px(190f) * height.toFloat() / width).toInt()
        } else {
            imageIvLayoutParams.width = (SizeUtils.dp2px(190f) * width.toFloat() / height).toInt()
            imageIvLayoutParams.height = SizeUtils.dp2px(190f)
        }

        val cutWidth = imageIvLayoutParams.width * 1f
        val cutHeight = imageIvLayoutParams.height * 1f
        singleImageView.layoutParams = imageIvLayoutParams
        val linkImage = circleImageBean.url
        if (null == linkImage || linkImage.isEmpty()) {
            singleImageView.visibility = View.GONE
        } else {
            GlideHelper.loadRadiusImageWithCutDrawable(circleImageBean.thumbnail, singleImageView, itemView, 4f, cutWidth, cutHeight)
        }
        singleImageView.setOnClickListener {
            onImageItemClickListener?.onImgClick(circleImageBean, 0)
        }
    }

    private fun towImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[0][1].showAndLoad(images, 1)
        setImgSquare(moreImages[0][0], 2)
        setImgSquare(moreImages[0][1], 2)
    }

    private fun threeImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[0][1].showAndLoad(images, 1)
        moreImages[0][2].showAndLoad(images, 2)
        setImgSquare(moreImages[0][0], 3)
        setImgSquare(moreImages[0][1], 3)
        setImgSquare(moreImages[0][2], 3)
    }

    private fun fourImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[0][1].showAndLoad(images, 1)
        moreImages[1][0].showAndLoad(images, 2)
        moreImages[1][1].showAndLoad(images, 3)
        setImgSquare(moreImages[0][0], 2)
        setImgSquare(moreImages[0][1], 2)
        setImgSquare(moreImages[1][0], 2)
        setImgSquare(moreImages[1][1], 2)
    }

    private fun fiveImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[0][1].showAndLoad(images, 1)
        moreImages[1][0].showAndLoad(images, 2)
        moreImages[1][1].showAndLoad(images, 3)
        moreImages[1][2].showAndLoad(images, 4)
        setImgSquare(moreImages[0][0], 2)
        setImgSquare(moreImages[0][1], 2)
        setImgSquare(moreImages[1][0], 3)
        setImgSquare(moreImages[1][1], 3)
        setImgSquare(moreImages[1][2], 3)
    }

    private fun sixImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[0][1].showAndLoad(images, 1)
        moreImages[0][2].showAndLoad(images, 2)
        moreImages[1][0].showAndLoad(images, 3)
        moreImages[1][1].showAndLoad(images, 4)
        moreImages[1][2].showAndLoad(images, 5)
        setImgSquare(moreImages[0][0], 3)
        setImgSquare(moreImages[0][1], 3)
        setImgSquare(moreImages[0][2], 3)
        setImgSquare(moreImages[1][0], 3)
        setImgSquare(moreImages[1][1], 3)
        setImgSquare(moreImages[1][2], 3)
    }

    private fun sevenImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[1][0].showAndLoad(images, 1)
        moreImages[1][1].showAndLoad(images, 2)
        moreImages[1][2].showAndLoad(images, 3)
        moreImages[2][0].showAndLoad(images, 4)
        moreImages[2][1].showAndLoad(images, 5)
        moreImages[2][2].showAndLoad(images, 6)
        setImgGoldenRatio(moreImages[0][0])
        setImgSquare(moreImages[1][0], 3)
        setImgSquare(moreImages[1][1], 3)
        setImgSquare(moreImages[1][2], 3)
        setImgSquare(moreImages[2][0], 3)
        setImgSquare(moreImages[2][1], 3)
        setImgSquare(moreImages[2][2], 3)
    }

    private fun eightImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[0][1].showAndLoad(images, 1)
        moreImages[1][0].showAndLoad(images, 2)
        moreImages[1][1].showAndLoad(images, 3)
        moreImages[1][2].showAndLoad(images, 4)
        moreImages[2][0].showAndLoad(images, 5)
        moreImages[2][1].showAndLoad(images, 6)
        moreImages[2][2].showAndLoad(images, 7)
        setImgSquare(moreImages[0][0], 2)
        setImgSquare(moreImages[0][1], 2)
        setImgSquare(moreImages[1][0], 3)
        setImgSquare(moreImages[1][1], 3)
        setImgSquare(moreImages[1][2], 3)
        setImgSquare(moreImages[2][0], 3)
        setImgSquare(moreImages[2][1], 3)
        setImgSquare(moreImages[2][2], 3)
    }

    private fun nineImg(images: MutableList<CircleImageBean>) {
        moreImages[0][0].showAndLoad(images, 0)
        moreImages[0][1].showAndLoad(images, 1)
        moreImages[0][2].showAndLoad(images, 2)
        moreImages[1][0].showAndLoad(images, 3)
        moreImages[1][1].showAndLoad(images, 4)
        moreImages[1][2].showAndLoad(images, 5)
        moreImages[2][0].showAndLoad(images, 6)
        moreImages[2][1].showAndLoad(images, 7)
        moreImages[2][2].showAndLoad(images, 8)
        setImgSquare(moreImages[0][0], 3)
        setImgSquare(moreImages[0][1], 3)
        setImgSquare(moreImages[0][2], 3)
        setImgSquare(moreImages[1][0], 3)
        setImgSquare(moreImages[1][1], 3)
        setImgSquare(moreImages[1][2], 3)
        setImgSquare(moreImages[2][0], 3)
        setImgSquare(moreImages[2][1], 3)
        setImgSquare(moreImages[2][2], 3)
    }


    /**
     * 设置图片设置为正方形
     *
     *  @param iv 图片控件
     * @param rowSize 一行多少个
     *
     *
     * */
    private fun setImgSquare(iv: ImageView, rowSize: Int) {
        val screenWidth = ScreenUtils.getScreenWidth(context)
        //图片一共可以有多宽
        val imgTotalWidth = screenWidth - (marginEndScreen + marginStartScreen + ((rowSize - 1) * imageSpace)).dp
        val imgWidth = imgTotalWidth / rowSize
        val layoutParams = iv.layoutParams
        layoutParams.height = imgWidth.toInt()
        iv.layoutParams = layoutParams
    }

    /**
     * 设置图片设置为黄金比例的宽高比
     *  @param iv 图片控件
     * */
    private fun setImgGoldenRatio(iv: ImageView) {
        val screenWidth = ScreenUtils.getScreenWidth(context)
        //图片一共可以有多宽
        val imgWidth = screenWidth - (marginEndScreen + marginStartScreen).dp
        val layoutParams = iv.layoutParams
        layoutParams.height = (imgWidth * goldenRatio).toInt()
        iv.layoutParams = layoutParams
    }

    private fun hideLine(arrayList: ArrayList<ImageView>) {
        for (iv in arrayList) {
            iv.hide(true)
        }
    }

    private fun showLine(arrayList: ArrayList<ImageView>) {
        for (iv in arrayList) {
            iv.hide(false)
        }
    }

    private fun hideAllLine() {
        for (al in moreImages) {
            for (iv in al) {
                iv.hide(true)
            }
        }
    }


    interface OnPostImgViewClickListener {
        fun onItemClick()
    }

    interface OnImageItemClickListener {
        fun onImgClick(entity: Any, position: Int)
    }


}

fun ImageView.showAndLoad(images: MutableList<CircleImageBean>, position: Int) {
    try {
        val entity = images[position]
        hide(false)
        setTag(R.id.iv_entity, entity)
        setTag(R.id.iv_position, position)
        load(entity.url)
    } catch (e: Exception) {
    }
}



