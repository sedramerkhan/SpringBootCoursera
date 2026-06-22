<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- JSTL core tags. The jakarta.* uri matches the Jakarta EE JSTL dependency. --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
                <%-- "todos" is the List<Todo> from TodoController. c:forEach
                     renders one row per item; EL reads each property. --%>
                <c:forEach var="todo" items="${todos}">
                    <tr>
                        <td>${todo.id}</td>
                        <td><c:out value="${todo.description}"/></td>
                        <td>${todo.targetDate}</td>
                        <td>
                            <c:choose>
                                <c:when test="${todo.done}">
                                    <span class="badge badge--done">Done</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge--pending">Pending</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div style="margin-top:1.5rem; display:flex; gap:1.25rem; align-items:center;">
            <a class="btn" href="/todos/add" style="text-decoration:none; text-align:center;">+ Add todo</a>
            <a class="link" href="/login" style="margin-top:0;">&larr; Sign out</a>
        </div>
    </div>
</body>
</html>