package com.pooja.themovie.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pooja.movie.model.Movie;
import com.pooja.themovie.R;
import com.pooja.themovie.adapter.MoviesAdapter;
import com.pooja.themovie.viewmodel.SearchActivityViewModel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    //Search TextView
    private AutoCompleteTextView mSearchAutoCompleteTextView;

    //Search button
    private Button mSearchButton;

    // Recycler view for search listview
    private RecyclerView mSearchListRecyclerView;

    //Movie Adapter
    private MoviesAdapter mMoviesAdapter;

    // boolean for checking the current ongoing movie request's status
    private boolean isFetchingMovies;

    private int currentPage = 1;

    //List for keeping searched movie history
    private ArrayList<String> mSearchedMovieHistoryList;

    // SharedPrefrence Key
    private final String SAVED_MOVIE_LIST = "saved_movie_list";

    //ArrayAdapter for AutoCompleteTextView dropdown list
    ArrayAdapter mArrayAdapter =null;

    //Viewmodel class reference
    SearchActivityViewModel mSearchActivityViewModel = null;

    //MutableLiveData for searched list
    MutableLiveData<List<Movie>> listMutableLiveData;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchAutoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.search_AutoCompleteTextView);
        mSearchButton = (Button)findViewById(R.id.search_Button);
        mSearchListRecyclerView = (RecyclerView)findViewById(R.id.search_recyclerView);
        mSearchAutoCompleteTextView.setThreshold(1);

        // get saved search list
        mSearchedMovieHistoryList = getSearchedHistoryList(SAVED_MOVIE_LIST);
        if(mSearchedMovieHistoryList == null)
        {
            mSearchedMovieHistoryList = new ArrayList<String>();

        }

        //set adapter for mSearchAutoCompleteTextView
        mArrayAdapter = new ArrayAdapter(SearchActivity.this,android.R.layout.simple_list_item_1, mSearchedMovieHistoryList);
        mSearchAutoCompleteTextView.setAdapter(mArrayAdapter);



        //Call on touch of Search textbox
        mSearchAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(!mSearchAutoCompleteTextView.isPopupShowing()) {
                    mSearchAutoCompleteTextView.showDropDown();
                    return false;
                }
                showKeyboard(mSearchAutoCompleteTextView);
                return true;
            }
        });



        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mSearchListRecyclerView.setLayoutManager(layoutManager);

        mSearchActivityViewModel = ViewModelProviders.of(this).get(SearchActivityViewModel.class);
        mSearchActivityViewModel.init();


        // search button click event happpend, validate search text and get movies list
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchAutoCompleteTextView.getText().toString().trim();
                if(!searchText.isEmpty())
                    getMovies(searchText);
                else
                {
                     showAlertDialog(getResources().getString(R.string.alert),getResources().getString(R.string.validate_string));
                     mSearchListRecyclerView.setAdapter(null);
                }
            }
        });

        setObserverOnMovieList();

    }


    /*
       Setup scrolling of mRecyclerView
       It will check if visible item count is greater than total item count than It will increase the currentPage count
       by 1 and send request for that page movie items.
    */
    private void setupOnScrollListener(final String searchText) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchListRecyclerView.setLayoutManager(linearLayoutManager);

        mSearchListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = linearLayoutManager.getItemCount();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    if (!isFetchingMovies) {
                        listMutableLiveData = mSearchActivityViewModel.getSearchList(searchText,currentPage+1);

                    }
                }
            }
        });


    }


    /*
        Give successfully searched movie list from SharedPrefrence
     */
    public ArrayList<String> getSearchedHistoryList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveSearchedHistoryList(String search, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        if(mSearchedMovieHistoryList !=null && !mSearchedMovieHistoryList.contains(search))
        {

            if(mSearchedMovieHistoryList.size()<=10 )
                mSearchedMovieHistoryList.add(search);
            else {
                mSearchedMovieHistoryList.remove(0);
                mSearchedMovieHistoryList.add(9,search);
            }

        }

        String json = gson.toJson(mSearchedMovieHistoryList);
        editor.putString(key, json);
        editor.apply();
        mArrayAdapter = new ArrayAdapter(SearchActivity.this,android.R.layout.simple_list_item_1, mSearchedMovieHistoryList);
        mSearchAutoCompleteTextView.setAdapter(mArrayAdapter);


    }
    /*
        Fetch list of movies if internet is available and also setup scrollview to the mSearchListRecyclerView
     */
    public void getMovies(String searchText)
    {
        if(isInternetAvailable())
        {
            hideKeyboard();
            showProgressDialog(getResources().getString(R.string.loading),getResources().getString(R.string.wait));
            currentPage = 0;
            mMoviesAdapter = null;
            isFetchingMovies = true;
            listMutableLiveData = mSearchActivityViewModel.getSearchList(searchText,currentPage+1);
            setupOnScrollListener(searchText);
        }
        else
            showAlertDialog(getResources().getString(R.string.alert),getResources().getString(R.string.no_internet));

    }

    /*
       set observer on the MutableLiveData movie list return from SearchActivityViewModel
    */
    private void setObserverOnMovieList()
    {
        if(listMutableLiveData==null)
            listMutableLiveData = mSearchActivityViewModel.searchList;

        listMutableLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                hideDialog();
                if (mSearchActivityViewModel.errorMessage == null) {
                    if (mMoviesAdapter == null) {
                        mMoviesAdapter = new MoviesAdapter(movies);
                        mSearchListRecyclerView.setAdapter(mMoviesAdapter);
                    } else {
                        mMoviesAdapter.appendMovies(movies);
                    }
                    currentPage = mSearchActivityViewModel.currentPage;
                    isFetchingMovies = false;
                    saveSearchedHistoryList(mSearchAutoCompleteTextView.getText().toString().trim(), SAVED_MOVIE_LIST);
                }
                else
                {
                    showAlertDialog(getResources().getString(R.string.alert), mSearchActivityViewModel.errorMessage);
                }
            }
        });

    }


}
