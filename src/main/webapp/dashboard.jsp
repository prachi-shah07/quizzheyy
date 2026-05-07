<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard | AI Personalized Quiz System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="page-shell">
    <div class="topbar">
        <div>
            <span class="eyebrow">Student Dashboard</span>
            <h1>Welcome, <c:out value="${user.fullName}" default="Learner"/></h1>
        </div>
        <div class="actions">
            <a class="button secondary" href="${pageContext.request.contextPath}/recommendations">Recommendations</a>
            <a class="button ghost" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="message error">${errorMessage}</div>
    </c:if>

    <div class="card-grid">
        <div class="panel stat-card">
            <span class="eyebrow">Overall Accuracy</span>
            <h2><fmt:formatNumber value="${overallAccuracy}" maxFractionDigits="1"/>%</h2>
            <p class="muted">Average across your topic-level performance records.</p>
        </div>
        <div class="panel stat-card">
            <span class="eyebrow">Adaptive Actions</span>
            <div class="stack">
                <a class="button primary" href="${pageContext.request.contextPath}/quiz">Start Adaptive Quiz</a>
                <a class="button secondary" href="${pageContext.request.contextPath}/quiz?mode=revision">Start Revision Quiz</a>
            </div>
        </div>
        <div class="panel stat-card">
            <span class="eyebrow">Latest Score</span>
            <c:choose>
                <c:when test="${not empty lastResult}">
                    <h2><fmt:formatNumber value="${lastResult.percentageScore}" maxFractionDigits="1"/>%</h2>
                    <p class="muted">${lastResult.adaptiveMessage}</p>
                </c:when>
                <c:otherwise>
                    <h2>--</h2>
                    <p class="muted">Take a quiz to generate your first personalized result.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="panel">
        <h2>Topic Analytics</h2>
        <c:choose>
            <c:when test="${empty performanceList}">
                <p class="muted">No analytics yet. Complete a quiz to create your adaptive skill profile.</p>
            </c:when>
            <c:otherwise>
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Topic</th>
                        <th>Accuracy</th>
                        <th>Speed</th>
                        <th>Consistency</th>
                        <th>Composite Score</th>
                        <th>Avg Response Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${performanceList}" var="item">
                        <tr>
                            <td>${item.topic}</td>
                            <td><fmt:formatNumber value="${item.accuracy * 100}" maxFractionDigits="1"/>%</td>
                            <td><fmt:formatNumber value="${item.speed * 100}" maxFractionDigits="1"/>%</td>
                            <td><fmt:formatNumber value="${item.consistency * 100}" maxFractionDigits="1"/>%</td>
                            <td><fmt:formatNumber value="${item.compositeScore * 100}" maxFractionDigits="1"/>%</td>
                            <td><fmt:formatNumber value="${item.averageResponseTime}" maxFractionDigits="1"/> sec</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
