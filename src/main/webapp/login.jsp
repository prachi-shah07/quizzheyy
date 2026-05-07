<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login | AI Personalized Quiz System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="page-shell auth-shell">
    <div class="panel auth-panel">
        <div>
            <span class="eyebrow">Adaptive Learning Portal</span>
            <h1>AI Personalized Quiz System</h1>
            <p class="muted">Sign in to continue your adaptive quiz journey.</p>
        </div>

        <c:if test="${param.registered eq 'true'}">
            <div class="message success">Registration complete. You can log in now.</div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="message error">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" class="form-grid">
            <label>Email
                <input type="email" name="email" placeholder="student@quizapp.com" required>
            </label>
            <label>Password
                <input type="password" name="password" placeholder="Enter password" required>
            </label>
            <button type="submit" class="button primary">Login</button>
        </form>

        <p class="muted">New here? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
    </div>
</div>
</body>
</html>
