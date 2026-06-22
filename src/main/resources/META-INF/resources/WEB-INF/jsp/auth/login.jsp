<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login</title>
    <!-- Reusable stylesheet served from src/main/resources/static/css/ -->
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
    <div class="card">
        <h1 class="card__title">Welcome back</h1>
        <p class="card__subtitle">Sign in to continue.</p>

        <!-- Shown only when AuthController puts an "error" in the model.
             EL-only (no JSTL needed): hidden while ${error} is empty. -->
        <div class="alert" style="${empty error ? 'display:none' : ''}">${error}</div>

        <!-- Posts to /login (AuthController.submitLogin), which validates via
             AuthService. method=post keeps the password out of the URL. -->
        <form class="form" action="/login" method="post">
            <div class="form-group">
                <label for="name">Name</label>
                <input class="field" type="text" id="name" name="name"
                       value="${name}" placeholder="Your name" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input class="field" type="password" id="password" name="password"
                       placeholder="••••••••" required>
            </div>

            <button class="btn" type="submit">Sign in</button>
        </form>
    </div>
</body>
</html>