package com.example.rubbishclassifywork.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.example.rubbishclassifywork.R;
import com.example.rubbishclassifywork.Utils.SearchUtil.DataHelper;
import com.example.rubbishclassifywork.Utils.SearchUtil.RubbishSuggestion;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    private final String TAG = "BlankFragment";
    private static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private FloatingSearchView mSearchView;
    private boolean mIsDarkSearchTheme = false;
    private String mLastQuery = "";
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView = view.findViewById(R.id.floating_search_view0);
        setupSearchBar();
    }

    private void setupSearchBar() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            //显示搜索建议
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    Log.d(TAG, "onSearchTextChanged: ddd"+newQuery);
                    //这个newQuery就是获取输入的结果
                    //在这里利用newQuery从服务器获取数据
                    List<RubbishSuggestion> newSuggestions =
                            new ArrayList<>(Arrays.asList(
                                    new RubbishSuggestion("ddd"),
                                    new RubbishSuggestion("aadd"),
                                    new RubbishSuggestion("dccd"),
                                    new RubbishSuggestion("aaa"),
                                    new RubbishSuggestion("bbb"),
                                    new RubbishSuggestion("ccc"),
                                    new RubbishSuggestion("eeee"),
                                    new RubbishSuggestion("qqq"),
                                    new RubbishSuggestion("qqwwq"),
                                    new RubbishSuggestion("qqqbbb"),
                                    new RubbishSuggestion("qqcccq"),
                                    new RubbishSuggestion("qwqwq"),
                                    new RubbishSuggestion("wwww")
                            ));
                    DataHelper.setsRubbishSuggestions(newSuggestions);
                    //在这里进行调用，然后将结果放到了results里面，再把results显示出去
                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<RubbishSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            //点击之后在这里
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                RubbishSuggestion rubbishSuggestion = (RubbishSuggestion) searchSuggestion;
                DataHelper.changeHistory(rubbishSuggestion);
//                Log.d(TAG, "onSuggestionClicked: dddd " + rubbishSuggestion.getBody());
                //在这里获取选择的结果之后，再上传到服务器查询是什么类型的垃圾
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

//                DataHelper.findColors(getActivity(), query,
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<ColorWrapper> results) {
//                                //show search results
//                            }
//
//                        });
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                //显示搜索历史，所以修改getHistory就行
                mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 5));

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                RubbishSuggestion rubbishSuggestion = (RubbishSuggestion) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";

//                if (rubbishSuggestion.getIsHistory()) {
//                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
//                            R.drawable.ic_history_black_24dp, null));
//
//                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
//                    leftIcon.setAlpha(.36f);
//                } else {
//                    leftIcon.setAlpha(0.0f);
//                    leftIcon.setImageDrawable(null);
//                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = rubbishSuggestion.getBody()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSearchView.setTranslationY(verticalOffset);
    }
}
