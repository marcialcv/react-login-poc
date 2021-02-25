<!-- recover the variable values declared in looking-and-feel.xml -->
<#assign
	show_footer = getterUtil.getBoolean(themeDisplay.getThemeSetting("show-footer"))
	show_header = getterUtil.getBoolean(themeDisplay.getThemeSetting("show-header"))
	show_header_search = getterUtil.getBoolean(themeDisplay.getThemeSetting("show-header-search"))
	wrap_widget_page_content = getterUtil.getBoolean(themeDisplay.getThemeSetting("wrap-widget-page-content"))
	show_navbar_by_theme = getterUtil.getBoolean(themeDisplay.getThemeSetting("show-navbar-by-theme"))
	twitter_icon = getterUtil.getBoolean(themeDisplay.getThemeSetting("twitter-icon"))
	twitter_icon_link_url = themeDisplay.getThemeSetting("twitter-icon-link-url")

/>

<!-- we set the variable "portal_content_css_class" to a section tag in portal_normal.ftl to apply or not a CSS class -->
<#if wrap_widget_page_content && ((layout.isTypeContent() && themeDisplay.isStateMaximized()) || (layout.getType() == "portlet"))>
	<#assign portal_content_css_class = "container" />
<#else>
	<#assign portal_content_css_class = "" />
</#if>