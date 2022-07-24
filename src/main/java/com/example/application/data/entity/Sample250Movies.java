package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Sample250Movies extends AbstractEntity {

    @Lob
    private String image;
    private Integer rank;
    private String name;
    private Double imDbRating;
    private Integer yearMovie;
    private String crew;

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public Integer getRank() {
        return rank;
    }
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getImDbRating() {
        return imDbRating;
    }
    public void setImDbRating(Double imDbRating) {
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

    public Sample250Movies(String image, Integer rank, String name, Double imDbRating, Integer yearMovie, String crew) {
        this.image = image;
        this.rank = rank;
        this.name = name;
        this.imDbRating = imDbRating;
        this.yearMovie = yearMovie;
        this.crew = crew;
    }
}
