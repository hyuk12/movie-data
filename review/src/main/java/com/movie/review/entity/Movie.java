package com.movie.review.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String title;
    private String posterUrl;
    private String releaseDate;
    private Double voteAverage;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
    private String actors;
    private String directors;
    private String backdropUrl;
    private Boolean adult;
    private Long tmdbId;
}
