package com.movie.review.dto.response;

import com.movie.review.entity.Cast;
import com.movie.review.entity.Crew;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CreditResponse {
    private List<Cast> cast;
    private List<Crew> crew;
}
