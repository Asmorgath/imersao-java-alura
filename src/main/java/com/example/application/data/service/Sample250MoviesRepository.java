package com.example.application.data.service;

import com.example.application.data.entity.Sample250Movies;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Sample250MoviesRepository extends JpaRepository<Sample250Movies, UUID> {

}