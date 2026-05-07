package com.quizapp.util;

import com.quizapp.model.Performance;
import com.quizapp.model.SkillProfile;

import java.util.List;

public final class SkillProfileUtil {

    private SkillProfileUtil() {
    }

    public static SkillProfile buildSkillProfile(List<Performance> performanceRecords) {
        SkillProfile skillProfile = new SkillProfile();
        for (Performance performance : performanceRecords) {
            SkillProfile.TopicMetrics metrics = new SkillProfile.TopicMetrics();
            metrics.setTopic(performance.getTopic());
            metrics.setAccuracy(performance.getAccuracy());
            metrics.setSpeed(performance.getSpeed());
            metrics.setConsistency(performance.getConsistency());
            metrics.setCompositeScore(performance.getCompositeScore());
            skillProfile.addMetric(metrics);
        }
        return skillProfile;
    }
}
