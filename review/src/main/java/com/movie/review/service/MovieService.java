package com.movie.review.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.movie.review.dto.request.MovieData;
import com.movie.review.entity.Genre;
import com.movie.review.entity.Movie;
import com.movie.review.repository.GenreRepository;
import com.movie.review.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public List<Integer> fetchLatestMovieIds(String apiKey, int numberOfMovies, int recentMonths, int upcomingMonths, int pastYears) throws IOException {
        Calendar calendar = Calendar.getInstance();

        String startDate = "";
        String endDate = "";
        List<Integer> movieIds = new ArrayList<>();
        int limit = 100;

        for (int m = 1; m <= recentMonths; m++) {
            calendar.add(Calendar.MONTH, -1);
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            calendar.add(Calendar.MONTH, -1);
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

//          String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=ko-KR&sort_by=popularity.desc&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
            String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=ko-KR&sort_by=release.desc&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
            movieIds.addAll(fetchMovieIdsFromUrl(apiKey, url, limit));
        }

        for (int m = 0; m < upcomingMonths; m++) {
            calendar.add(Calendar.MONTH, 1);
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            calendar.add(Calendar.MONTH, 1);
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=ko-KR&sort_by=release.desc&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
            movieIds.addAll(fetchMovieIdsFromUrl(apiKey, url, limit));
        }

        for (int y = 1; y <= pastYears; y++) {
            calendar.add(Calendar.YEAR, -1);
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            calendar.add(Calendar.YEAR, -1);
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=ko-KR&sort_by=release.desc&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
            movieIds.addAll(fetchMovieIdsFromUrl(apiKey, url, limit));
        }

        return movieIds.stream().distinct().limit(numberOfMovies).collect(Collectors.toList());
    }

    public List<Integer> fetchMoviesByGenre(String apiKey, int genreId, int numberOfMovies, int recentMonths, int upcomingMonths, int pastYears) throws IOException {
        Calendar calendar = Calendar.getInstance();

        String startDate = "";
        String endDate = "";
        List<Integer> movieIds = new ArrayList<>();

        for (int m = 1; m <= recentMonths; m++) {
            calendar.add(Calendar.MONTH, -1);
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            calendar.add(Calendar.MONTH, -1);
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=ko-KR&sort_by=release_date.desc&page=1&with_genres=" + genreId + "&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
            movieIds.addAll(fetchMovieIdsFromUrl(apiKey, url, 100));
        }

        for (int m = 0; m < upcomingMonths; m++) {
            calendar.add(Calendar.MONTH, 1);
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            calendar.add(Calendar.MONTH, 1);
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=ko-KR&sort_by=release_date.desc&page=1&with_genres=" + genreId + "&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
            movieIds.addAll(fetchMovieIdsFromUrl(apiKey, url, 100));
        }

        for (int y = 1; y <= pastYears; y++) {
            calendar.add(Calendar.YEAR, -1);
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            calendar.add(Calendar.YEAR, -1);
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=ko-KR&sort_by=release_date.desc&page=1&with_genres=" + genreId + "&primary_release_date.gte=" + startDate + "&primary_release_date.lte=" + endDate;
            movieIds.addAll(fetchMovieIdsFromUrl(apiKey, url, 100));
        }

        return movieIds.stream().distinct().limit(numberOfMovies).collect(Collectors.toList());
    }

    private List<Integer> fetchMovieIdsFromUrl(String apiKey, String baseUrl, int limit) throws IOException {
        List<Integer> movieIds = new ArrayList<>();
        int currentPage = 1;
        int totalPages = 1;

        while (movieIds.size() < limit && currentPage <= totalPages) {
            String url = baseUrl + "&page=" + currentPage;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed to fetch movie list: " + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            JsonArray results = json.getAsJsonArray("results");
            totalPages = json.get("total_pages").getAsInt();

            for (JsonElement result : results) {
                movieIds.add(result.getAsJsonObject().get("id").getAsInt());
            }

            connection.disconnect();
            currentPage++;
        }

        return movieIds.stream().limit(limit).collect(Collectors.toList());
    }

    private String safeGetAsString(JsonElement element) {
        if (element.isJsonNull()) {
            return "";
        }
        return element.getAsString();
    }


    public MovieData fetchMovieData(String apiKey, int movieId) throws IOException {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey + "&language=ko-KR";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != 200) {
            throw new IOException("Failed to fetch movie data: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }

        String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        JsonObject json = JsonParser.parseString(response).getAsJsonObject();


        MovieData movieData = new MovieData();
        movieData.setId(json.get("id").getAsInt());
        movieData.setTitle(safeGetAsString(json.get("title")));
        movieData.setOverview(safeGetAsString(json.get("overview")));
        movieData.setPosterPath(safeGetAsString(json.get("poster_path")));
        movieData.setGenres(parseGenres(json.getAsJsonArray("genres")));
        movieData.setReleaseDate(safeGetAsString(json.get("release_date")));
        movieData.setVoteAverage(json.get("vote_average").getAsDouble());
        movieData.setAdult(json.get("adult").getAsBoolean());

        connection.disconnect();

        return movieData;
    }

    private List<Genre> parseGenres(JsonArray genresJson) {
        List<Genre> genres = new ArrayList<>();
        for (JsonElement genreElement : genresJson) {
            JsonObject genreObject = genreElement.getAsJsonObject();
            Genre genre = new Genre();
            genre.setId(genreObject.get("id").getAsInt());
            genre.setName(genreObject.get("name").getAsString());
            genres.add(genre);
        }
        return genres;
    }

    public void saveMovies(String apiKey, int numberOfMovies, int recentMonths, int upcomingMonths, int pastYears) throws IOException {
//        List<Integer> latestMovieIds = fetchLatestMovieIds(apiKey, numberOfMovies, recentMonths, upcomingMonths, pastYears);
        List<Integer> latestMovieIds = fetchMoviesByGenre(apiKey, 80, numberOfMovies, recentMonths, upcomingMonths, pastYears);

        for (Integer movieId : latestMovieIds) {
            List<Movie> existingMovies = movieRepository.findByTmdbId(movieId);
            MovieData movieData = fetchMovieData(apiKey, movieId);
            JsonObject movieCredits = fetchMovieCredits(apiKey, movieId);

            Movie movie;
            if (!existingMovies.isEmpty()) {
                movie = existingMovies.get(0);
            } else {
                movie = new Movie();
            }

            Set<Genre> movieGenres = new HashSet<>();
            for (Genre genre : movieData.getGenres()) {
                Genre existingGenre = genreRepository.findById(genre.getId()).orElse(null);
                if (existingGenre == null) {
                    movieGenres.add(genre);
                } else {
                    movieGenres.add(existingGenre);
                }
            }

            // Save movie to the movie table
            movie.setTitle(movieData.getTitle());
            movie.setOverview(movieData.getOverview());
            movie.setPosterUrl(generatePosterUrl(movieData.getPosterPath(), "w500"));
            movie.setBackdropUrl(generatePosterUrl(movieData.getPosterPath(), "original"));
            movie.setReleaseDate(movieData.getReleaseDate());
            movie.setTmdbId(movieData.getId());
            movie.setVoteAverage(movieData.getVoteAverage());
            movie.setAdult(movieData.isAdult());
            // Add other movie attributes here

            JsonArray cast = movieCredits.getAsJsonArray("cast");
            List<JsonElement> castList = new ArrayList<>();
            cast.forEach(castList::add);
            List<String> actors = castList.stream()
                    .map(actor -> actor.getAsJsonObject().get("name").getAsString())
                    .limit(10) // 배우 목록을 10명으로 제한합니다.
                    .collect(Collectors.toList());
            movie.setActors(String.join(",", actors));

            JsonArray crew = movieCredits.getAsJsonArray("crew");
            List<JsonElement> crewList = new ArrayList<>();
            crew.forEach(crewList::add);
            List<String> directors = crewList.stream()
                    .filter(crewMember -> crewMember.getAsJsonObject().get("job").getAsString().equals("Director"))
                    .map(director -> director.getAsJsonObject().get("name").getAsString())
                    .limit(10) // 감독 목록을 10명으로 제한합니다.
                    .collect(Collectors.toList());
            movie.setDirectors(String.join(",", directors));

            // Get the actors and directors from the movie credits

            movie.setGenres(movieGenres);
            movieRepository.save(movie);
        }
    }


    private String generatePosterUrl(String posterPath, String imageSize) {
        String baseUrl = "https://image.tmdb.org/t/p/";
        return baseUrl + imageSize + posterPath;
    }

    public JsonObject fetchMovieCredits(String apiKey, int movieId) throws IOException {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey + "&language=ko-KR";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != 200) {
            throw new IOException("Failed to fetch movie credits: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }

        String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

        connection.disconnect();

        return json;
    }



}
