package com.lambda.events;

import com.lambda.models.entities.User;

public interface CustomEvent {
    String getAppUrl();
    User getUser();
}