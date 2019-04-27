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
		<div id="gongneng" style="width: 100%;">

			<div align="center">
				<h3>
					刷卡记录查看
				</h3>
				<%--<form action="AccessRecordsAction_showList_carType.action" method="post">
							<table class="table">
								<tr style="width: 100%">
									<th align="center" style="width: 15%">
										请输入车牌号
									</th>
									<td align="center" style="width: 25%">
										<input type="text" name="carInOutType.carPai"
											/>
									</td>
									<th align="center" style="width: 15%">
										请输入您要查询的日期
									</th>
									<td align="center" style="width: 25%">
										<input class="Wdate" type="text" name="carInOutType.updateTime" 
											onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"/>
									</td>
									<td align="center" style="width: 30%">
										<input type="submit" value="查询"
											style="width: 100px; height: 25px;" />
									</td>
								
								</tr>
							</table>
						</form>
				--%>
				<table class="table">
					<tr bgcolor="#c0dcf2" height="50px">
						<td align="center">
							序号
						</td>
						<td align="center">
							姓名
						</td>
						<td align="center">
							部门
						</td>
						<td align="center">
							卡号
						</td>
						<td align="center">
							日期
						</td>
						<td align="center">
							时间
						</td>
					</tr>
					<s:iterator value="attendanceTowCollectList" id="samples"
						status="pageStatus">
						<s:if test="#pageStatus.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								style="height: 25px;" onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:property value="#pageStatus.index+1" />
						</td>
						<td align="center">
							${samples.name}
						</td>
						<td align="center">
							${samples.dept}
						</td>
						<td align="center">
							${samples.cardId}
						</td>
						<td align="center">
							${samples.dateTime}
						</td>
						<td align="center">
							${samples.time}
						</td>
					</s:iterator>
					<tr>
						<s:if test="errorMessage==null">
							<td colspan="6" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
						</s:if>
						<s:else>
							<td colspan="6" align="center" style="color: red">
								${errorMessage}
							</td>
						</s:else>
					</tr>
				</table>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">

</script>
	</body>
</html>
