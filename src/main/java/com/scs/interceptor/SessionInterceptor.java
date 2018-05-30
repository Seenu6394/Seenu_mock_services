package com.scs.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.scs.exception.AuthorizationException;
import com.scs.util.SessionUtil;
import com.scs.util.StorageConstants;

public class SessionInterceptor  extends HandlerInterceptorAdapter {
	private static final Logger logger = Logger.getLogger(SessionInterceptor.class);
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AuthorizationException  {
		logger.debug("SESSION INTERCEPTOR RESPONSE START");
		if(!SessionUtil.objectExists(request.getSession(),  StorageConstants.LOGIN_RESPONSE)){
			throw new AuthorizationException("Invalid Request");
		}else{
//			User user = SessionUtil.getValue(request.getSession(), StorageConstants.user);
//			if (Utility.checkNullEmpty(user.getUserId())) {
//				for (SessionInformation sessionInfo : sessionRegistry.getAllSessions(user.getUserId(), true)) {
//					if (sessionInfo.isExpired() && sessionInfo.getSessionId().equals(SessionUtil.getSessionId(request.getSession()))) {
//						logger.error("Session expired");
//						request.getSession().invalidate();
//						throw new AuthorizationException("Sorry! You seem to have logged in from other device.");
//					}
//				}
//			}
		}
		logger.debug("well = "+StorageConstants.LOGIN_RESPONSE.toString());
		return true;
	}
}
