package com.alpha.repositories;

import com.alpha.model.entity.Album;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface AlbumRepository extends JpaRepository<Album, Long>, AlbumRepositoryCustom {

    @NonNull
    Optional<Album> findById(@NonNull @Param("id") Long id);

    Optional<Album> findByIdAndUploader_Username(Long id, String username);

    @NonNull
    Page<Album> findAll(@NonNull Pageable pageable);

    @EntityGraph(value = "album-artist", type = EntityGraphType.FETCH)
    Page<Album> findAllBySync(Integer sync, Pageable pageable);

    @Procedure(procedureName = "DELETE_ALBUM")
    void deleteByIdProc(@Param("p_id") Long id);

}
