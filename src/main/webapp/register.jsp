<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register | AI Personalized Quiz System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="page-shell auth-shell">
    <div class="panel auth-panel">
        <div>
            <span class="eyebrow">Create Student Profile</span>
            <h1>Register</h1>
            <p class="muted">Create an account to begin personalized quizzes and recommendations.</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="message error">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post" class="form-grid">
            <label>Full Name
                <input type="text" name="fullName" placeholder="Enter your name" required>
            </label>
            <label>Email
                <input type="email" name="email" placeholder="you@example.com" required>
            </label>
            <label>Password
                <input type="password" name="password" placeholder="Create password" required>
            </label>
            <button type="submit" class="button primary">Register</button>
        </form>

        <p class="muted">Already registered? <a href="${pageContext.request.contextPath}/login">Back to login</a></p>
    </div>
</div>
</body>
</html>
