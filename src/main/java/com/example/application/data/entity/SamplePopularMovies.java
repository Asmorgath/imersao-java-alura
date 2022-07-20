package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class SamplePopularMovies extends AbstractEntity {

    @Lob
    private String image;
    private Integer ranking;
    private String name;
    private Integer imDbRating;
    private Integer yearMovie;
    private String crew;

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public Integer getRanking() {
        return ranking;
    }
    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getImDbRating() {
        return imDbRating;
    }
    public void setImDbRating(Integer imDbRating) {
        this.imDbRating = imDbRating;
    }
    public Integer getYearMovie() {
        return yearMovie;
    }
    public void setYearMovie(Integer yearMovie) {
        this.yearMovie = yearMovie;
    }
    public String getCrew() {
        return crew;
    }
    public void setCrew(String crew) {
        this.crew = crew;
    }

}
