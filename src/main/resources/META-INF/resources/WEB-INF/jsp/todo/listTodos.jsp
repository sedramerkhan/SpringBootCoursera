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
                    <th></th>
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
                        <td>
                            <%-- POST form per row so delete isn't a plain GET link. --%>
                            <form action="/todos/${todo.id}/delete" method="post" style="margin:0;">
                                <button type="submit" class="btn-icon" aria-label="Delete todo" title="Delete">
                                    <%-- Inline SVG trash icon — no icon library needed. --%>
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                                         stroke="currentColor" stroke-width="2"
                                         stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="3 6 5 6 21 6"></polyline>
                                        <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path>
                                        <path d="M10 11v6M14 11v6"></path>
                                        <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path>
                                    </svg>
                                </button>
                            </form>
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