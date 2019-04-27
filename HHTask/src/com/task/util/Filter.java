package com.task.util;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.task.entity.Admin;
import com.task.entity.Users;

/*
 * 用户登录过滤处理
 */
public class Filter implements javax.servlet.Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();
		String contextPath = request.getContextPath();// 项目名称
		String host = request.getHeader("Referer");// 上一个页面的地址
		String url = request.getServletPath();// 当前请求地址
		String oldUrl = "index.jsp";

		/****************** 记录本次的访问方式 *****************/
		boolean isFromMobile = false;
		// 检查是否已经记录访问方式（移动端或pc端）
		if (null == session.getAttribute("mobileOrPc")) {
			try {
				// 获取ua，用来判断是否为移动端访问
				String userAgent = ((HttpServletRequest) servletRequest)
						.getHeader("USER-AGENT").toLowerCase();
				if (null == userAgent) {
					userAgent = "";
				}
				isFromMobile = Util.check(userAgent);
				// 判断是否为移动端访问
				if (isFromMobile) {
					session.setAttribute("mobileOrPc", "mobile");
				} else {
					session.setAttribute("mobileOrPc", "pc");
				}
			} catch (Exception e) {
			}
		}
		/****************** 记录本次的访问方式结束 *****************/

		if (url.indexOf("admin") > 0) {
			Admin admin = (Admin) session.getAttribute("admin");
			if (admin == null && !url.equals("/admin/login.jsp")) {
				// 跳过 不检查
				if (url.indexOf("images") > 0 || url.indexOf("css.css") > 0) {
					chain.doFilter(request, response);
					return;
				}

				response.sendRedirect(contextPath + "/admin/login.jsp");
				return;
			} else if (admin != null && url.startsWith("/admin/login.jsp")) {
				response.sendRedirect(contextPath + "/admin/" + oldUrl);
				return;
			}
		} else {
			Users user = (Users) session.getAttribute("Users");
			if (user == null) {
				// 跳过 不检查
				if (url.indexOf("vcode.jsp") > 0
						|| url.indexOf("tasklogo.jpg") > 0
						|| url.indexOf("loginforPhone.jsp") > 0
						|| url.indexOf("favicon.ico") > 0
						|| url.indexOf("logo.jpg") > 0
						|| url.indexOf("css/") > 0 || url.indexOf("js/") > 0
						|| url.indexOf("keyboard") > 0
						|| url.indexOf("error.gif") > 0
						|| url.indexOf("foot_03.jpg") > 0
						|| url.indexOf("upload/file/OsTemplate") > 0
						|| url.indexOf("upload/file/screenfile") > 0
						|| url.indexOf("javascript/jquery-1.8.3.js") > 0
						|| url.indexOf("upload/user") > 0
						|| url.indexOf("images/mk") > 0
						|| url.indexOf("javascript/kindeditor-master") > 0
						|| url.indexOf("javascript") > 0
						|| url.indexOf("bbs/showFailure") > 0
						|| url.indexOf("javascript/tubiao") > 0
						|| url.indexOf("upload/file/sysImages") > 0
						|| url.indexOf("upload/bbs") > 0
						|| url.indexOf("images") > 0 || url.indexOf("img") > 0
						|| url.indexOf("gysLogin.jsp") > 0
						|| url.indexOf("sjLogin.jsp") > 0
						|| url.indexOf("fxLogin.jsp") > 0
						|| url.indexOf("CompanyInfo") > 0
						|| url.indexOf("orderManager_monthView.jsp") > 0
						|| url.indexOf("orderManager_id_monthView.jsp") > 0
						|| url.indexOf("downloadapk.jsp") > 0
						|| url.indexOf("util/uploadlic.jsp") > 0
						|| url.indexOf("upload/file/download/") > 0
						|| url.indexOf("app.jsp") > 0
						|| url.indexOf("makebarcode.jsp") > 0
						|| url.indexOf("makeqrcode.jsp") > 0
						|| url.indexOf("/upload/file/") > 0
						|| url.indexOf("/processTz/") > 0
						|| url.indexOf("/plateTest") >= 0
						|| url.indexOf("/servlet/UploadServlet") >= 0
						|| url.indexOf("/servlet/WaterDianServlet") >= 0
						|| url.indexOf("otherLogin_float.jsp") >= 0
						|| url.indexOf("upload/gongyi") > 0
						|| url.indexOf("ComanyVIPadd1.jsp") > 0
						|| url.indexOf("ComanyVIPadd.jsp") > 0
						|| url.indexOf("cn_add.jsp") > 0
						|| url.indexOf("cn_hzshow.jsp") > 0
						|| url.indexOf("cn_show.jsp") > 0
						|| url.indexOf("cn_showlist.jsp") > 0
						|| url.indexOf("showScreen3_order.jsp") > 0
						|| url.indexOf("upload/file/daQRcode") > 0
						|| url.indexOf("bsps.jsp") > 0
						|| url.indexOf("Procard_gxjdck.jsp") > 0
						|| url.indexOf("showScreen4_order.jsp") > 0
						|| url.indexOf("screenContent_show.jsp") > 0
						|| url.indexOf("TestPrint.html") > 0
						|| url.indexOf("spc_show.jsp") > 0
						|| url.indexOf("ecar_show2.jsp") > 0
						|| url.indexOf("menjin/visitor/") > 0
						|| url.indexOf("echarts.js") > 0
						|| url.indexOf("upload/file/menjin/visitor") > 0
						|| url.indexOf("upload/file/cookbook") > 0
						|| url.equals("/s")) 
						{
					chain.doFilter(request, response);
					return;
				}
				String code = "";// 员工号
				String deptNum = "";// 部门编号
				String passwordvalue = "";// 密码
				String loginType = "";// 登录类型
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						Cookie cookie = cookies[i];
						if ("code".equals(cookie.getName())) {
							code = cookie.getValue();
						} else if ("deptNum".equals(cookie.getName())) {
							deptNum = cookie.getValue();
						} else if ("password".equals(cookie.getName())) {
							passwordvalue = cookie.getValue();
						} else if ("loginType".equals(cookie.getName())) {
							loginType = cookie.getValue();
						}
					}
				}
				if (code.length() > 1) {
					if ("sjLogin.jsp".equals(loginType)) {
						response
								.sendRedirect("UsersAction!jyLogin.action?user.code="
										+ code
										+ "&password.password="
										+ passwordvalue + "&autoLogin=yes");
					} else if ("fxLogin.jsp".equals(loginType)) {
						response
								.sendRedirect("UsersAction!jyLogin2.action?user.code="
										+ code
										+ "&password.password="
										+ passwordvalue + "&autoLogin=yes");
					} else {
						response
								.sendRedirect("UsersAction!login.action?user.code="
										+ code
										+ "&password.password="
										+ passwordvalue
										+ "&password.deptNumber="
										+ deptNum
										+ "&autoLogin=yes");
					}
					return;
				}
				int indexof = url.indexOf("/login.jsp");
				int otherindexof = url.indexOf("/otherLogin.jsp");
				int sjindexof = url.indexOf("/sjLogin.jsp");
				int fxindexof = url.indexOf("/fxLogin.jsp");
				if (indexof < 0 && otherindexof < 0) {
					response.sendRedirect(contextPath + "/login.jsp");
					return;
				} else if (sjindexof > 0) {
					response.sendRedirect(contextPath + "/sjLogin.jsp");
					return;
				} else if (fxindexof > 0) {
					response.sendRedirect(contextPath + "/fxLogin.jsp");
					return;
				} else if (indexof < 0 && otherindexof != 0) {
					response.sendRedirect(contextPath + "/login.jsp");
					return;
				} else if (otherindexof < 0 && url != null
						&& !"/login.jsp".equals(url)) {
					response.sendRedirect(contextPath + "/otherLogin.jsp");
					return;
				}

			} else if (user != null && url.equals("/login.jsp")
					|| url.equals("/index.jsp")
					|| url.equals("/otherLogin.jsp")) {
				response
						.sendRedirect("ModuleFunctionAction!findMfByUser.action");
				return;
			} else if (user != null && url.equals("/sjLogin.jsp")) {
				response.sendRedirect("UsersAction!sczzIndex.action");
				return;
			} else if (user != null && url.equals("/fxLogin.jsp")) {
				response.sendRedirect("attendClassAction_initaddLesson.action");
				return;
			}

		}
		chain.doFilter(request, response);

	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
