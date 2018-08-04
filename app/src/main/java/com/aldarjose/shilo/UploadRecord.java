package com.aldarjose.shilo;

public class UploadRecord {

    private String mTag;
    private String mImageID;
    private String mImageUrl;

        public UploadRecord(){}
        public UploadRecord(String problemType, String imageUploadID, String imageUrl) {

            mTag = problemType;
            mImageID = imageUploadID;
            mImageUrl = imageUrl;

        }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getImageID() {
        return mImageID;
    }

    public void setImageID(String imageID) {
        mImageID = imageID;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
