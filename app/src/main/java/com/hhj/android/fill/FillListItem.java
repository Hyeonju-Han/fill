package com.hhj.android.fill;

/* Han Hyeonju */

public class FillListItem {

    /*Data array*/
    private String[] mData;

    /* Item ID */
    private String mId;

    /* True if this item is selectable */
    private boolean mSelectable = true;

    /* Initialize with icon and data array */
    public FillListItem(String itemId, String[] obj) {
        mId = itemId;
        mData = obj;
    }

    /* Initialize with strings */
    public FillListItem(String memoId, String fillDate, String fillText,
                        String id_photo, String uri_photo) {
        mId = memoId;
        mData = new String[4];
        mData[0] = fillDate;
        mData[1] = fillText;
        mData[2] = id_photo;
        mData[3] = uri_photo;
    }

    /* True if this item is selectable */
    public boolean isSelectable() {
        return mSelectable;
    }

    /* Set selectable flag */
    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    public String getId() {
        return mId;
    }

    public void setId(String itemId) {
        mId = itemId;
    }


    /* Get data array */
    public String[] getData() {
        return mData;
    }

    /*Get data*/
    public String getData(int index) {
        if (mData == null || index >= mData.length) {
            return null;
        }

        return mData[index];
    }

    /*Set data array*/
    public void setData(String[] obj) {
        mData = obj;
    }


    /*Compare with the input object*/
    public int compareTo(FillListItem other) {
        if (mData != null) {
            Object[] otherData = other.getData();
            if (mData.length == otherData.length) {
                for (int i = 0; i < mData.length; i++) {
                    if (!mData[i].equals(otherData[i])) {
                        return -1;
                    }
                }
            } else {
                return -1;
            }
        } else {
            throw new IllegalArgumentException();
        }

        return 0;
    }

}
