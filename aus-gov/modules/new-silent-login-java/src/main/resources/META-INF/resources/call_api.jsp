<%@ include file="/init.jsp" %>

<%
String firstName = (String)request.getAttribute("firstName");
String person_id = (String)request.getAttribute("person_id");
%>
    <div>
            <h1> Hello: <%= firstName%></h1>
            <br>
            <h2>Your Id is: <%= person_id%></h2>
    </div>
