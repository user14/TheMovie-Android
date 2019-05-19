package com.pooja.themovie.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pooja.movie.model.Movie;
import com.pooja.themovie.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
    Movie List Adapter
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w92";

    private List<Movie> movies;

    public MoviesAdapter(List<Movie> movies) {
        this.movies = movies;
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.move_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
            movieViewHolder.bindData(movies.get(i));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void appendMovies(List<Movie> moviesToAppend) {
        movies.addAll(moviesToAppend);
        notifyDataSetChanged();
    }

    /*
        View holder class
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private TextView release_date;
        private TextView movieName;
        private TextView movie_description;
        private TextView rating;
        private ImageView icon;


        public MovieViewHolder(@NonNull View itemView) {

            super(itemView);

            release_date = (TextView)itemView.findViewById(R.id.release_date);
            movieName = (TextView)itemView.findViewById(R.id.movie_name);
            movie_description = (TextView)itemView.findViewById(R.id.movie_details);
            rating = (TextView)itemView.findViewById(R.id.movie_rating);
            icon = (ImageView)itemView.findViewById(R.id.movie_icon);

        }

        public void bindData(Movie movie)
        {
            release_date.setText(getDate(movie.getReleaseDate()));
            movieName.setText(movie.getTitle());
            movie_description.setText(movie.getDescription());
            rating.setText(String.valueOf(movie.getRating()));

            // update image icon using Glide lib
            Glide.with(itemView)
                    .load(IMAGE_BASE_URL + movie.getPosterPath())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_foreground))
                    .into(icon);
        }
    }

    private String getDate(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date sourceDate = null;
        try {
            sourceDate = dateFormat.parse(date);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy");

            return targetFormat.format(sourceDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }
}
