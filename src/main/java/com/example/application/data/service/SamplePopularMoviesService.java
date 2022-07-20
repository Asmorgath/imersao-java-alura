package com.example.application.data.service;

import com.example.application.data.entity.SamplePopularMovies;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SamplePopularMoviesService {

    private final SamplePopularMoviesRepository repository;

    @Autowired
    public SamplePopularMoviesService(SamplePopularMoviesRepository repository) {
        this.repository = repository;
    }

    public Optional<SamplePopularMovies> get(UUID id) {
        return repository.findById(id);
    }

    public SamplePopularMovies update(SamplePopularMovies entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<SamplePopularMovies> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
