<%@ include file="/init.jsp" %>

<div id="<portlet:namespace />"></div>
<aui:script require="<%= mainRequire %>">
	main.default('#<portlet:namespace />', {accessToken:"<%= accessToken %>"});
</aui:script>
