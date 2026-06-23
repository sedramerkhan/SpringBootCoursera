<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jspf" %>
<%@ include file="../common/navbar.jspf" %>
    <div class="card">
        <span class="pill">Signed in</span>
        <h1 class="card__title" style="margin-top:.75rem;">Hello, ${name}!</h1>
        <p class="card__subtitle">Here is what you submitted on the login form.</p>

        <!-- ${name} / ${password} come from the ModelMap in AuthController. -->
        <div class="kv">
            <span class="kv__key">Name</span>
            <span class="kv__value">${name}</span>
        </div>
        <div class="kv">
            <span class="kv__key">Password</span>
            <span class="kv__value">${password}</span>
        </div>

        <p class="muted" style="margin-top:1rem;">
            Note: showing a password back on screen is for this demo only —
            real apps never display or store passwords in plain text.
        </p>

        <div style="margin-top:1.5rem; display:flex; gap:1.25rem; align-items:center;">
            <a class="btn" href="/todos" style="text-decoration:none; text-align:center;">View my todos</a>
            <a class="link" href="/login" style="margin-top:0;">&larr; Back to login</a>
        </div>
    </div>
<%@ include file="../common/footer.jspf" %>