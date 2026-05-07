<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Result | AI Personalized Quiz System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="page-shell">
    <div class="topbar">
        <div>
            <span class="eyebrow">Quiz Outcome</span>
            <h1>Your Result</h1>
        </div>
        <div class="actions">
            <a class="button secondary" href="${pageContext.request.contextPath}/dashboard">Back to Dashboard</a>
            <a class="button primary" href="${pageContext.request.contextPath}/recommendations">View Recommendations</a>
        </div>
    </div>

    <c:if test="${not empty timerMessage}">
        <div class="message error">${timerMessage}</div>
    </c:if>

    <div class="card-grid">
        <div class="panel stat-card">
            <span class="eyebrow">Score</span>
            <h2><fmt:formatNumber value="${result.percentageScore}" maxFractionDigits="1"/>%</h2>
            <p class="muted">${result.correctAnswers} out of ${result.totalQuestions} correct</p>
        </div>
        <div class="panel stat-card">
            <span class="eyebrow">Average Response Time</span>
            <h2><fmt:formatNumber value="${result.averageResponseTime}" maxFractionDigits="1"/> sec</h2>
            <p class="muted">${result.difficultyRecommendation}</p>
        </div>
        <div class="panel stat-card">
            <span class="eyebrow">Adaptive Insight</span>
            <p>${result.adaptiveMessage}</p>
        </div>
    </div>

    <div class="panel">
        <h2>Topic Accuracy Snapshot</h2>
        <div class="tag-group">
            <c:forEach items="${result.topicAccuracy}" var="entry">
                <span class="tag">${entry.key}: <fmt:formatNumber value="${entry.value}" maxFractionDigits="1"/>%</span>
            </c:forEach>
        </div>
    </div>

    <div class="panel">
        <h2>Question Review and Explanations</h2>
        <c:forEach items="${quiz.questions}" var="question" varStatus="status">
            <div class="question-card review-card">
                <div class="question-meta">
                    <span>Question ${status.index + 1}</span>
                    <span>${question.topic}</span>
                    <span class="${result.correctnessMap[question.id] ? 'status-good' : 'status-bad'}">
                        ${result.correctnessMap[question.id] ? 'Correct' : 'Incorrect'}
                    </span>
                </div>
                <h3>${question.questionText}</h3>
                <p><strong>Your Answer:</strong> ${result.submittedAnswers[question.id]}</p>
                <p><strong>Correct Answer:</strong> ${question.correctOption}</p>
                <p><strong>Explanation:</strong> ${question.explanation}</p>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>
