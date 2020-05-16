package com.example.rubbishclassifywork.Utils.SearchUtil;

import android.content.Context;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DataHelper {

    //推荐的数据是在这里定义的
    //他那个选择是从本地的多个里面进行选择，而我展现的已经是选择好的结果了，不过问题不大，我修改这里就行了
    /*
        这里是初始化的历史搜索，也就是说这里保存的是历史搜索，原本的操作是将其标记为true就行了，但是因为我的数据是放在服务器上的，所以我的操作应该是将这个保存下来
        需要注意的是，每次要将原来的搜索历史删除掉，然后放到第一位上，就是在这里面的位置了
        具体操作：首先进行遍历，判断是否在搜索历史中，如果在，那么则进行删除操作，然后放到头部，这个应该用链表来实现，如果不在，则直接放到头部
        注意：最多只保存20个历史搜索（多了的话，遍历的时间会增加）
     */
    private static List<RubbishSuggestion> sRubbishSuggestions = new ArrayList<>();     //临时推荐搜索
    private static List<RubbishSuggestion> hRubbishSuggestions = new ArrayList<>();     //历史搜索

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
        for (int i = 0; i < hRubbishSuggestions.size(); i++) {
            rubbishSuggestion = hRubbishSuggestions.get(i);
            rubbishSuggestion.setIsHistory(true);
            suggestionList.add(rubbishSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    //点击之后，修改历史搜索
    public static void changeHistory(RubbishSuggestion suggestion){
        for(int i=0; i<hRubbishSuggestions.size();i++){
            if(hRubbishSuggestions.get(i).getBody().equals(suggestion.getBody())){
                hRubbishSuggestions.remove(i);
            }
        }
        hRubbishSuggestions.add(0,suggestion);
    }
}
