<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jspf" %>
    <div class="card">
        <h1 class="card__title">Welcome back</h1>
        <p class="card__subtitle">Sign in to continue.</p>

        <%-- Spring Security redirects to /login?error on a failed sign-in and
             /login?logout after logout. EL-only banners (no JSTL needed):
             hidden unless the matching query param is present. --%>
        <div class="alert" style="${empty param.error ? 'display:none' : ''}">
            Invalid name or password. Please try again.
        </div>
        <div class="alert alert--ok" style="${empty param.logout ? 'display:none' : ''}">
            You have been logged out.
        </div>

        <%-- Posts to /login, which Spring Security's filter intercepts and
             authenticates (it expects the "username" and "password" params).
             The _csrf hidden field is required because CSRF protection is on. --%>
        <form class="form" action="/login" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-group">
                <label for="username">Name</label>
                <input class="field" type="text" id="username" name="username"
                       placeholder="Your name" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input class="field" type="password" id="password" name="password"
                       placeholder="••••••••" required>
            </div>

            <button class="btn" type="submit">Sign in</button>
        </form>
    </div>
<%@ include file="../common/footer.jspf" %>