package com.quizapp.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkillProfile {

    private final Map<String, TopicMetrics> topicMetrics = new LinkedHashMap<>();

    public Map<String, TopicMetrics> getTopicMetrics() {
        return topicMetrics;
    }

    public void addMetric(TopicMetrics metrics) {
        topicMetrics.put(metrics.getTopic(), metrics);
    }

    public List<String> getWeakTopics() {
        List<String> topics = new ArrayList<>();
        for (TopicMetrics metric : topicMetrics.values()) {
            if (metric.getCompositeScore() < 0.50) {
                topics.add(metric.getTopic());
            }
        }
        return topics;
    }

    public List<String> getMediumTopics() {
        List<String> topics = new ArrayList<>();
        for (TopicMetrics metric : topicMetrics.values()) {
            if (metric.getCompositeScore() >= 0.50 && metric.getCompositeScore() < 0.80) {
                topics.add(metric.getTopic());
            }
        }
        return topics;
    }

    public List<String> getStrongTopics() {
        List<String> topics = new ArrayList<>();
        for (TopicMetrics metric : topicMetrics.values()) {
            if (metric.getCompositeScore() >= 0.80) {
                topics.add(metric.getTopic());
            }
        }
        return topics;
    }

    public double getAverageCompositeScore() {
        if (topicMetrics.isEmpty()) {
            return 0.50;
        }
        return topicMetrics.values().stream()
                .mapToDouble(TopicMetrics::getCompositeScore)
                .average()
                .orElse(0.50);
    }

    public List<TopicMetrics> getMetricsSortedByWeakness() {
        List<TopicMetrics> metrics = new ArrayList<>(topicMetrics.values());
        metrics.sort(Comparator.comparingDouble(TopicMetrics::getCompositeScore));
        return metrics;
    }

    public static class TopicMetrics {
        private String topic;
        private double accuracy;
        private double speed;
        private double consistency;
        private double compositeScore;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(double accuracy) {
            this.accuracy = accuracy;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public double getConsistency() {
            return consistency;
        }

        public void setConsistency(double consistency) {
            this.consistency = consistency;
        }

        public double getCompositeScore() {
            return compositeScore;
        }

        public void setCompositeScore(double compositeScore) {
            this.compositeScore = compositeScore;
        }
    }
}
