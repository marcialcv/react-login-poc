package com.liferay.com.portlet.render;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.com.model.Person;
import com.liferay.com.constants.NewSilentLoginJavaPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectSession;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + NewSilentLoginJavaPortletKeys.NEWSILENTLOGINJAVA,
                "mvc.command.name=/new_silent_login_java/call_api"
        },
        service = MVCRenderCommand.class
)
public class NewSilentLoginJavaRender implements MVCRenderCommand {
    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

        HttpSession session = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)).getSession();

        String token = getToken(session);
        String urlLogin = _portal.getPortalURL(PortalUtil.getHttpServletRequest(renderRequest)).concat("/c/portal/login");

        if (Validator.isNotNull(token)) {
            String urlAPI = "https://webserver-lfrspaings-prd.lfr.cloud/o/custom-api/users/123";

            try {
                URL obj = new URL(urlAPI);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(500);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                _log.info("Request URL ... " + urlAPI);

                int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {

                        ObjectMapper mapper = new ObjectMapper();
                        Person person = mapper.readValue(inputLine, Person.class);

                        renderRequest.setAttribute("firstName", person.getFirstName());
                        renderRequest.setAttribute("person_id", person.getPerson_id());

                        _log.info("API Content... \n firstName: " + person.getFirstName() + " and person ID " + person.getPerson_id());

                    }

                    in.close();
                    _log.info("Done");
                }
            } catch (IOException exception) {

            }

        }else{
            renderRequest.setAttribute("urlLogin", urlLogin);
            return "/login_warn.jsp";
        }
        return "/call_api.jsp";
    }
    public static String getToken(HttpSession httpSession) {
        return (httpSession.getAttribute(OPEN_ID_CONNECT_SESSION) instanceof OpenIdConnectSession) ? ((OpenIdConnectSession) httpSession.getAttribute(OPEN_ID_CONNECT_SESSION)).getAccessTokenValue(): null;
    }

    @Reference
    private Portal _portal;

    private static final Log _log = LogFactoryUtil.getLog(NewSilentLoginJavaRender.class);

}
