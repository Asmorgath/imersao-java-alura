package com.example.application.data.service;

import com.example.application.data.entity.Sample250Series;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Sample250SeriesRepository extends JpaRepository<Sample250Series, UUID> {

}