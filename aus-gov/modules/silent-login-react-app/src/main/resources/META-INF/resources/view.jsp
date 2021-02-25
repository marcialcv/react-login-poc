<%@ include file="/init.jsp" %>

<div class="custom-background-application" id="<portlet:namespace />-root"></div>

<aui:script require="<%= mainRequire %>">
	main.default('<portlet:namespace />-root');
</aui:script>