package com.liferay.samples.fbo.oidc.callback;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.samples.fbo.oidc.providerinfo.OIDCProviderInfoServlet;

@Component(
		immediate = true,
		property = {
			"osgi.http.whiteboard.context.path=/",
			"osgi.http.whiteboard.servlet.pattern=/oidc/callback/*"
		},
		service = Servlet.class
	)
public class OIDCCallbackServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
		if (_log.isInfoEnabled()) {
			_log.info("OIDC Callback Servlet init");
		}

		super.init();
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_log.info("User ID: " + PortalUtil.getUserId(httpServletRequest));
		_log.info("Company ID: " + PortalUtil.getCompanyId(httpServletRequest));
		
		if (_log.isInfoEnabled()) {
			_log.info("doGet");
		}

		_writeCallbackScript(httpServletRequest, httpServletResponse);
	}

	/**
	 * Callback management
	 *
	 * @return browser side callback management script
	 */
	private String _generateHTML(String clientId, String idProvider, String code) {

		
		StringBuffer sb = new StringBuffer();

		sb.append("<html>");
		sb.append("<head><title>OIDC Callback manager</title></head>");
		sb.append("<body>");
		sb.append("<script type=\"text/javascript\">");
		sb.append("  console.log('window.location = ' + window.location);");
		sb.append("  console.log('window.parent.location = ' + window.parent.location);");
		sb.append("  console.log('document.referrer = ' + document.referrer);");
		sb.append("  console.log('document.location.href = ' + document.location.href);");
		sb.append("  var origin = (window.location != window.parent.location) ? window.location.origin : document.location.href;");
		sb.append("  console.log('Origin value = ' + origin);");
		sb.append("  var urlParams = new URLSearchParams(window['location'].search);");
		sb.append("  var error = urlParams.get(\"error\");");
		sb.append("  var code = urlParams.get(\"code\");");
		sb.append("  var json_obj, status = false;");
		sb.append("  console.log('error value = ' + error);");
		sb.append("  console.log('code value = ' + code);");
		sb.append("  if (error === 'login_required' || error === 'interaction_required') {");
		sb.append("    ");
		sb.append("    var message = {");
		sb.append("      client_id: \"" + clientId + "\",");
		sb.append("      id_provider: \"" + idProvider + "\",");
		sb.append("      interaction_required: true");
		sb.append("    };");
		sb.append("  	console.log('if 1 message value = ' + message);");
		sb.append("  } else {");
		sb.append("    var message = {");
		sb.append("      client_id: \"" + clientId + "\",");
		sb.append("      id_provider: \"" + idProvider + "\",");
		sb.append("      code: \"" + code + "\"");
		sb.append("    };");
		sb.append("  	console.log('else 2 message value = ' + message);");
		sb.append("  }");
		sb.append("  window.parent.postMessage(message, origin);");
		sb.append("</script>");
		sb.append("</body>");
		sb.append("</html>");

		return new String(sb);
	}

	/**
	 * Write HTML
	 *
	 * @param httpServletResponse
	 */
	private void _writeCallbackScript(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.TEXT_HTML_UTF8);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		try {

			String[] split = httpServletRequest.getPathInfo().split("/");
			
			String clientId = split[2];//"medfile-react-client-app";
			String idProvider = split[1];//"oi";
			String code = ParamUtil.get(httpServletRequest, "code", "");

			_log.info("Client ID: " + clientId);
			_log.info("ID Provider: " + idProvider);
			_log.info("Authorization code: " + code);
			
			ServletResponseUtil.write(
				httpServletResponse, _generateHTML(clientId, idProvider, code));
		}
		catch (Exception e) {
			_log.warn(e.getMessage(), e);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_PRECONDITION_FAILED);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(OIDCProviderInfoServlet.class);

	private static final long serialVersionUID = 1L;

}