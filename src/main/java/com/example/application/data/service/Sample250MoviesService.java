package com.example.application.data.service;

import com.example.application.data.entity.Sample250Movies;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class Sample250MoviesService {

    private final Sample250MoviesRepository repository;

    @Autowired
    public Sample250MoviesService(Sample250MoviesRepository repository) {
        this.repository = repository;
    }

    public Optional<Sample250Movies> get(UUID id) {
        return repository.findById(id);
    }

    public Sample250Movies update(Sample250Movies entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Sample250Movies> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<Sample250Movies> listByURL(){
        HttpManager httpManager = new HttpManager("https://alura-imdb-api.herokuapp.com/movies");
        String data = httpManager.getData();

        return new ParsedDataReturner().extractMovies(data);
    }


}
