<ul class="flex-row nav" role="menubar">
<!-- this variables used here, like "is_sign_in, sign_in_url, sign_text, nav_items" are declarad inside build/init.ftl -->
	<#if !is_signed_in>
		<li class="nav-item" role="presentation">
			<a class="nav-link" data-redirect="${is_login_redirect_required?string}" href="${sign_in_url}" id="sign-in" rel="nofollow">
				${sign_in_text}
			</a>
		</li>
	</#if>

<!-- adding URL from existing Pages to footer  -->
<#foreach nav_item in nav_items>
	<li class="nav-item" role="presentation">
		<a class="nav-link" aria-labelledby="layout_${nav_item.getLayoutId()}" href="${nav_item.getURL()}" ${nav_item.getTarget()} role="menuitem">
			${nav_item.getName()}
		</a>
	</li>
</#foreach>
</ul>