package com.quizapp.service;

import com.quizapp.dao.PerformanceDAO;
import com.quizapp.dao.QuestionDAO;
import com.quizapp.model.Performance;
import com.quizapp.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdaptiveRecommendationService implements RecommendationService {

    private final PerformanceDAO performanceDAO;
    private final QuestionDAO questionDAO;

    public AdaptiveRecommendationService() {
        this.performanceDAO = new PerformanceDAO();
        this.questionDAO = new QuestionDAO();
    }

    @Override
    public List<String> getRecommendations(User user) throws Exception {
        List<String> recommendations = new ArrayList<>();
        List<Performance> performanceList = performanceDAO.getPerformanceByUserId(user.getId());

        if (performanceList.isEmpty()) {
            recommendations.add("Start with a baseline quiz so the system can learn your strengths and weak areas.");
            recommendations.add("Aim to answer each question within 30 seconds to build a reliable speed profile.");
            return recommendations;
        }

        performanceList.sort(Comparator.comparingDouble(Performance::getCompositeScore));
        Performance weakest = performanceList.get(0);
        recommendations.add("Focus first on " + weakest.getTopic() + " because it currently has the lowest composite score.");

        if (weakest.getAccuracy() < 0.50) {
            recommendations.add("Revisit core concepts in " + weakest.getTopic() + " before taking a harder adaptive quiz.");
        }

        if (weakest.getAverageResponseTime() > 20) {
            recommendations.add("Your response time in " + weakest.getTopic() + " is high. Practice timed revision questions to improve speed.");
        }

        long strongTopics = performanceList.stream().filter(item -> item.getCompositeScore() >= 0.80).count();
        if (strongTopics > 0) {
            recommendations.add("You are ready for more difficult questions in at least " + strongTopics + " topic(s).");
        }

        if (!questionDAO.findIncorrectQuestionsForUser(user.getId(), 3).isEmpty()) {
            recommendations.add("Take a revision quiz to retry recently incorrect questions and review explanations immediately.");
        }

        recommendations.add("Maintain consistency by taking short quizzes regularly so the adaptive engine can refine your skill profile.");
        return recommendations;
    }
}
