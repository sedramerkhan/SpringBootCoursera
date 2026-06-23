<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jspf" %>
<%@ include file="../common/navbar.jspf" %>
    <div class="card">
        <span class="pill">Signed in</span>
        <h1 class="card__title" style="margin-top:.75rem;">Hello, ${name}!</h1>
        <p class="card__subtitle">You are signed in with Spring Security.</p>

        <%-- ${name} is the authenticated principal, added by AuthController.
             We no longer have (or show) the raw password — Spring Security
             only ever sees its hash. --%>

        <div style="margin-top:1.5rem;">
            <a class="btn" href="/todos" style="text-decoration:none; text-align:center;">View my todos</a>
        </div>
    </div>
<%@ include file="../common/footer.jspf" %>