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
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px; "
				align="left">
				<div
					style="float: left; width: 50%; padding-left: 30px; padding-top: 5px;"
					align="left">
					
				</div>
				<div style="float: left; width: 45%; padding-top: 5px;"
					align="right">
					<a
						href="ModuleFunctionAction!findMfByIdForJump.action?id=${moduleFunction.id}"
						style="color: #ffffff">刷新</a>
				</div>
			</div>
			
			<div align="center">
				<table class="table" style="width: 80%">
					<tr>
						<th colspan="5">报价单</th>
					</tr>
					<s:iterator value="quotationLists" id="qls">
						<tr>
							<td colspan="4">
								<a href="ProjectQuotationList_listChildren.action?quotationList.id=${qls.id}">${qls.description}</a>
							</td>
							<td>
								<a href="ProjectQuotationList_updateWlInput.action?quotationList.id=${qls.id}" target="_blank">物流费用</a>&nbsp;&nbsp;
								<a href="ProjectQuotationList_updateOiInput.action?quotationList.id=${qls.id}" target="_blank">利润</a>&nbsp;&nbsp;
								<a href="ProjectQuotationList_showAll.action?quotationList.id=${qls.id}" target="_blank">计算报价</a>
							</td>
						</tr>
					</s:iterator>
					<s:iterator value="quotations" id="q">
							<tr>
								<th>零件名称</th>
								<td>${q.description}</td>
								<th>零件代号</th>
								<td>${q.partNum}</td>
								<td colspan="2" ><a href="ProjectQuotation_list.action?quotation.id=${q.id}"  target="_blank">查看详细</a> &nbsp;
									<a href="ProjectQuotation_showAll.action?quotation.id=${q.id}" target="_blank">计算报价</a>
								</td>
							</tr>
						</s:iterator>
						<tr>
							<td colspan="6" align="center">
								<a href="ProjectQuotation_addChildInput.action?quotation.root.id=${quotationList.id}" target="_blank">添加报价单</a>&nbsp;&nbsp;
								<a href="ProjectQuotationList_addDirectoryInput.action?quotationList.id=${quotationList.id}" target="_blank">添加目录</a>&nbsp;&nbsp;
							</td>
						</tr>
				</table>
				<br/>
				
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
	</body>
</html>
