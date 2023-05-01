package com.movie.review.dto.request;

import com.movie.review.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieData {
    private int id;
    private String title;
    private String overview;
    private String PosterPath;
    private String backdropUrl;
    private String releaseDate;
    private double voteAverage;
    private boolean adult;
    private List<Genre> genres;
}
