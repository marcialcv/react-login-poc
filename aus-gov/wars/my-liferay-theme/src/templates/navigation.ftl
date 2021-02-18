<button aria-controls="navigationCollapse" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler navbar-toggler-right" data-target="#navigationCollapse" data-toggle="liferay-collapse" type="button">
		<span class="navbar-toggler-icon"></span>
</button>

   <#assign preferences = freeMarkerPortletPreferences.getPreferences({
	   "displayStyle": "ddmTemplate_WD_Basic_Menu",
	   "displayDepth": "0",
	   "rootMenuItemType": "absolute",
	   "siteNavigationMenuType": "-1",
	   "expandedLevels": "auto",
	   "rootMenuItemLevel": "0",
	   "displayStyleGroupId": "${group_id}",
	   "portletSetupPortletDecoratorId": "borderless", "
	   portletSetupUseCustomTitle":"false"}) />


<div class="collapse navbar-collapse" id="navigationCollapse">
	<@liferay.navigation_menu default_preferences="${preferences}" instance_id="main_navigation_menu" />
</div>
