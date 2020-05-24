package com.example.rubbishclassifywork.Fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.example.rubbishclassifywork.HelperClass.HttpUtil;
import com.example.rubbishclassifywork.R;
import com.example.rubbishclassifywork.Utils.SearchUtil.DataHelper;
import com.example.rubbishclassifywork.Utils.SearchUtil.RubbishSuggestion;
import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchFgment extends Fragment implements AppBarLayout.OnOffsetChangedListener,View.OnClickListener{
    private static boolean clickFlag = false;   //用来设置在点击之后就不再推荐
    private final String TAG = "BlankFragment";
    private static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private FloatingSearchView mSearchView;
    private static RelativeLayout rubf,rubd,ToRightCar,ToLeftCar,eyeBall,baseChar;
    private static ImageView rubImg,blueCan,redCan,yellowCan,greenCan;
    private static TextView rubName,rubKind,explains,suggesions;
    private static Button backButton;
    private boolean mIsDarkSearchTheme = false;
    private boolean isHandleBack = false;
    private String mLastQuery = "";
    public SearchFgment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView = view.findViewById(R.id.floating_search_view0);
        setupSearchBar();
        initView();
        initAnimator();
    }

    private void initView(){

        rubf = getView().findViewById(R.id.rubf);
        rubd = getView().findViewById(R.id.rubd);
        ToRightCar = getView().findViewById(R.id.ToRightCar);
        ToLeftCar = getView().findViewById(R.id.ToLeftCar);
        rubImg = getView().findViewById(R.id.rubImg);
        eyeBall = getView().findViewById(R.id.eyeball);
        rubName = getView().findViewById(R.id.rubName);
        rubKind = getView().findViewById(R.id.rubKind);
        explains = getView().findViewById(R.id.explains);
        suggesions = getView().findViewById(R.id.suggests);
        blueCan = getView().findViewById(R.id.blue_can);
        redCan = getView().findViewById(R.id.red_can);
        greenCan = getView().findViewById(R.id.green_can);
        yellowCan = getView().findViewById(R.id.yellow_can);
        baseChar = getView().findViewById(R.id.baseChar);
        backButton = getView().findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
    }

    private void setupSearchBar() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            //显示搜索建议
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                }
                else if(clickFlag){
                    clickFlag = false;
                }
                else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    //这个newQuery就是获取输入的结果
                    //在这里利用newQuery从服务器获取数据
                    List<RubbishSuggestion> newSuggestions = QurySuggestion(newQuery);

                    DataHelper.setsRubbishSuggestions(newSuggestions);
                    //在这里进行调用，然后将结果放到了results里面，再把results显示出去
                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, results -> {

                                //this will swap the data and
                                //render the collapse/expand animations as necessary
                                mSearchView.swapSuggestions(results);

                                //let the users know that the background
                                //process has completed
                                mSearchView.hideProgress();
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            //点击之后在这里
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                showResult((RubbishSuggestion) searchSuggestion);
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                List<RubbishSuggestion> newSuggestions = QurySuggestion(query);
                if(newSuggestions.size()==0){
                    showNormalDialog();
                }
                else if(newSuggestions.size()==1){
                    Log.d(TAG, "onSearchAction: kkkk size = 1");
                    showResult((RubbishSuggestion) newSuggestions.get(0));
                }
                else {
                    DataHelper.setsRubbishSuggestions(newSuggestions);
                    //在这里进行调用，然后将结果放到了results里面，再把results显示出去
                    DataHelper.findSuggestions(getActivity(), query, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, results -> {

                                //this will swap the data and
                                //render the collapse/expand animations as necessary
                                mSearchView.swapSuggestions(results);

                                //let the users know that the background
                                //process has completed
                                mSearchView.hideProgress();
                            });
                }
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

        // 点击搜索栏之后
        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                //显示搜索历史，所以修改getHistory就行
                mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 5));
