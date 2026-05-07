<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quiz | AI Personalized Quiz System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="page-shell">
    <div class="topbar">
        <div>
            <span class="eyebrow">Live Adaptive Quiz</span>
            <h1>${quiz.targetedDifficulty} Quiz</h1>
            <p class="muted">The next question changes immediately based on your last answer. Four correct in a row makes the next question harder, while a wrong answer makes the next question easier.</p>
        </div>
        <div class="panel timer-box">
            <span class="eyebrow">Question Timer</span>
            <strong id="questionTimer">${isReviewMode ? 'Answered' : '01:00'}</strong>
            <div class="muted">Question ${questionNumber} of ${maxQuestions}</div>
        </div>
    </div>

    <c:if test="${not empty adaptiveSession.lastAdaptiveMessage}">
        <div class="message success">${adaptiveSession.lastAdaptiveMessage}</div>
    </c:if>

    <c:if test="${isReviewMode}">
        <div class="message success">You are reviewing an already answered question. Use Next to return to the current adaptive question.</div>
    </c:if>

    <c:set var="currentAnswer" value="${adaptiveSession.submittedAnswers[currentQuestion.id]}" />

    <div class="panel">
        <form id="quizProgressForm" action="${pageContext.request.contextPath}/quiz-progress" method="post">
            <input type="hidden" id="elapsedSeconds" name="elapsedSeconds" value="0">

            <div class="question-card active-question">
                <div class="question-meta">
                    <span>Question ${questionNumber}</span>
                    <span>${currentQuestion.topic}</span>
                    <span>Difficulty ${currentQuestion.difficultyLevel}</span>
                    <span>${quiz.perQuestionTimeLimitSeconds} seconds</span>
                </div>
                <h3>${currentQuestion.questionText}</h3>

                <label class="option-row">
                    <input type="radio" name="answer" value="A" ${currentAnswer eq 'A' ? 'checked' : ''} ${isReviewMode ? 'disabled' : ''}>
                    <span>A. ${currentQuestion.optionA}</span>
                </label>
                <label class="option-row">
                    <input type="radio" name="answer" value="B" ${currentAnswer eq 'B' ? 'checked' : ''} ${isReviewMode ? 'disabled' : ''}>
                    <span>B. ${currentQuestion.optionB}</span>
                </label>
                <label class="option-row">
                    <input type="radio" name="answer" value="C" ${currentAnswer eq 'C' ? 'checked' : ''} ${isReviewMode ? 'disabled' : ''}>
                    <span>C. ${currentQuestion.optionC}</span>
                </label>
                <label class="option-row">
                    <input type="radio" name="answer" value="D" ${currentAnswer eq 'D' ? 'checked' : ''} ${isReviewMode ? 'disabled' : ''}>
                    <span>D. ${currentQuestion.optionD}</span>
                </label>

                <c:if test="${isReviewMode and currentAnswer eq 'Not Answered'}">
                    <p class="muted"><strong>Status:</strong> Not Answered</p>
                </c:if>
            </div>

            <div class="quiz-nav">
                <c:if test="${questionNumber > 1}">
                    <button class="button ghost" type="submit" name="action" value="previous">Previous</button>
                </c:if>

                <c:choose>
                    <c:when test="${questionNumber < totalQuestionsSoFar}">
                        <button class="button secondary" type="submit" name="action" value="next">Next</button>
                    </c:when>
                    <c:when test="${questionNumber == maxQuestions}">
                        <button class="button primary" type="submit" name="action" value="submit">Submit Quiz</button>
                    </c:when>
                    <c:otherwise>
                        <button class="button primary" type="submit" name="action" value="next">Next Question</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </form>
    </div>
</div>

<script>
    const isReviewMode = ${isReviewMode ? 'true' : 'false'};
    const perQuestionLimit = ${quiz.perQuestionTimeLimitSeconds};
    const isLastQuestion = ${questionNumber == maxQuestions ? 'true' : 'false'};
    const timerElement = document.getElementById('questionTimer');
    const elapsedElement = document.getElementById('elapsedSeconds');
    const quizForm = document.getElementById('quizProgressForm');
    let elapsed = 0;
    let timerId = null;

    function renderTimer() {
        const remaining = Math.max(0, perQuestionLimit - elapsed);
        const minutes = String(Math.floor(remaining / 60)).padStart(2, '0');
        const seconds = String(remaining % 60).padStart(2, '0');
        timerElement.textContent = minutes + ':' + seconds;
    }

    function autoAdvance() {
        const hiddenAction = document.createElement('input');
        hiddenAction.type = 'hidden';
        hiddenAction.name = 'action';
        hiddenAction.value = isLastQuestion ? 'submit' : 'next';
        quizForm.appendChild(hiddenAction);
        quizForm.submit();
    }

    if (!isReviewMode) {
        renderTimer();
        timerId = setInterval(function() {
            elapsed += 1;
            elapsedElement.value = elapsed;
            renderTimer();
            if (elapsed >= perQuestionLimit) {
                clearInterval(timerId);
                autoAdvance();
            }
        }, 1000);

        quizForm.addEventListener('submit', function() {
            if (timerId) {
                clearInterval(timerId);
            }
            elapsedElement.value = elapsed;
        });
    }
</script>
</body>
</html>
