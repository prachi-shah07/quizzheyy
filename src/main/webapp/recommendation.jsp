<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Recommendations | AI Personalized Quiz System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="page-shell">
    <div class="topbar">
        <div>
            <span class="eyebrow">Adaptive Support</span>
            <h1>Personalized Recommendations</h1>
        </div>
        <div class="actions">
            <a class="button secondary" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a class="button primary" href="${pageContext.request.contextPath}/quiz">New Quiz</a>
        </div>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="message error">${errorMessage}</div>
    </c:if>

    <div class="panel">
        <c:choose>
            <c:when test="${empty recommendations}">
                <p class="muted">No recommendations available yet. Take a quiz to generate guidance.</p>
            </c:when>
            <c:otherwise>
                <ul class="recommendation-list">
                    <c:forEach items="${recommendations}" var="item">
                        <li>${item}</li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
