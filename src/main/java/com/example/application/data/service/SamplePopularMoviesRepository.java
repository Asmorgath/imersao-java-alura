package com.example.application.data.service;

import com.example.application.data.entity.SamplePopularMovies;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePopularMoviesRepository extends JpaRepository<SamplePopularMovies, UUID> {

}