package com.example.application.data.service;

import com.example.application.data.entity.SamplePopularSeries;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePopularSeriesRepository extends JpaRepository<SamplePopularSeries, UUID> {

}