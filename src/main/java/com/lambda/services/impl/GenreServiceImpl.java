package com.lambda.services.impl;

import com.lambda.models.entities.Genre;
import com.lambda.repositories.GenreRepository;
import com.lambda.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {
    @Autowired
    GenreRepository genreRepository;

    @Override
    public Optional<Genre> findById(Integer id) {
        return genreRepository.findById(id);
    }

    @Override
    public Page<Genre> findAll(Pageable pageable) {
        return genreRepository.findAll(pageable);
    };

    @Override
    public Genre findByName(String name) {
        return genreRepository.findByName(name);
    }

    @Override
    public Iterable<Genre> findAllByNameContaining(String name) {
        return genreRepository.findAllByNameContaining(name);
    }

    @Override
    public void save(Genre genre) {
        genreRepository.save(genre);
    }

    @Override
    public void deleteById(Integer id) {
        genreRepository.deleteById(id);
    }
}