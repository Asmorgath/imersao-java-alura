package com.example.application.data.service;

import com.example.application.data.entity.Sample250Series;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class Sample250SeriesService {

    private final Sample250SeriesRepository repository;

    @Autowired
    public Sample250SeriesService(Sample250SeriesRepository repository) {
        this.repository = repository;
    }

    public Optional<Sample250Series> get(UUID id) {
        return repository.findById(id);
    }

    public Sample250Series update(Sample250Series entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Sample250Series> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
