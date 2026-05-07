package com.quizapp.service;

public class TimerThread extends Thread {

    private final int quizId;
    private final int durationSeconds;
    private volatile int remainingSeconds;
    private volatile boolean expired;
    private volatile boolean cancelled;

    public TimerThread(int quizId, int durationSeconds) {
        super("QuizTimer-" + quizId);
        this.quizId = quizId;
        this.durationSeconds = durationSeconds;
        this.remainingSeconds = durationSeconds;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!cancelled && remainingSeconds > 0) {
            try {
                Thread.sleep(1000L);
                synchronized (this) {
                    remainingSeconds--;
                }
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        if (!cancelled) {
            expired = true;
        }
    }

    public synchronized int getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isExpired() {
        return expired;
    }

    public void cancelTimer() {
        cancelled = true;
        interrupt();
    }

    public int getQuizId() {
        return quizId;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }
}
