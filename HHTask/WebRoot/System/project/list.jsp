<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: thin solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; padding-left: 35px; padding-top: 5px; padding-bottom: 5px; "
				align="left">
				<div style="float: left; width: 50%" align="left">
					
				</div>
				<div style="float: left; width: 48%" align="right">
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">刷新</a>
				</div>
			</div>
			
			<div align="center">
				<table class="table">
					<tr  bgcolor="#c0dcf2" height="50px">
						<td align="center">序号</td>
						<td align="center">项目名称</td>
						<td align="center">项目编号</td>
						<td align="center">创建人</td>
						<td align="center">创建日期</td>
						<td align="center">是否完成</td>
						<td align="center">状态</td>
						<td align="center">其它操作</td>
					</tr>
					<s:iterator id="p" value="projects" status="st">
						<tr>
							<td>${st.index + 1}</td>
							<td>${p.name}</td>
							<td>${p.numb}</td>
							<td>${p.createPerson}</td>
							<td>${p.createDate}</td>
							<td>
								<s:if test="#p.closed == true">
									<font color="green">完成</font>
								</s:if><s:else>
									<font color="#red">进行中</font>
								</s:else>
							</td>
							<td>${p.staring}</td>
							<td>
								<a target="_blank" href="Project_updateInput.action?project.id=${p.id}">修改</a>&nbsp;&nbsp;
								<a target="_blank" href="Project_showAll.action?project.id=${p.id}">查看详细</a>
							</td>
							
						</tr>
					</s:iterator>
					
					<tr>
						<s:if test="errorMessage==null">
							<td colspan="9" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
							</td>
						</s:if>
						<s:else>
							<td colspan="8" align="center" style="color: red">
								${errorMessage}
							</td>
						</s:else>
					</tr>
				</table>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
	</body>
</html>
