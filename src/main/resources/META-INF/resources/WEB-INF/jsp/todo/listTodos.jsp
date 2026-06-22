<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sm.coursera.todo.Todo, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Your Todos</title>
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
    <div class="card card--wide">
        <span class="pill">Signed in</span>
        <%-- ${name} comes from the session (set at login via @SessionAttributes). --%>
        <h1 class="card__title" style="margin-top:.75rem;">${name}'s todos</h1>
        <p class="card__subtitle">Your tasks and their target dates.</p>

        <table class="table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Description</th>
                    <th>Target date</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <%-- The model attribute "todos" is a List<Todo> from TodoController.
                     A scriptlet loop renders one row per item (no JSTL needed). --%>
                <% for (Todo todo : (List<Todo>) request.getAttribute("todos")) { %>
                    <tr>
                        <td><%= todo.getId() %></td>
                        <td><%= todo.getDescription() %></td>
                        <td><%= todo.getTargetDate() %></td>
                        <td>
                            <% if (todo.getDone()) { %>
                                <span class="badge badge--done">Done</span>
                            <% } else { %>
                                <span class="badge badge--pending">Pending</span>
                            <% } %>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>

        <a class="link" href="/login">&larr; Sign out</a>
    </div>
</body>
</html>