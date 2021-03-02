<%@ include file="/init.jsp" %>

<%
 String urlLogin = (String)request.getAttribute("urlLogin");
%>
 <div>
 <aui:button href="<%= urlLogin %>" value="You need to Login with OpenId!" />
</div>