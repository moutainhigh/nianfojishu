<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<head>
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<BODY>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<h3>
					研发项目汇总
				</h3>
			</div>
			<table class="table" >
				<tr>
					<th align="center">编号</th>
					<th align="center">项目池名称</th>
					<th align="center">项目编号</th>
					<th align="center">项目名称</th>
					<th align="center">负责人</th>
					<th align="center">项目分数</th>
					<th align="center">项目占比</th>
					<th align="center">人数</th>
					<th align="center">项目总金额</th>
					<th align="center">个人所得金额</th>
					<th align="center">申请延期时间</th>
					<th align="center">审批人</th>
					<th align="center">申请延期状态</th>
					<th>状态</th>
					<th align="center">操作</th>
				</tr>
				<s:if test="null!=projectManageyfList&&projectManageyfList.size()>0">
					<s:iterator value="projectManageyfList" id="project" status="see">
						<s:if test="#see.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
							<td>
								<s:property value="#see.index+1" />
							</td>
							<td>${project.projectPool.poolName}</td>
							<td>${project.proNum}</td>
							<td>${project.proName}</td>
							<td>${project.principals}</td>
							<td>${project.gradeStore}</td>
							<td>${project.proportion}</td>
							<td>${project.person}</td>
							<td align="right">${project.money}(元)</td>
							<td align="right">
								<s:if test="null!=#project.selfMoney">
									${project.selfMoney}(元)
								</s:if>
							</td>
							<td>
								<s:if test="null!=#project.outTime">
									<s:property value="#project.outTime" />(天)
								</s:if>
							</td>
							<td>
								<s:property value="#project.outTimeexplainPerson" />
							</td>
							<td>
								<s:property value="#project.outTimeResult" />
							</td>
							<td>${project.status}</td>
							<td>
								<s:if test="null==pageStatus || ''==pageStatus">
									<input type="button" value="项目明细" 
									onclick="location.href='${pageContext.request.contextPath}/projectPoolAction_getStoreResult.action?id=${project.id}'"/>
								</s:if>
							</td>
						</tr>
					</s:iterator>
				</s:if>
				<s:else>
					<tr>
						<td colspan="21" style="font-size: 15px; color: red">
							对不起，没有查到相关的研发项目信息
						</td>
					</tr>
				</s:else>
			</table>
			
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
	
<script type="text/javascript">
	



</script>
	</BODY>
</HTML>