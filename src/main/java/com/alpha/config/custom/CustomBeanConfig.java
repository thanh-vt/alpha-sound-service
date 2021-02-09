package com.alpha.config.custom;

import com.alpha.error.FileStorageException;
import com.cloudinary.Cloudinary;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

//import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@SuppressWarnings("deprecation")
public class CustomBeanConfig {

    @Value("${custom.auth-service}")
    private String tokenEndpoint;

    @Value("${custom.client-id}")
    private String clientId;

    @Value("${custom.client-secret}")
    private String clientSecret;

    @Value("${storage.cloudinary.url}")
    private String cloudinaryUrl;

    @Value("${storage.firebase.credentials}")
    private String firebaseCredentials;

    private final AccessTokenConverter accessTokenConverter;

    public CustomBeanConfig(AccessTokenConverter accessTokenConverter) {
        this.accessTokenConverter = accessTokenConverter;
    }

//    @Bean
//    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
//        return new SecurityEvaluationContextExtension();
//    }

    @Bean
    @Profile({"default", "heroku"})
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }

    @Bean
    @Profile({"default", "heroku"})
    public StorageClient firebaseStorage() {
        try {
//            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/Users/nguyenxuanhoang/Documents/ThucHanhCodeGym/adminsdk/climax-sound-firebase-adminsdk-c29fo-27166cf850.json"))
//                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
//            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/mnt/D43C7B5B3C7B3816/CodeGym/Module 4/Project Climax Sound/climax-sound-firebase-adminsdk-c29fo-27166cf850.json"))
//                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
//            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
          GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(firebaseCredentials.getBytes()));
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setDatabaseUrl("https://climax-sound.firebaseio.com")
                    .setStorageBucket("climax-sound.appspot.com")
                    .build();

            FirebaseApp fireApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            if (firebaseApps != null && !firebaseApps.isEmpty()) {
                for (FirebaseApp app : firebaseApps) {
                    if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                        fireApp = app;
                }
            } else
                fireApp = FirebaseApp.initializeApp(options);
            return StorageClient.getInstance(fireApp);
        } catch (IOException ex) {
            throw new FileStorageException("Could not get admin-sdk json file. Please try again!", ex);
        }
//        catch (JSONException ex) {
//            throw new JsonIOException("Could not get admin-sdk json file. Please try again!", ex);
//        }
    }

    @Primary
    @Bean
    public RemoteTokenServices tokenService() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(tokenEndpoint);
        tokenService.setClientId(clientId);
        tokenService.setClientSecret(clientSecret);
        tokenService.setAccessTokenConverter(accessTokenConverter);
        return tokenService;
    }
}