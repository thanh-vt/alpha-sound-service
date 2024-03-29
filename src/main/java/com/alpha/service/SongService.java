package com.alpha.service;

import com.alpha.elastic.model.SongEs;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongDTO.SongAdditionalInfoDTO;
import com.alpha.model.dto.SongSearchDTO;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface SongService {

    Page<SongDTO> findAll(Pageable pageable);

    Page<SongEs> findPageByName(String name, Pageable pageable);

    Page<SongDTO> findAllByConditions(Pageable pageable, SongSearchDTO songSearchDTO);

    SongDTO findById(Long id);

    SongAdditionalInfoDTO findAdditionalInfoById(Long id);

    SongDTO upload(MultipartFile file, SongDTO songDTO);

    SongDTO update(Long id, SongDTO songDTO, MultipartFile multipartFile) throws IOException;

    void deleteById(Long id);

    void deleteAll(Collection<SongDTO> songs);

}
