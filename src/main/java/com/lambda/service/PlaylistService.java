package com.lambda.service;

import com.lambda.model.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PlaylistService {
    boolean checkSongExistence(Playlist playlist, Long songId);
    Optional<Playlist> findById(Long id);
    Iterable<Playlist> findAllByUser_Id(Long userId);
    Page<Playlist> findAllByUser_Id(Long userId, Pageable pageable);
    Page<Playlist> findAllByNameContaining(String name, Pageable pageable);
    void save(Playlist playlist);
    void deleteById(Long id);
    boolean addSongToPlaylist(Long songId, Long playlistId);
    boolean deleteSongFromPlaylist(Long songId, Long playlistId);
}
