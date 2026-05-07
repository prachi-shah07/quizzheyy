<%
    Integer userId = (Integer) session.getAttribute("loggedInUserId");
    if (userId != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
