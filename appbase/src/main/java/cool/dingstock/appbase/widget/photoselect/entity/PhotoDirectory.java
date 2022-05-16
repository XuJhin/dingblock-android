package cool.dingstock.appbase.widget.photoselect.entity;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cool.dingstock.lib_base.util.FileUtils;

public class PhotoDirectory {

    private String id;
    private String coverPath;
    private String name;
    private long dateAdded;
    private List<PhotoBean> photoList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PhotoDirectory)) {
            return false;
        }

        PhotoDirectory directory = (PhotoDirectory) o;

        boolean hasId = !TextUtils.isEmpty(id);
        boolean otherHasId = !TextUtils.isEmpty(directory.id);

        if (hasId && otherHasId) {
            if (!TextUtils.equals(id, directory.id)) {
                return false;
            }

            return TextUtils.equals(name, directory.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(id)) {
            if (TextUtils.isEmpty(name)) {
                return 0;
            }

            return name.hashCode();
        }

        int result = id.hashCode();

        if (TextUtils.isEmpty(name)) {
            return result;
        }

        result = 31 * result + name.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<PhotoBean> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<PhotoBean> photoList) {
        if (photoList == null) {
            return;
        }
        for (int i = 0, j = 0, num = photoList.size(); i < num; i++) {
            PhotoBean p = photoList.get(j);
            if (p == null || !FileUtils.fileIsExists(p.getPath())) {
                photoList.remove(j);
            } else {
                j++;
            }
        }
        this.photoList = photoList;
    }


    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(photoList.size());
        for (PhotoBean photoBean : photoList) {
            paths.add(photoBean.getPath());
        }
        return paths;
    }

    public void addPhoto(PhotoBean photoBean) {
        if (null != photoBean) {
            photoList.add(photoBean);
        }
    }

    @Override
    public String toString() {
        return "PhotoDirectory{" +
                "coverPath='" + coverPath + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dateAdded=" + dateAdded +
                ", photoList=" + photoList +
                '}';
    }
}
