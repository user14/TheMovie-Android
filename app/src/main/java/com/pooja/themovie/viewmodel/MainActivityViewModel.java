package com.pooja.themovie.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.pooja.movie.OnGetMoviesCallback;
import com.pooja.movie.model.Movie;
import com.pooja.movie.repository.MoviesRepository;
import com.pooja.themovie.BuildConfig;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private MoviesRepository mMoviesRepository;
    public MutableLiveData<List<Movie>> mMovieList;
    public String errorMessage;
    public int currentPage;

    /*
        setup basic needs of this class like repository and MutableDataList
     */
    public void init()
    {
        mMoviesRepository = MoviesRepository.getInstance();
        if(mMovieList ==null)
            mMovieList = new MutableLiveData<>();
    }

    /*
        @param : page number
        return MutaableLivedata list from movie repository
     */
    public MutableLiveData<List<Movie>> getMoviesList(int cuurentPage)
    {
        mMoviesRepository.getMovies(cuurentPage, new OnGetMoviesCallback() {

            @Override
            public void onSuccess(int page, List<Movie> movies) {
                currentPage = page;
                errorMessage = null;
                mMovieList.setValue(movies);

            }

            @Override
            public void onError(String message) {
                errorMessage = message;
                mMovieList.setValue(null);
            }
        }, BuildConfig.APIKEY);
        return mMovieList;
    }
}
