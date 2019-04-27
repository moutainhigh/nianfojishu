<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
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
		<style type="text/css">
table {
	font-size: 14px;
	padding: 0px;
	margin: 0px;
	border-collapse: collapse;
	/* 关键属性：合并表格内外边框(其实表格边框有2px，外面1px，里面还有1px哦) */
	border: solid #999; /* 设置边框属性；样式(solid=实线)、颜色(#999=灰) */
	border-width: 1px 0 0 1px;
	width: 980px;
}

table th,table td {
	border: solid #999;
	border-width: 1 1px 1px 1;
	padding: 2px;
}
</style>
	</head>
	<body bgcolor="#ffffff">
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border:  solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; padding-left: 35px; padding-top: 5px; padding-bottom: 5px; "
				align="left">
				<div style="float: left; width: 50%" align="left">
					
				</div>
			</div>
			
			<div align="center">
				<form action="GoodsStoreAction!findjianhao.action" method="post">
					<table>
						<tr>
							<th colspan="2">
								<font size="5">入库表信息</font>
								<a href="BonusAction.action">返回</a>
							</th>
						</tr>
						<tr>
							<th>
								件号
							</th>
							<td>
								<input type="text" name="jianhao" />
								&nbsp;&nbsp;&nbsp;
								<input type="submit" value="确  定" />
								<a href="GoodsStoreAction.action?todayDate=${todayDate}">查看全部</a>
								<input type="hidden" value="${date2}" name="date2" />
								<input type="hidden" value="${date}" name="date" />

							</td>
						</tr>
					</table>
				</form>
				<table align="center">
					<tr>
						<th>
							件号
						</th>
						<th>
							型别
						</th>
						<th>
							数量
						</th>
						<th>
							入库时间
						</th>
						<th>
							操作
						</th>
					</tr>
					<s:iterator id="good" value="list" status="stauts">
						<s:if test="#stauts.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td align="center">
							${good.goodsStoreMarkId}
						</td>
						<td align="center">
							${good.goodsStoreCompanyName}
						</td>
						<td align="center">
							${good.goodsStoreCount}
						</td>
						<td align="center">
							${good.goodsStoreTime}
						</td>
						<td align="center">
							<A
								href="GoodsStoreAction!findMakIDmoney.action?jianhao=${good.goodsStoreMarkId}&todayDate=${todayDate}">查看价格</A>
						</td>
						</tr>
					</s:iterator>
					<tr>
						<td colspan="16" align="right">
							第
							<font color="red"><s:property value="cpage" /> </font> /
							<s:property value="total" />
							页
							<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
								styleClass="page" theme="number" />
						</td>
					</tr>
				</table>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
	</body>
</html>
