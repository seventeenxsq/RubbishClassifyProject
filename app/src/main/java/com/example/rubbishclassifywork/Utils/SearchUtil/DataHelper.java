package com.example.rubbishclassifywork.Utils.SearchUtil;

import android.content.Context;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHelper {

    //推荐的数据是在这里定义的
    //他那个选择是从本地的多个里面进行选择，而我展现的已经是选择好的结果了，不过问题不大，我修改这里就行了
    private static List<RubbishSuggestion> sRubbishSuggestions =
            new ArrayList<>(Arrays.asList(
                    new RubbishSuggestion("green"),
                    new RubbishSuggestion("blue"),
                    new RubbishSuggestion("pink"),
                    new RubbishSuggestion("purple"),
                    new RubbishSuggestion("brown"),
                    new RubbishSuggestion("gray"),
                    new RubbishSuggestion("Granny Smith Apple"),
                    new RubbishSuggestion("Indigo"),
                    new RubbishSuggestion("Periwinkle"),
                    new RubbishSuggestion("Mahogany"),
                    new RubbishSuggestion("Maize"),
                    new RubbishSuggestion("Mahogany"),
                    new RubbishSuggestion("Outer Space"),
                    new RubbishSuggestion("Melon"),
                    new RubbishSuggestion("Yellow"),
                    new RubbishSuggestion("Orange"),
                    new RubbishSuggestion("Red"),
                    new RubbishSuggestion("Orchid")));
    //在这里控制历史记录
    public static void resetSuggestionsHistory() {
        for (RubbishSuggestion rubbishSuggestion : sRubbishSuggestions) {
            rubbishSuggestion.setIsHistory(false);
        }
    }

    //修改本地的内容
    public static void setsRubbishSuggestions(List<RubbishSuggestion> Suggestions){
        sRubbishSuggestions = Suggestions;
    }


    public interface OnFindSuggestionsListener {
        void onResults(List<RubbishSuggestion> results);
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                //这里就是展示的结果了，我想要的是直接展示出去
                FilterResults results = new FilterResults();
                Collections.sort(sRubbishSuggestions, new Comparator<RubbishSuggestion>() {
                    @Override
                    public int compare(RubbishSuggestion lhs, RubbishSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = sRubbishSuggestions;
                results.count = sRubbishSuggestions.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<RubbishSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public static List<RubbishSuggestion> getHistory(Context context, int count) {

        List<RubbishSuggestion> suggestionList = new ArrayList<>();
        RubbishSuggestion rubbishSuggestion;
        for (int i = 0; i < sRubbishSuggestions.size(); i++) {
            rubbishSuggestion = sRubbishSuggestions.get(i);
            rubbishSuggestion.setIsHistory(true);
            suggestionList.add(rubbishSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }
}
