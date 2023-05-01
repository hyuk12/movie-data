package com.movie.review.controller;

import com.movie.review.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/api/movies/fetch-and-save")
    public ResponseEntity<String> fetchAndSaveLatestMovies(
            @RequestParam("api_key") String apiKey,
            @RequestParam(value = "number_of_movies", defaultValue = "15000") int numberOfMovies
    ) {
        try {
            movieService.saveMovies(apiKey, numberOfMovies,1, 1, 10);
            return new ResponseEntity<>("Successfully fetched and saved latest movies", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/movies")
//    public List<Movie> saveMovies(String region, String releaseYear) {
//        return movieService.saveMovies(region, releaseYear);
//    }
//
//    @PostMapping("/movies/genre")
//    public List<Movie> saveMoviesByGenre(String region, String releaseYear, String genre) {
//        return movieService.saveMoviesByGenre(region, releaseYear, genre);
//    }
}
