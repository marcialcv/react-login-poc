<%@ include file="/init.jsp" %>

<p>
	<b><liferay-ui:message key="newsilentloginjava.caption"/></b>
</p>

<portlet:renderURL var="callAPIRender">
	<portlet:param name="mvcRenderCommandName" value="/new_silent_login_java/call_api" />
</portlet:renderURL>

<aui:button href="<%= callAPIRender %>" value="Call API" />

