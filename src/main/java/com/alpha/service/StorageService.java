package com.alpha.service;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.ModelStatus;
import com.alpha.elastic.model.ResourceMapEs;
import com.alpha.model.entity.Media;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.ResourceInfoRepository;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class StorageService {

    protected abstract ResourceInfoRepository getResourceInfoRepository();

    public abstract ResourceInfo upload(MultipartFile multipartFile, Media media);

    public void deleteOldResources(ResourceInfo resourceInfo, StorageType storageType) {
        List<ResourceInfo> resourceInfoList;
        if (resourceInfo.getMediaId() != null) {
            resourceInfoList= this.getResourceInfoRepository()
                .findAllByMediaIdAndStorageTypeAndMediaRefAndStatus(resourceInfo.getMediaId(),
                    storageType,
                    resourceInfo.getMediaRef(), ModelStatus.ACTIVE);
        } else {
            resourceInfoList = this.getResourceInfoRepository()
                .findAllByUsernameAndStorageTypeAndMediaRefAndStatus(resourceInfo.getUsername(),
                    storageType,
                    resourceInfo.getMediaRef(), ModelStatus.ACTIVE);
        }
        resourceInfoList.forEach(this::deleteResourceInfo);
    }

    public void saveResourceInfo(ResourceInfo resourceInfo) {
        this.getResourceInfoRepository().saveAndFlush(resourceInfo);
    }

    public void deleteResourceInfo(ResourceInfo resourceInfo) {
        this.delete(resourceInfo);
        resourceInfo.setStatus(ModelStatus.REMOVED);
        this.getResourceInfoRepository().save(resourceInfo);
//        this.getResourceInfoRepository().delete(resourceInfo);
    }

    public abstract void delete(ResourceInfo resourceInfo);

    public Resource loadFileAsResource(String fileName, String folder) {
        return null;
    }

    public abstract HttpServletRequest getHttpServletRequest();

    public abstract StorageType getStorageType();

    public String getFullUrl(ResourceMapEs resourceMapEs) {
        if (resourceMapEs == null) return "";
        String url = "";
        switch (this.getStorageType()) {
            case FIREBASE:
                url = resourceMapEs.getFirebaseUrl();
                break;
            case CLOUDINARY:
                url = resourceMapEs.getCloudinaryUrl();
                break;
            case LOCAL:
                url = resourceMapEs.getLocalUri();
                break;
        }
        return url;
    }

}
