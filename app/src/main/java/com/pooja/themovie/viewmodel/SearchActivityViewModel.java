package com.pooja.themovie.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.pooja.movie.OnGetMoviesCallback;
import com.pooja.movie.model.Movie;
import com.pooja.movie.repository.MoviesRepository;
import com.pooja.themovie.BuildConfig;

import java.util.List;

public class SearchActivityViewModel extends ViewModel {

    private MoviesRepository moviesRepository;
    public MutableLiveData<List<Movie>> searchList;
    public String errorMessage;
    public int currentPage;

    /*
        setup basic needs of this class like repository and MutableDataList
     */

    public void init()
    {
        moviesRepository = MoviesRepository.getInstance();
        if(searchList ==null)
            searchList = new MutableLiveData<>();
    }
    /*
           @param : page number
           return MutableLivedata list from movie repository
        */
    public MutableLiveData<List<Movie>> getSearchList(String searchText, int cuurentPage)
    {
        moviesRepository.getSearchedMovies(searchText,cuurentPage, new OnGetMoviesCallback() {

            @Override
            public void onSuccess(int page, List<Movie> movies) {
                currentPage = page;
                errorMessage = null;
                searchList.setValue(movies);

            }

            @Override
            public void onError(String message) {
                errorMessage = message;
                searchList.setValue(null);
            }
        }, BuildConfig.APIKEY);
        return searchList;
    }
}
