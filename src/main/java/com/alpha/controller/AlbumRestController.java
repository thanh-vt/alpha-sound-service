package com.alpha.controller;

import com.alpha.elastic.model.AlbumEs;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumDTO.AlbumAdditionalInfoDTO;
import com.alpha.model.dto.AlbumSearchDTO;
import com.alpha.model.dto.AlbumUpdateDTO;
import com.alpha.service.AlbumService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/album")
public class AlbumRestController {

    private final AlbumService albumService;

    public AlbumRestController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Page<AlbumDTO>> albumList(Pageable pageable) {
        Page<AlbumDTO> albumList = this.albumService.findAll(pageable);
        return new ResponseEntity<>(albumList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/es-search")
    public ResponseEntity<Page<AlbumEs>> search(@RequestParam(value = "name") String name,
        Pageable pageable) {
        Page<AlbumEs> songList = this.albumService.findPageByName(name, pageable);
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<AlbumDTO> albumDetail(@RequestParam("id") Long id, Pageable pageable) {
        AlbumDTO albumDTO = this.albumService.detail(id, pageable);
        return new ResponseEntity<>(albumDTO, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/additional-info/{id}")
    public ResponseEntity<AlbumAdditionalInfoDTO> albumAdditionalInfo(@PathVariable("id") Long id) {
        AlbumAdditionalInfoDTO albumAdditionalInfoDTO = this.albumService
            .findAdditionalInfoById(id);
        return new ResponseEntity<>(albumAdditionalInfoDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<AlbumDTO>> albumSearch(@ModelAttribute AlbumSearchDTO albumSearchDTO,
        Pageable pageable) {
        Page<AlbumDTO> filteredAlbumList = this.albumService
            .findAllByConditions(pageable, albumSearchDTO);
        return new ResponseEntity<>(filteredAlbumList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload")
    public ResponseEntity<AlbumDTO> createAlbum(@Valid @RequestPart("album") AlbumDTO album,
        @RequestPart(value = "cover", required = false)
            MultipartFile file) {
        AlbumDTO albumDTO = this.albumService.upload(file, album);
        return new ResponseEntity<>(albumDTO, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/update-song-list/{albumId}")
    public ResponseEntity<AlbumDTO> createAlbum(
        @Valid @RequestBody List<AlbumUpdateDTO> songDTOList,
        @PathVariable("albumId") Long albumId) {
        this.albumService.updateSongList(albumId, songDTOList);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<AlbumDTO> editAlbum(@Valid @RequestPart("album") AlbumDTO album,
        @RequestPart(value = "cover", required = false)
            MultipartFile file,
        @PathVariable("id") Long id) {
        AlbumDTO albumDTO = this.albumService.update(file, album, id);
        return new ResponseEntity<>(albumDTO, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteAlbum(@RequestParam("id") Long id) {
        this.albumService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
