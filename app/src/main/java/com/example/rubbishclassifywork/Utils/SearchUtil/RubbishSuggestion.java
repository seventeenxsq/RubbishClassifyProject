package com.example.rubbishclassifywork.Utils.SearchUtil;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class RubbishSuggestion implements SearchSuggestion {
    private String mRubbishName;
    private boolean mIsHistory = false;

    public RubbishSuggestion(String suggestion) {
        this.mRubbishName = suggestion.toLowerCase();
    }

    public RubbishSuggestion(Parcel source) {
        this.mRubbishName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mRubbishName;
    }

    public static final Creator<RubbishSuggestion> CREATOR = new Creator<RubbishSuggestion>() {
        @Override
        public RubbishSuggestion createFromParcel(Parcel in) {
            return new RubbishSuggestion(in);
        }

        @Override
        public RubbishSuggestion[] newArray(int size) {
            return new RubbishSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRubbishName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