//                onBackPressed();

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
//                mSearchView.setSearchBarTitle(mLastQuery);
                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());
                Log.d(TAG, "onFocusCleared()");
            }
        });

        mSearchView.setOnBindSuggestionCallback((suggestionView, leftIcon, textView, item, itemPosition) -> {
            RubbishSuggestion rubbishSuggestion = (RubbishSuggestion) item;

            String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
            String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";

            //在这里设置搜索历史的图标，也可以用在其他地方
            if (rubbishSuggestion.getIsHistory()) {
                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_history_black_24dp, null));

                Util.setIconColor(leftIcon, Color.parseColor(textColor));
                leftIcon.setAlpha(.36f);
            } else {
                leftIcon.setAlpha(0.0f);
                leftIcon.setImageDrawable(null);
            }

            textView.setTextColor(Color.parseColor(textColor));
            String text = rubbishSuggestion.getBody()
                    .replaceFirst(mSearchView.getQuery(),
                            "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
            textView.setText(Html.fromHtml(text));
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSearchView.setTranslationY(verticalOffset);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backButton:
                rubd.setVisibility(View.GONE);
                rubf.setVisibility(View.GONE);
                mSearchView.clearQuery();
                mSearchView.clearSearchFocus();
                isHandleBack=false;
                ObjectAnimator animatorBaseChar = ObjectAnimator.ofFloat(baseChar,"alpha",0f,1f);
                ObjectAnimator animatorSearch = ObjectAnimator.ofFloat(mSearchView,"translationY",-180f,0f);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(1000);
                set.play(animatorSearch).with(animatorBaseChar);
                set.start();
                break;
        }
    }
    public void onBackPressed(){
        if(isHandleBack){
            rubd.setVisibility(View.GONE);
            rubf.setVisibility(View.GONE);
            mSearchView.clearQuery();
            mSearchView.clearSearchFocus();
            isHandleBack=false;
            ObjectAnimator animatorBaseChar = ObjectAnimator.ofFloat(baseChar,"alpha",0f,1f);
            ObjectAnimator animatorSearch = ObjectAnimator.ofFloat(mSearchView,"translationY",-180f,0f);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(1000);
            set.play(animatorSearch).with(animatorBaseChar);
            set.start();
            isHandleBack=false;
        }
    }

    private static void ChangeByKind(int kind){
        String otherRubExplains = "其他垃圾是指除餐厨垃圾、有害垃圾、可回收物以外的其他生活废弃物";    //kind=3
        String harmRubExplains = "有害垃圾是指对人体健康或自然环境造成直接或潜在危害的零星废弃物";     //kind=2
        String kitcRubExplains = "餐厨垃圾是指易腐的生物质废弃物，包括剩菜、果壳、绿植、碎骨以及日常食品等";    //kind=4
        String recyRubExplains = "可回收物是指适宜回收和可循环再利用的废弃物。包括废玻璃、废金属、废纸张等";    //kind=1
        String otherRubSuggestions = "难以辨识的生活垃圾尽量沥干水分，投入垃圾容器内";
        String harmRubSuggestions = "电池类轻放，油漆桶或杀虫剂类密闭后投放，易碎玻璃包裹后轻放，废药品带包装一起投放";
        String kitcRubSuggestions = "纯流质如奶粥汤酒等可直接倒入下水道，有包装物的餐厨垃圾应将包装物取出后分类投放";
        String recyRubSuggestions = "轻投轻放，应清洁干燥，避免污染。立体包装请清空内容物，清洁后压扁投放";

        switch (kind){
            case 1:
                rubImg.setImageResource(R.drawable.recyc1);
                rubKind.setText("可回收物");
                explains.setText(recyRubExplains);
                suggesions.setText(recyRubSuggestions);
                break;
            case 2:
                rubImg.setImageResource(R.drawable.harm1);
                rubKind.setText("有害垃圾");
                explains.setText(harmRubExplains);
                suggesions.setText(harmRubSuggestions);
                break;
            case 4:
                rubImg.setImageResource(R.drawable.kitc1);
                rubKind.setText("餐厨垃圾");
                explains.setText(kitcRubExplains);
                suggesions.setText(kitcRubSuggestions);
                break;
            case 8:
                rubImg.setImageResource(R.drawable.other1);
                rubKind.setText("其他垃圾");
                explains.setText(otherRubExplains);
                suggesions.setText(otherRubSuggestions);
                break;
        }
    }

    private void initAnimator(){
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(ToRightCar,"translationX",0f,-1200f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(ToRightCar,"translationX",1200f,0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(ToLeftCar,"translationX",0f,1200f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(ToLeftCar,"translationX",-1200f,0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(9000);
        animator4.setStartDelay(9000);
        animator2.setStartDelay(9000);
        set.play(animator1).with(animator3).with(animator4).with(animator2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();

        ObjectAnimator animatoreye1 = ObjectAnimator.ofFloat(eyeBall,"translationY",0f,-10f);
        ObjectAnimator animatoreye2 = ObjectAnimator.ofFloat(eyeBall,"translationX",0f,-10f);
        ObjectAnimator animatoreye3 = ObjectAnimator.ofFloat(eyeBall,"translationY",-10f,0f);
        ObjectAnimator animatoreye4 = ObjectAnimator.ofFloat(eyeBall,"translationX",-10f,0f);
        animatoreye2.setStartDelay(700);
        animatoreye3.setStartDelay(1400);
        animatoreye4.setStartDelay(2000);
        AnimatorSet set1 = new AnimatorSet();
        set1.setDuration(1000);
        set1.setStartDelay(1000);
        set1.play(animatoreye1).with(animatoreye2).with(animatoreye3).with(animatoreye4);
        set1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set1.start();

        ObjectAnimator animatorCan1 = ObjectAnimator.ofFloat(blueCan,"translationY",0f,-15f,0f);
        ObjectAnimator animatorCan2 = ObjectAnimator.ofFloat(yellowCan,"translationY",0f,-15f,0f);
        ObjectAnimator animatorCan3 = ObjectAnimator.ofFloat(greenCan,"translationY",0f,-15f,0f);
        ObjectAnimator animatorCan4 = ObjectAnimator.ofFloat(redCan,"translationY",0f,-15f,0f);
        animatorCan2.setStartDelay(1000);
        animatorCan3.setStartDelay(2000);
        animatorCan4.setStartDelay(3000);
        AnimatorSet setCan = new AnimatorSet();
        setCan.setDuration(1000);
        setCan.play(animatorCan1).with(animatorCan2).with(animatorCan3).with(animatorCan4);
        setCan.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        setCan.start();
    }

    public boolean getIsHandleBack(){
        return isHandleBack;
    }

    public List<RubbishSuggestion> QurySuggestion(String newQuery){
        String path = "http://106.13.235.119:8081/SearchServerweb/search?name="+newQuery;
        List<RubbishSuggestion> Suggestions = new ArrayList<>();
        URL url = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String jsonstr = HttpUtil.doPost(url);
        JsonReader reader;
        assert jsonstr != null;
        if(!jsonstr.equals("[]")){
            Log.d(TAG, "QurySuggestion: kkkk " + jsonstr);
            reader = new JsonReader(new StringReader(jsonstr));
            reader.setLenient(true);
            try {
                reader.beginArray();
                while(reader.hasNext()){
                    RubbishSuggestion aRubbishSuggestion = new RubbishSuggestion();
                    reader.beginObject();
                    while(reader.hasNext()){
                        String tagName = reader.nextName();
                        if(tagName.equals("name")){
                            aRubbishSuggestion.setRubbishName(reader.nextString());
                        }
                        else if(tagName.equals("kind")){
                            aRubbishSuggestion.setRubbishKind(reader.nextInt());
                        }
                    }
                    Suggestions.add(aRubbishSuggestion);
                    reader.endObject();
                }
                reader.endArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Suggestions;
    }
    private void showNormalDialog(){
        AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                .setTitle("提示")//标题
                .setMessage("暂时没有这个垃圾，我们会尽快补充词条的！")//内容
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog1.show();
    }

    private void showResult(RubbishSuggestion searchSuggestion){
        RubbishSuggestion rubbishSuggestion = searchSuggestion;
        DataHelper.changeHistory(rubbishSuggestion);

        //关闭软键盘
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘

//                Log.d(TAG, "onSuggestionClicked: dddd " + rubbishSuggestion.getBody());
        //点击之后，searchSuggestion就是选择的结果,根据kind选择界面，关闭搜索栏
        rubf.setVisibility(View.VISIBLE);
        rubd.setVisibility(View.VISIBLE);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(rubf,"translationY",0f,-1800f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(rubf,"translationY",-1800f,0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(rubd,"translationY",0f,1800f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(rubd,"translationY",1800f,0f);
        ObjectAnimator animatorSearch = ObjectAnimator.ofFloat(mSearchView,"translationY",0f,-180f);
        ObjectAnimator animatorBaseChar = ObjectAnimator.ofFloat(baseChar,"alpha",1f,0f);
        animator1.setDuration(1);
        animator2.setDuration(1000);
        animator3.setDuration(1);
        animator4.setDuration(1000);
        animatorSearch.setDuration(1000);
        animatorBaseChar.setDuration(1000);
        AnimatorSet set1 = new AnimatorSet();
        set1.play(animator1).with(animator2).with(animator3).with(animator4).with(animatorSearch).with(animatorBaseChar);
        set1.start();
        rubName.setText(rubbishSuggestion.getRubbishName());
        ChangeByKind(rubbishSuggestion.getRubbishKind());

        //这下面是只需要点击一次的关键，利用一个Flag来进行区分
        clickFlag = true;
        isHandleBack = true;    //在有这个界面的时候，才需要返回
        mSearchView.setSearchText(rubbishSuggestion.getRubbishName());
        mSearchView.clearSuggestions();
    }
}
