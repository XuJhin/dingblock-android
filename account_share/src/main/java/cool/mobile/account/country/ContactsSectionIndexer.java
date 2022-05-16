package cool.mobile.account.country;

import android.widget.SectionIndexer;

import java.util.Arrays;
import java.util.List;

import cool.dingstock.appbase.entity.bean.account.CountryBean;

public class ContactsSectionIndexer implements SectionIndexer {

    private static String OTHER = "#";
    private static String[] mSections = {OTHER, "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};

    private static int OTHER_INDEX = 0;

    private int[] mPositions;

    private int mCount;

    public ContactsSectionIndexer(List<CountryBean> contacts) {
        mCount = contacts.size();
        initPositions(contacts);
    }


    public String getSectionTitle(String spell) {
        int sectionIndex = getSectionIndex(spell);
        return mSections[sectionIndex];
    }

    public int getSectionIndex(String index) {
        if (index == null) {
            return OTHER_INDEX;
        }

        index = index.trim();
        String firstLetter = OTHER;

        if (index.length() == 0) {
            return OTHER_INDEX;
        } else {
            firstLetter = String.valueOf(index.charAt(0)).toUpperCase();
        }

        int sectionCount = mSections.length;
        for (int i = 0; i < sectionCount; i++) {
            if (mSections[i].equals(firstLetter)) {
                return i;
            }
        }

        return OTHER_INDEX;

    }

    public void initPositions(List<CountryBean> contacts) {
        int sectionCount = mSections.length;
        mPositions = new int[sectionCount];
        Arrays.fill(mPositions, -1);
        int itemIndex = 0;
        for (CountryBean contact : contacts) {
            String spell = contact.getSpell();
            int sectionIndex = getSectionIndex(spell);
            if (mPositions[sectionIndex] == -1)
                mPositions[sectionIndex] = itemIndex;
            itemIndex++;
        }
        int lastPos = -1;
        for (int i = 0; i < sectionCount; i++) {
            if (mPositions[i] == -1)
                mPositions[i] = lastPos;
            lastPos = mPositions[i];
        }
    }


    @Override
    public int getPositionForSection(int section) {
        if (section < 0 || section >= mSections.length) {
            return -1;
        }
        return mPositions[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position < 0 || position >= mCount) {
            return -1;
        }
        int index = Arrays.binarySearch(mPositions, position);
        return index >= 0 ? index : -index - 2;
    }

    @Override
    public Object[] getSections() {
        return mSections;
    }

    public boolean isFirstItemInSection(int position) {
        int section = Arrays.binarySearch(mPositions, position);
        return (section > -1);
    }

}
