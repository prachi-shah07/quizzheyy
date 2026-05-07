package com.quizapp.service;

import com.quizapp.dao.PerformanceDAO;

public class PerformanceAnalysisThread extends Thread {

    private final int userId;
    private final PerformanceDAO performanceDAO;

    public PerformanceAnalysisThread(int userId, PerformanceDAO performanceDAO) {
        super("PerformanceAnalysis-" + userId);
        this.userId = userId;
        this.performanceDAO = performanceDAO;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            synchronized (("PERF_LOCK_" + userId).intern()) {
                performanceDAO.refreshPerformanceForUser(userId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
