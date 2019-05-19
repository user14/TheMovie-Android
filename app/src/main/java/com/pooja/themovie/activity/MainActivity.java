package com.pooja.themovie.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pooja.movie.model.Movie;
import com.pooja.themovie.BuildConfig;
import com.pooja.themovie.R;
import com.pooja.themovie.adapter.MoviesAdapter;
import com.pooja.themovie.viewmodel.MainActivityViewModel;

import java.util.List;

public class MainActivity extends BaseActivity {
    //Movie list
    private RecyclerView mRecyclerView;

    // Movie list adapter
    private MoviesAdapter mMoviesAdapter;

    // boolean for checking the current ongoing movie request's status
    private boolean isFetchingMovies;

    // int for request's page count
    private int currentPage = 1;

    // MainActivityViewModel reference object
    MainActivityViewModel mMainActivityViewModel = null;

    //MutableLivedata list of movies
    MutableLiveData<List<Movie>> mMovielistMutableLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.movie_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        // Search button, it will start the SearchActivity
        FloatingActionButton fab = findViewById(R.id.search_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mMainActivityViewModel.init();
        mMovielistMutableLiveData = mMainActivityViewModel.mMovieList;


        //Check for the internet connectivity and get the movie list from MainActivityViewModel
        if(isInternetAvailable()) {
            showProgressDialog(getResources().getString(R.string.loading),getResources().getString(R.string.wait));
            isFetchingMovies = true;
            mMovielistMutableLiveData = mMainActivityViewModel.getMoviesList(currentPage);
            setupOnScrollListener();
        }
        else
            showAlertDialog(getResources().getString(R.string.alert),getResources().getString(R.string.no_internet));

        setObserverOnMovieList();

    }

    /*
        return layout of MainActivity to BaseActivity to set in the View
     */
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    /*
        Setup scrolling of mRecyclerView
        It will check if visible item count is greater than total item count than It will increase the currentPage count
        by 1 and send request for that page movie items.
     */
    private void setupOnScrollListener() {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = linearLayoutManager.getItemCount();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    if (!isFetchingMovies) {
                        mMovielistMutableLiveData = mMainActivityViewModel.getMoviesList(currentPage+1);

                    }
                }
            }
        });


    }

    /*
        Update the list in mRecyclerView
     */
    private void updateAdapter(List<Movie> movies)
    {
        if (mMoviesAdapter == null) {
            mMoviesAdapter = new MoviesAdapter(movies);
            mRecyclerView.setAdapter(mMoviesAdapter);
        } else {
            mMoviesAdapter.appendMovies(movies);
        }
    }

    /*
        set observer on the MutableLiveData movie list return from MainActivityViewModel
     */
    private void setObserverOnMovieList()
    {
        if(mMovielistMutableLiveData !=null)
            mMovielistMutableLiveData.observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    hideDialog();
                    if (mMainActivityViewModel.errorMessage == null) {

                        updateAdapter(movies);
                        currentPage = mMainActivityViewModel.currentPage;
                        isFetchingMovies = false;
                    }else
                    {

                        showAlertDialog(getResources().getString(R.string.alert), mMainActivityViewModel.errorMessage);
                    }
                }
            });
    }


}
