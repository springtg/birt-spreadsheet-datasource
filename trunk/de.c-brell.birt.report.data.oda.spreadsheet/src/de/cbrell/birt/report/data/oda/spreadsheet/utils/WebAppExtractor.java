package de.cbrell.birt.report.data.oda.spreadsheet.utils;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.birt.report.engine.api.EngineConstants;

public class WebAppExtractor {
	
	private static final String WEB_PARAM_SPREADSHEET_HOME_FOLDER_ACCESS_ONLY ="SPREADSHEET_HOME_FOLDER_ACCESS_ONLY";
	private static final String WEB_PARAM_SPREADSHEET_HOME_FOLDER = "SPREADSHEET_HOME_FOLDER";
	
	private static ServletContext sc;
	
	public static void initialize(Map<String, Object> appContext) {
		if(sc==null && appContext!=null) {
			Object req = appContext.get(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST);
			if(req!=null) {
				HttpServletRequest request = (HttpServletRequest)req;
				HttpSession session = request.getSession();
				if(session!=null) {
					sc = session.getServletContext();
				}
			}
		}
	}
	
	private static String getWebAppParam(String param) {
		if(sc!=null) {
			return sc.getInitParameter(param);
		}
		return null;
	}
	
	public static Boolean getAccessHomeFolderOnly() {
		if(sc!=null) {
			String webAppParam = getWebAppParam(WEB_PARAM_SPREADSHEET_HOME_FOLDER_ACCESS_ONLY);
			if(webAppParam!=null) {
				return Boolean.valueOf(webAppParam);
			}
		}
		return null;
	}
	
	public static String getSpreadsheetHomeFolderPath() {
		String homeFolder = getWebAppParam(WEB_PARAM_SPREADSHEET_HOME_FOLDER);
		if(homeFolder!=null) {
			return sc.getRealPath(homeFolder);
		}
		return null;
	}

}
