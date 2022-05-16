package cool.dingstock.appbase.widget.photoselect;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import cool.dingstock.appbase.widget.photoselect.entity.PhotoBean;
import cool.dingstock.lib_base.util.CollectionUtils;


public class PhotoSelectResult {

    /**
     * 所有的相片
     */
    public static List<PhotoBean> allLocalMedia = new ArrayList<>();

    /**
     * 选择的相片
     */
    public static LinkedHashSet<PhotoBean> selectLocalMedia = new LinkedHashSet<>();

    /**
     * 当前界面展示的相片
     */
    public static List<PhotoBean> currentLocalMedia = new ArrayList<>();

    /**
     * 单个选择
     */
    public static PhotoBean selectLocalSingle;

    public static int currentMaxCount = 3;

    public static final int SPAN_COUNT = 4;

    public static void setCurrentMaxCount(int currentMaxCount) {
        PhotoSelectResult.currentMaxCount = currentMaxCount;
    }

    public static void setAllLocalMedia(List<PhotoBean> photoList) {
        if (CollectionUtils.isEmpty(photoList)) {
            return;
        }
        allLocalMedia = photoList;
        if (CollectionUtils.isEmpty(selectLocalMedia)) {
            selectLocalMedia.clear();
            return;
        }
        for (PhotoBean choosePhotoBean : selectLocalMedia) {
            for (PhotoBean photoBean : allLocalMedia) {
                if (photoBean.getId().equals(choosePhotoBean.getId())) {
                    photoBean.setChoose(true);
                    selectLocalMedia.add(photoBean);
                }
            }
        }
    }

    public static void setMediaChoose(String id, boolean isChoose) {
        for (PhotoBean photoBean : allLocalMedia) {
            if (id.equals(photoBean.getId())) {
                photoBean.setChoose(isChoose);
                if (isChoose) {
                    selectLocalMedia.add(photoBean);
                } else {
                    selectLocalMedia.remove(photoBean);
                }
                break;
            }
        }
    }


    public static boolean isMax() {
        return (selectLocalMedia.size() >= currentMaxCount && !isSingleChoose())
                || (selectLocalSingle != null && currentMaxCount == 1);
    }

    public static boolean isSingleChoose() {
        return currentMaxCount == 1;
    }

    public static void release() {
        if (currentMaxCount == 1) {
            selectLocalSingle = null;
        }
    }


}
