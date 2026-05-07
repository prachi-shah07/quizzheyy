package com.quizapp.service;

import com.quizapp.model.User;

import java.util.List;

public interface RecommendationService {

    List<String> getRecommendations(User user) throws Exception;
}
