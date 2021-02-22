<!-- Using the variable declared in looking-and-feel.xml -->
<#if  twitter_icon>
    <ul class="list-inline mb-0">
        <#if twitter_icon>
            <li class="list-inline-item">
                <a class="bg-white bg-white-10 d-block icon-social icon-text-info rounded-circle text-center text-decoration-none text-info" href="${twitter_icon_link_url}" rel="external" target="_blank" title="Go to our Twitter (in new window)">
                    <span class="icon-twitter"></span>
                </a>
            </li>
        </#if>
    </ul>
</#if>