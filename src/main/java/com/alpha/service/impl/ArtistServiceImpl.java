package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.EntityType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.ModelStatus;
import com.alpha.elastic.model.ArtistEs;
import com.alpha.elastic.repo.ArtistEsRepository;
import com.alpha.mapper.ArtistMapper;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.ArtistSearchDTO;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.ArtistRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.service.ArtistService;
import com.alpha.service.FavoritesService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
public class ArtistServiceImpl implements ArtistService {

    @Value("${storage.storage-type}")
    private StorageType storageType;

    private final ArtistRepository artistRepository;

    private final ArtistEsRepository artistEsRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final ArtistMapper artistMapper;

    private final StorageService storageService;

    private final UserService userService;

    private final FavoritesService favoritesService;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository,
        ArtistEsRepository artistEsRepository,
        ResourceInfoRepository resourceInfoRepository,
        ArtistMapper artistMapper, StorageService storageService,
        UserService userService, FavoritesService favoritesService) {
        this.artistRepository = artistRepository;
        this.artistEsRepository = artistEsRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.artistMapper = artistMapper;
        this.storageService = storageService;
        this.userService = userService;
        this.favoritesService = favoritesService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> findAll(Pageable pageable) {
        return this.artistRepository.findByConditions(pageable, new ArtistSearchDTO());
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDTO findById(Long id) {
        Optional<Artist> optionalArtist = this.artistRepository.findById(id);
        if (optionalArtist.isPresent()) {
            Artist artist = optionalArtist.get();
            ArtistDTO artistDTO = this.artistMapper.entityToDto(artist);
            Optional<ResourceInfo> optionalResourceInfo = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(artist.getId(), this.storageType,
                    MediaRef.ARTIST_AVATAR, ModelStatus.ACTIVE);
            optionalResourceInfo.ifPresent(
                resourceInfo -> artistDTO
                    .setAvatarUrl(resourceInfo.getUri()));
            this.favoritesService.setLike(artistDTO, EntityType.ARTIST);
            return artistDTO;
        } else {
            throw new EntityNotFoundException("Artist not found!");
        }
    }

    @Override
    @SneakyThrows
    @Transactional(readOnly = true)
    public Page<ArtistEs> findPageByName(String name, Pageable pageable) {
        String phrase = StringAccentRemover.removeStringAccent(name);
        log.info("Phrase {}", phrase);
//        return this.artistEsRepository
//            .findAll(pageable)
//            .map(e -> {
//                e.setAvatarUrl(this.storageService.getFullUrl(e.getResourceMap()));
//                return e;
//            });
        return this.artistEsRepository
            .findPageByName(phrase, pageable)
            .map(e -> {
                e.setAvatarUrl(this.storageService.getFullUrl(e.getResourceMap()));
                return e;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> findByConditions(Pageable pageable, ArtistSearchDTO artistSearchDTO) {
        return this.artistRepository.findByConditions(pageable, artistSearchDTO);
    }

    @Override
    @Transactional
    public ArtistDTO create(ArtistDTO artistDTO, MultipartFile multipartFile) {
        UserInfo currentUser = this.userService.getCurrentUserInfo();
        Artist artist = Artist.builder()
            .name(artistDTO.getName())
            .likeCount(0L)
            .uploader(currentUser)
            .status(ModelStatus.ACTIVE)
            .createTime(new Date())
            .sync(0)
            .build();
        this.patchArtist(artistDTO, artist);
        this.artistRepository.saveAndFlush(artist);
        ResourceInfo resourceInfo = this.storageService.upload(multipartFile, artist);
        artistDTO.setId(artist.getId());
        artistDTO.setUnaccentName(artist.getUnaccentName());
        artistDTO.setAvatarUrl(resourceInfo.getUri());
        return artistDTO;
    }


    @Override
    @Transactional
    public ArtistDTO update(Long id, ArtistDTO artistDTO, MultipartFile multipartFile)
        throws IOException {
        Optional<Artist> oldArtist = this.artistRepository.findById(id);
        if (oldArtist.isPresent()) {
            Artist artist = oldArtist.get();
            if (multipartFile != null) {
                ResourceInfo resourceInfo = this.storageService
                    .upload(multipartFile, artist);
                artist.setAvatarResource(resourceInfo);
                artistDTO.setAvatarUrl(resourceInfo.getUri());
            }
            this.patchArtist(artistDTO, artist);
            artist.setUpdateTime(new Date());
            artist.setSync(0);
            this.artistRepository.save(artist);
            artistDTO.setUnaccentName(artist.getUnaccentName());
            return artistDTO;
        } else {
            throw new RuntimeException("Artist does not exist");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }

    @Override
    public Map<Long, Boolean> getUserArtistLikeMap(Map<Long, Boolean> artistIdMap) {
        return null;
    }

    private void patchArtist(ArtistDTO artistDTO, Artist artist) {
        artist.setName(artistDTO.getName());
        artist.setUnaccentName(StringAccentRemover.removeStringAccent(artistDTO.getName()));
        artist.setBirthDate(artistDTO.getBirthDate());
        artist.setBiography(artistDTO.getBiography());
    }

}
