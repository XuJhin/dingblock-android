package cool.dingstock.imagepicker.data;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import java.util.ArrayList;
import java.util.Set;

import cool.dingstock.imagepicker.bean.ImageSet;
import cool.dingstock.imagepicker.bean.MimeType;


public class MediaItemsLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStoreConstants._ID,
            MediaStoreConstants.DATA,
            MediaStoreConstants.DISPLAY_NAME,
            MediaStoreConstants.WIDTH,
            MediaStoreConstants.HEIGHT,
            MediaStoreConstants.MIME_TYPE,
            MediaStoreConstants.SIZE,
            MediaStoreConstants.DURATION,
            MediaStoreConstants.DATE_MODIFIED};

    private static final String ORDER_BY = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

    private MediaItemsLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    static CursorLoader newInstance(Context context, ImageSet album, Set<MimeType> mimeTypeSet) {
        String[] selectionsArgs;
        String albumSelections = "";
        String mimeSelections = "";
        int index = 0;
        ArrayList<String> arrayList = MimeType.getMimeTypeList(mimeTypeSet);
        if (album.isAllMedia() || album.isAllVideo()) {
            selectionsArgs = new String[arrayList.size()];
        } else {
            selectionsArgs = new String[arrayList.size() + 1];
            selectionsArgs[0] = album.id;
            index = 1;
            albumSelections = " bucket_id=? AND ";
        }

        for (String mimeType : arrayList) {
            selectionsArgs[index] = mimeType;
            mimeSelections = String.format("%s =? OR %s", MediaStore.Files.FileColumns.MIME_TYPE, mimeSelections);
            index++;
        }

        if (mimeSelections.endsWith(" OR ")) {
            mimeSelections = mimeSelections.substring(0, mimeSelections.length() - 4);
        }

        String selections = albumSelections + "(" + MediaStoreConstants.MEDIA_TYPE + "=" + MediaStoreConstants.MEDIA_TYPE_IMAGE + " OR " +
                MediaStoreConstants.MEDIA_TYPE + "=" + MediaStoreConstants.MEDIA_TYPE_VIDEO + ")"
                + " AND " + MediaStoreConstants.SIZE + ">0" + " AND (" + mimeSelections + ")";

        return new MediaItemsLoader(context, selections, selectionsArgs);
    }

    @Override
    public void onContentChanged() {
    }
}
