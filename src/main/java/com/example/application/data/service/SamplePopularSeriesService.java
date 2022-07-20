package com.example.application.data.service;

import com.example.application.data.entity.SamplePopularSeries;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SamplePopularSeriesService {

    private final SamplePopularSeriesRepository repository;

    @Autowired
    public SamplePopularSeriesService(SamplePopularSeriesRepository repository) {
        this.repository = repository;
    }

    public Optional<SamplePopularSeries> get(UUID id) {
        return repository.findById(id);
    }

    public SamplePopularSeries update(SamplePopularSeries entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<SamplePopularSeries> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
