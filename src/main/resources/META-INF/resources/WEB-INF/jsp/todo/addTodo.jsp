<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add Todo</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
    <div class="card">
        <h1 class="card__title">Add a todo</h1>
        <p class="card__subtitle">${name}, what do you want to get done?</p>

        <!-- Posts to /todos/add (TodoController.addTodo), which saves it and
             redirects back to /todos. -->
        <form class="form" action="/todos/add" method="post">
            <div class="form-group">
                <label for="description">Description</label>
                <input class="field" type="text" id="description" name="description"
                       placeholder="e.g. Learn JSTL" required autofocus>
            </div>

            <div class="form-group">
                <label for="targetDate">Target date</label>
                <!-- type=date submits yyyy-MM-dd, which binds to LocalDate. -->
                <input class="field" type="date" id="targetDate" name="targetDate" required>
            </div>

            <button class="btn" type="submit">Add todo</button>
        </form>

        <a class="link" href="/todos">&larr; Back to list</a>
    </div>
</body>
</html>