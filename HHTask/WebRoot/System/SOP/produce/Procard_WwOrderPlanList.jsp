<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
		<script src="http://pv.sohu.com/cityjson?ie=utf-8">
</script>

		<script type="text/javascript">
$(function() {
	if ('${strList1}'.indexOf('外部权限') < 0 && '${strList1}'.indexOf('gys') < 0) {
		if ('${companyInfo.outIp}'.indexOf(returnCitySN["cip"])<0) {
			$(".hideprice").hide();
			$("#showwarning").show();
		}
	}
})
</script>
		<STYLE type="text/css">
.dhlabel {
	border-top: 1px solid #000;
	border-bottom: 1px solid #000;
	border-left: 1px solid #000;
	border-right: 1px solid #000;
	margin-left: 5px;
	margin-right: 5px;
	padding: 3px 5px;
	white-space: nowrap;
}

td:hover .qs_ul {
	display: block;
}

.qs_ul {
	display: none;
	border: 1px solid #999;
	list-style: none;
	margin: 0;
	padding: 0;
	position: absolute;
	width: auto;
	background: #CCC;
	color: green;
}

.ztree li a {
	color: #fff;
}
</STYLE>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div id="bodyDiv" align="center" class="transDiv"
				onclick="chageDiv('none')">
			</div>
			<div id="contentDiv"
				style="position: absolute; z-index: 255; width: 900px; display: none;"
				align="center">
				<div id="closeDiv"
					style="position: relative; top: 165px; left: 0px; right: 200px; z-index: 255; background: url(<%=basePath%>/images/bq_bg2.gif); width: 900px;">
					<table style="width: 100%">
						<tr>
							<td>
								<span id="title">修改供应商</span>
							</td>
							<td align="right">
								<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
									id="closeTcDiv" height="32" onclick="chageDiv('none')">
							</td>
						</tr>
					</table>
					<div id="operatingDiv"
						style="background-color: #ffffff; width: 100%;">
						<iframe id="xiugaiIframe" src="" marginwidth="0" marginheight="0"
							hspace="0" vspace="0" frameborder="0" scrolling="yes"
							style="width: 98%; height: 500px; margin: 0px; padding: 0px;"></iframe>

					</div>
				</div>
			</div>
			<div align="center">
				<s:if test="waigouOrder!=null">
					<s:if
						test="waigouOrder2.status!='待核对'&&waigouOrder2.status!='待审批'&&waigouOrder2.status!='待通知'">
						<div id="test" style="position: absolute; right: 5px; top: 5px;">
							<img alt="" src="<%=basePath%>/img/yishenhe.png"
								style="transform: rotate(8deg);" width="80px;" height="50px;" />
					</s:if>
			</div>
			<table class="table" style="width: 50%">
				<tr>
					<th colspan="2">
						采购订单明细(
						<a
							href="sellAction!showWwclDetail.action?id=${waigouOrder.id}&noOperation=${noOperation}">物料明细</a>)
					</th>
				</tr>
				<tr>
					<th align="right">
						总成件号:
					</th>
					<td>
						${waigouOrder.rootMarkId}
					</td>
				</tr>
				<tr>
					<th align="right">
						业务件号:
					</th>
					<td>
						${waigouOrder.ywMarkId}
					</td>
				</tr>
				<tr>
					<th align="right">
						供应商编号:
					</th>
					<td>
						${waigouOrder.userCode}
					</td>
				</tr>
				<tr>
					<th align="right">
						供应商:
					</th>
					<td>
						${waigouOrder.gysName}
					</td>
				</tr>
				<tr>
					<th align="right">
						采购月份:
					</th>
					<td>
						${waigouOrder.caigouMonth}
					</td>
				</tr>
				<tr>
					<th align="right">
						订单状态:
					</th>
					<td>
						${waigouOrder.status}
					</td>
				</tr>
				<tr>
					<th align="right">
						计划单号:
					</th>
					<td>
						${waigouOrder.planNumber}
					</td>
				</tr>
				<tr>
					<th align="right">
						付款方式:
					</th>
					<td>
						<s:if
							test="(waigouOrder.status=='待核对'||waigouOrder.status=='待通知')&&(noOperation!='noOperation'||noOperation==null)">
							<form action="WaigouwaiweiPlanAction!updateOrderMsg.action"
								method="post">
								<input type="hidden" name="waigouOrder.id"
									value="${waigouOrder.id}">
								<input type="hidden" name="tag" value="payType">
								<input name="waigouOrder.payType" value="${waigouOrder.payType}">
								<input type="submit" value="修改">
							</form>
						</s:if>
						<s:else>
							${waigouOrder.payType}
							</s:else>
					</td>
				</tr>
				<tr>
					<th align="right">
						添加日期:
					</th>
					<td>
						${waigouOrder.addTime}
					</td>
				</tr>
				<tr>
					<th align="right">
						通知日期:
					</th>
					<td>
						${waigouOrder.tongzhiTime}
					</td>
				</tr>
				<tr>
					<th align="right">
						确认采购日期:
					</th>
					<td>
						${waigouOrder.querenTime}
					</td>
				</tr>
			</table>
			<br />
			<br />
			</s:if>
			<s:else>
				<a
					href="WaigouwaiweiPlanAction!findWwOrderList.action?pageStatus=${pageStatus}&noOperation=${noOperation}">采购订单模式</a>
				<div align="right">
					<s:if test="pageStatus!='gysall'&&pageStatus!='gysnew'">
						<s:if test="pageStatus=='dsq'">
							<label style="background-color: gray;" class="dhlabel">
								外委待申请
							</label>
						</s:if>
						<s:else>
							<label style="background-color: #5cb85c;"
								onclick="toShowWW('dsq');" class="dhlabel">
								<font color="white">外委待申请</font>
							</label>
						</s:else>
						<s:if test="pageStatus=='dtz'">
							<label style="background-color: gray;" class="dhlabel">
								外委待通知
							</label>
						</s:if>
						<s:else>
							<label style="background-color: #5cb85c;"
								onclick="toShowWW('dtz');" class="dhlabel">
								<font color="white">外委待通知</font>
							</label>
						</s:else>
						<s:if test="pageStatus=='dqr'">
							<label style="background-color: gray;" class="dhlabel">
								外委待确认
							</label>
						</s:if>
						<s:else>
							<label style="background-color: #5cb85c;"
								onclick="toShowWW('dqr');" class="dhlabel">
								<font color="white">外委待确认</font>
							</label>
						</s:else>
						<s:if test="pageStatus=='findAllself'">
							<label style="background-color: gray;" class="dhlabel">
								所有(自己)
							</label>
						</s:if>
						<s:else>
							<label style="background-color: #5cb85c;"
								onclick="toShowWW('findAllself');" class="dhlabel">
								<font color="white">所有(自己)</font>
							</label>
						</s:else>
						<s:if test="pageStatus=='findAll'">
							<label style="background-color: gray;" class="dhlabel">
								所有
							</label>
						</s:if>
						<s:else>
							<label style="background-color: #5cb85c;"
								onclick="toShowWW('findAll');" class="dhlabel">
								<font color="white">所有</font>
							</label>
						</s:else>
					</s:if>
				</div>
				<form
					action="WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=${pageStatus}&noOperation=${noOperation}"
					method="post">
					<table class="table">
						<tr>
							<th>
								采购单编号:
							</th>
							<td>
								 <input name="waigouPlan.planNumber"
									value="${waigouPlan.planNumber}" />
							</td>
							<th>
								总成件号
								<br />
								(或业务件号):
							</th>
							<td>
								<input name="waigouPlan.rootMarkId"
									value="${waigouPlan.rootMarkId}" />
							</td>
							<th>
								总成批次:
							</th>
							<td>
								<input name="waigouPlan.rootSlfCard"
									value="${waigouPlan.rootSlfCard}" />
							</td>
						</tr>
						<th>
							件号:
						</th>
						<td>
							<input name="waigouPlan.markId" value="${waigouPlan.markId}" />
						</td>
						<th>
							添加时间（起）:
						</th>
						<td>
							<input class="Wdate" type="text" name="waigouPlan.addTime"
								value="${fn:split(waigouPlan.addTime,',' )[0]}"
								onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
						</td>
						<th>
							添加时间（止）:
						</th>
						<td>
							<input class="Wdate" type="text" name="waigouPlan.addTime"
								value="${fn:split(waigouPlan.addTime,',' )[1]}"
								onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen',startDate:'%y-%M-%d 23:59:59'})" />
						</td>
						</tr>
						</tr>
						<tr>
							<th>
								供应商:
							</th>
							<td>
								<input name="waigouPlan.gysName" value="${waigouPlan.gysName}" />
							</td>
							<th>
								到货时间（起）:
							</th>
							<td>
								<input class="Wdate" type="text" name="waigouPlan.acArrivalTime"
									value="${fn:split(waigouPlan.acArrivalTime,',' )[0]}"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
							</td>
							<th>
								到货时间（止）:
							</th>
							<td>
								<input class="Wdate" type="text" name="waigouPlan.acArrivalTime"
									value="${fn:split(waigouPlan.acArrivalTime,',' )[1]}"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',skin:'whyGreen',startDate:'%y-%M-%d 23:59:59'})" />
							</td>
						</tr>
						<tr>
							<th>
								顺序:
							</th>
							<td>
								<s:if test="sunxu=='dao'">
								<input type="radio" name="sunxu" value="sun" />顺序<input  type="radio" name="sunxu" value="dao" checked="checked"/>倒序
								</s:if>
								<s:else>
								<input  type="radio" name="sunxu" value="sun" checked="checked"/>顺序<input  type="radio" name="sunxu" value="dao" />倒序
								</s:else>
							</td>
							<th>
							</th>
							<td>
							</td>
							<th>
							</th>
							<td>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="6">
								<input type="submit" value="查询" class="input">
								<input type="button" value="导出" class="input"
									onclick="clicks1(this.form);todisabledone(this)" data="downData">
								<s:if test="pageStatus=='findAlljg'">
									<input type="button" value="导出(价格)" class="input"
										onclick="clicks2(this.form);todisabledone(this)" data="downData">
								</s:if>
							</td>
						</tr>
					</table>
				</form>
			</s:else>
			<div align="right">
				<div style="float: left; width: 90%" align="left">
					显示:
					<s:if test="strList!=null && strList.size()>0">
						<input type="checkbox" id="showjg"
							onchange="showtd('showjg','jgtd');" class="hideprice">
						<div style="color: red; display: none;" id="showwarning">
							外部网络,无价格查看权限
						</div>
					</s:if>
					<s:else>
						无价格查看权限
					</s:else>
					价格&nbsp;&nbsp;&nbsp;
					<input type="checkbox" id="showsj"
						onchange="showtd('showsj','sjtd');">
					时间
				</div>
				<div style="float: left; width: 9%">
					<s:if test="waigouOrder.id!=null">
						<a
							href="WaigouwaiweiPlanAction!getwgOrderTz.action?id=${waigouOrder.id}">下载图纸</a>
					</s:if>
					<s:if test="noOperation!='noOperation'||noOperation==null">
						<a
							href="WaigouwaiweiPlanAction!gotoprint.action?processIds=${waigouOrder.id}&pageStatus=waiwei&tag=${pageStatus}">打印</a>&nbsp;&nbsp;&nbsp;
						</s:if>
				</div>
				<div style="clear: both;">
				</div>
			</div>
			<s:if
				test="(waigouOrder.status=='待确认'||waigouOrder.status=='协商确认')&&(noOperation!='noOperation'||noOperation==null)">
				<div align="center">
					<form
						action="WaigouwaiweiPlanAction!allUpdateJiaoFuTime.action?id=${waigouOrder.id}"
						method="post">
						统一交付日期:
						<input id="jiaofuTime" class="Wdate" type="text"
							name="waigouPlan.jiaofuTime" style="width: 100px;"
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
						<input type="hidden" name="pageStatus" value="dqr" />
						<input type="hidden" name="tag" value="ww" />
						<input type="submit">
					</form>
				</div>
			</s:if>
			<form id="form" method="post">
				<input value="${waigouOrder.id}" name="id" id="waigouOrderId"
					type="hidden">
				<table class="table">
					<tr bgcolor="#c0dcf2" height="50px">
						<th align="center">
								<input type="checkbox" value="" onclick="chageAllCheck(this)"/>全选
						</th>
						<th align="center">
							序号
						</th>
						<th align="center">
							供应商
						</th>
						<th align="center">
							采购单号
						</th>
						<th align="center">
							总成件号
						</th>
						<th align="center">
							总成批次
						</th>
						<th align="center">
							件号
						</th>
						<th align="center">
							零件名称
						</th>
						<th align="center">
							外委工序号
						</th>
						<th align="center">
							外委工序
						</th>
						<th align="center">
							外委类型
						</th>
						<th align="center">
							采购单版本
						</th>
						<th align="center">
							生产BOM版本
						</th>
						<th align="center">
							数量
						</th>
						<th align="center">
							签收数量
						</th>
						<th align="center">
							入库数量
						</th>
						<th align="center">
							是否设变
						</th>
						<th align="center" class="jgtd" style="display: none;">
							含税单价(元)
						</th>
						<th align="center" class="jgtd" style="display: none;">
							不含税单价(元)
						</th>
						<th align="center" class="jgtd" style="display: none;">
							税率
						</th>
						<th align="center" class="jgtd" style="display: none;">
							总额(含税)
						</th>
						<th align="center" class="sjtd" style="display: none;">
							添加时间
						</th>
						<th align="center">
							供应商库存
						</th>
						<th align="center" class="sjtd" style="display: none;">
							协商交付日期
						</th>
						<th align="center" class="sjtd" style="display: none;">
							确认时间
						</th>
						<th align="center" class="sjtd" style="display: none;">
							应到货时间
						</th>
						<th align="center" class="sjtd" style="display: none;">
							实际到货时间
						</th>
						<th align="center">
							产品状态
						</th>
						<th align="center">
							设变信息
						</th>
						<th align="center">
							操作
						</th>
					</tr>
					<s:iterator value="wwPlanList" id="pageWgww2" status="pageStatus2">
						<s:if test="#pageWgww2.cybanben=='yes'">
							<tr align="center" bgcolor="red" style="height: 50px;">
						</s:if>
						<s:else>
							<s:if test="#pageStatus2.index%2==1">
								<tr align="center" bgcolor="#e6f3fb" style="height: 50px;"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									style="height: 50px;" onmouseout="outBgcolor(this,'')">
							</s:else>
						</s:else>
						<td>
								<input type="checkbox" value="${pageWgww2.id}"  name="processIds" />
						</td>
						<td>
							<input id="wwplanId<s:property value="#pageStatus2.index" />"
								value="${pageWgww2.id}"
								name="wwPlanList[<s:property value="#pageStatus2.index" />].id"
								type="hidden">
							<input value="${pageStatus}" name="pageStatus" type="hidden">
							<s:property value="#pageStatus2.index+1" />
						</td>
						<td>
							<a target="_showGys"
								href="zhaobiaoAction!listByIdZhUser.action?zhUser.id=${pageWgww2.gysId}">
								<s:if
									test="#pageWgww2.gysjc==null||#pageWgww2.gysjc.length()==0">
									${pageWgww2.gysName}
									</s:if> <s:else>
									${pageWgww2.gysjc}
									</s:else> </a>
						</td>
						<td>
							${pageWgww2.waigouOrder.planNumber}

						</td>
						<td>
							${pageWgww2.rootMarkId}
							</br>
							(
							<font color="red">${pageWgww2.ywmarkId}</font>)

						</td>
						<td
							style="width: 100px; max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageWgww2.rootSlfCard}</font>
							<ul class="qs_ul">
								<li>
								<b>${pageWgww2.rootSlfCard}</b>
								</li>
							</ul>
							
						</td>
						<td>
							${pageWgww2.markId}
						</td>
						<td>
							${pageWgww2.proName}
						</td>
						<td>
							${pageWgww2.processNOs}
						</td>
						<td
							style="width: 100px; max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageWgww2.processNames}</font>
							<ul class="qs_ul">
								<li>
									${pageWgww2.processNames}
								</li>
							</ul>
						</td>
						<td>
							${pageWgww2.wwType}
						</td>
						<td>
							${pageWgww2.banben}
						</td>
						<td>
							${pageWgww2.procardbanben}
						</td>
						<td align="right">
							${pageWgww2.number}
						</td>
						<td align="right">
							${pageWgww2.qsNum}
						</td>
						<td align="right">
							${pageWgww2.rukuNum}
						</td>
						<td>
							<s:if test="#pageWgww2.bgMarkIds!=null&&#pageWgww2.bgMarkIds.length()>0">
							是
							</br>${pageWgww2.bgMarkIds}
							</s:if>
							<s:else>
							${pageWgww2.hadChange}
							</s:else>
						</td>
						<td align="right" class="jgtd" style="display: none;">
							<a
								href="PriceAction!findPriceById.action?id=${pageWgww2.priceId}&statue=mingxi"
								target="_showPrice">
								<fmt:formatNumber value="${pageWgww2.hsPrice}" pattern="#.####"></fmt:formatNumber>	
								</a>
						</td>
						<td align="right" class="jgtd" style="display: none;">
						<fmt:formatNumber value="${pageWgww2.price}" pattern="#.####"></fmt:formatNumber>	
						</td>
						<td align="right" class="jgtd" style="display: none;">
							${pageWgww2.taxprice}
						</td>
						<td align="right" class="jgtd" style="display: none;">
							<fmt:formatNumber value="${pageWgww2.money}" pattern="#.####"></fmt:formatNumber>	
						</td>
						<td class="sjtd" style="display: none;">
							${pageWgww2.addTime}
						</td>
						<s:if test="waigouOrder.status=='待确认'||waigouOrder.status=='协商确认'">
							<td>
								<input id="kuCunCount<s:property value="#pageStatus2.index" />"
									name="wwPlanList[<s:property value="#pageStatus2.index" />].kuCunCount"
									style="width: 100px;" value="${pageWgww2.kuCunCount}">
							</td>
							<td class="sjtd" style="display: none;">
								<input id="jiaofuTime<s:property value="#pageStatus2.index" />"
									class="Wdate" type="text"
									name="wwPlanList[<s:property value="#pageStatus2.index" />].jiaofuTime"
									style="width: 100px;"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"
									value="${pageWgww2.jiaofuTime}" />
							</td>
						</s:if>
						<s:else>
							<td>
								${pageWgww2.kuCunCount}
							</td>
							<td class="sjtd" style="display: none;">
								${pageWgww2.jiaofuTime}
							</td>
						</s:else>
						<td class="sjtd" style="display: none;">
							${pageWgww2.querenTime}
						</td>
						<td class="sjtd" style="display: none;">
							${pageWgww2.shArrivalTime}
						</td>
						<td width="180px;" class="sjtd"
							style="display: none; max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
							<font size="1">${pageWgww2.acArrivalTime}</font>
							<ul class="qs_ul">
								<li>
									${pageWgww2.acArrivalTime}
								</li>
							</ul>
						</td>
						<td>
							${pageWgww2.status}
						</td>
						<td>
							<s:if test="#pageWgww2.sbId==null">
								${pageWgww2.sbNumber}
							</s:if>
							<s:else>
								<a
								href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${pageWgww2.sbId}">${pageWgww2.sbNumber}</a>
							</s:else>
						</td>
						<td>
							<s:if test="noOperation!='noOperation'||noOperation==null">
								<s:if test='waigouOrder.status=="待确认"'>
									<input id="jiaoqibtn" type="button" value="交期确认"
										onclick="querenjiaoqi(<s:property value="#pageStatus2.index" />)">
								</s:if>
								<br />
								<a
									href="WaigouwaiweiPlanAction!findWwPlanList.action?id=${pageWgww2.waigouOrder.id}&pageStatus=${pageStatus}">订
									单</a>
								<br />
								<input type="button" value="查看图纸" style="height: 35px;"
									onclick="javascript:location.href='WaigouwaiweiPlanAction!gysTzview2.action?id=${pageWgww2.id}';">
								<br />
								<s:if test="#pageWgww2.number!=#pageWgww2.syNumber">
									<input type="button" value="送货明细" style="height: 35px;"
										onclick="javascript:location.href='WaigouwaiweiPlanAction!findWaigouPlanDNDetail.action?id=${pageWgww2.id}';">
								</s:if>
								<s:else>
									<input type="button" value="送货明细" disabled="disabled">
								</s:else>
								<s:if
									test='waigouOrder.status=="待通知"||waigouOrder.status=="待核对"||waigouOrder.status=="待确认" 
									|| waigouOrder.status=="生产中" '>
									<input type="button" value="修改供应商"
										onclick="tanchu('${pageWgww2.id}','${pageWgww2.gysId}')" />
								</s:if>

								<%--							<s:if test='#pageWgww2.status=="待通知"||#pageWgww2.status=="待核对"||#pageWgww2.status=="待确认"'>--%>
								<s:if test="pageStatus!='gysall'&&pageStatus!='gysnew'">
									<input type="button" value="刷新价格"
										onclick="sxjg(${pageWgww2.id},${waigouOrder.id})" />
								</s:if>
								<%--							</s:if>--%>
								<s:if test="pageStatus == 'gyssbdqr'">
									<input type="button" value="确认取消"
										onclick="quxiaoww('${pageWgww2.id}')" />
								</s:if>
								<s:if test="#pageWgww2.isshowBl">
									<input type="button" value="补料"
										onclick="showcsbl('${pageWgww2.id}')" />
								</s:if>
								<s:if test="#pageWgww2.syNumber>0">
									<input type="button" value="关闭"
										onclick="closeWaiweiPlan(${pageWgww2.id})" />
								</s:if>
								<s:if test="#pageWgww2.syNumber==#pageWgww2.number">
									<input type="button" value="取消"
										onclick="backwwplan(${pageWgww2.id})" />
								</s:if>
							</s:if>
							<s:else>
								<s:if test="#pageWgww2.number!=#pageWgww2.syNumber">
									<input type="button" value="送货明细" style="height: 35px;"
										onclick="javascript:location.href='WaigouwaiweiPlanAction!findWaigouPlanDNDetail.action?id=${pageWgww2.id}';">
								</s:if>
								<s:else>
									<input type="button" value="送货明细" disabled="disabled">
								</s:else>
							</s:else>
							<s:if test="#pageWgww2.status=='设变锁定'">
							 <input type="button" value="手动解锁" style="height: 35px;"
										onclick="sdjs(${pageWgww2.id})"/>
							</s:if>
<%--							<input type="button" value="同步生产" style="height: 35px;"--%>
<%--										onclick="tbsc(${pageWgww2.id})"/>--%>
						</td>
						</tr>
					</s:iterator>
					<tr>
						<td colspan="25" align="right">
							第
							<font color="red"><s:property value="cpage" /> </font> /
							<s:property value="total" />
							页
							<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
								styleClass="page" theme="number" />
						</td>
					</tr>

					<s:if test="pageStatus=='dqr'&&()">
						<tr>
							<th colspan="25">
								<a href="javascript:;" onclick="queren(${waigouOrder.id})"
									href="WaigouwaiweiPlanAction!orderQueren.action?processIds=${waigouOrder.id}&pageStatus=ww">订单确认</a>
								<label id="jiaoqiAll1">
									<a href="javascript:;" onclick="querenjiaoqiAll()">交期确认</a>
								</label>
								<label id="jiaoqiAll2" style="display: none;">
									交期确认
								</label>
							</th>
						</tr>
					</s:if>
					<tr>
						<th colspan="25">
							<input type="button" value="统一刷新价格" style="height: 30px;width: 150px;cursor: pointer;" onclick="shuaixinAllPrice(this.form)"/>
						</th>
					</tr>
				</table>
			</form>
		</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
var parentdocument =  window.parent.document;
var offset=0;
$(function(){
	var oDiv=document.getElementById("test"); 
	var  obj =window.parent.document.getElementById("showAll");
	if(obj!=null){
		offset = obj.offsetTop; 
	}
	if('待审核'=='${waigouOrder.status}'){
		$("#test").hide();
	}
})
parentdocument.onscroll = function(){
	var oDiv=document.getElementById("test"); 
	var parenttop = parentdocument.body.scrollTop;
		parenttop = parenttop-offset;
		if(parenttop<0){
			parenttop = 10;
		}
	oDiv.style.top=parenttop;
}
window.onscroll=function(){ 
	var oDiv=document.getElementById("test"); 
	oDiv.style.top=document.body.scrollTop + 10;  //控制上下位置
} 
function vali() {
	var nums = document.getElementsByName("mrkIds");
	for ( var i = 0; i < nums.length; i++) {
		if (nums[i].checked) {
			return true;
		}
	}
	alert("请选择外委工序！！！");
	return false;
}
function queren(id){
	if(window.confirm("确认采购之后，此外委采购订单将会提交申请。是否确定采购？")){
		window.location.href="WaigouwaiweiPlanAction!orderQueren.action?pageStatus=ww&processIds="+id;
	}
}	
function querenjiaoqiAll(){
	$("#jiaoqiAll1").hide();
	$("#jiaoqiAll2").show();
	if (confirm("是否确认交期!")) {
		$.ajax( {
					type : "POST",
					url : "WaigouwaiweiPlanAction!querenjiaoqiAll.action?",
					dataType : "json",
					data : $('#form').serialize(),
					success : function(data) {
						alert(data);
						$("#jiaoqiAll2").hide();
						$("#jiaoqiAll1").show();
					}
				});
	}
}

function querenjiaoqi(index){
	$("#jiaoqibtn").attr("disabled","disabled");
	var wwplanId=$("#wwplanId"+index).val();
	var kuCunCount=$("#kuCunCount"+index).val();
	var jiaofuTime=$("#jiaofuTime"+index).val();
	if (confirm("是否确认交期!")) {
		$
				.ajax( {
					type : "POST",
					url : "WaigouwaiweiPlanAction!gysXsOrder.action",
					dataType : "json",
					data : {
						'waigouPlan.id' : wwplanId,
						'waigouPlan.kuCunCount' : kuCunCount,
						'waigouPlan.jiaofuTime' : jiaofuTime,
						'id' : $("#waigouOrderId").val()
					},
					success : function(data) {
						alert(data);
						$("#jiaoqibtn").removeAttr("disabled");
					}
				});
	}

}

function tanchu(id,gysId){
		document.getElementById("xiugaiIframe").src = "WaigouwaiweiPlanAction!toxiugaigys.action?id="+id+"&id2="+gysId;
		chageDiv('block');
		
}
function toShowWW(pStatus){
	window.location.href="WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus="+pStatus+"&noOperation=${noOperation}";
}
function quxiaoww(id){
	if(confirm("是否确认取消!")){
		window.location.href="WaigouwaiweiPlanAction!agreeSbquxiaoww.action?id="+id+"&pageStatus="+pStatus;
	}
}
function clicks1(objForm) {
	objForm.action = "WaigouwaiweiPlanAction!exportExcelWaigouPlan.action?pageStatus=${pageStatus}&tag=ww";
	objForm.submit();
	objForm.action = "WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=${pageStatus}";
}
function clicks2(objForm) {
	objForm.action = "WaigouwaiweiPlanAction!exportExcelWaigouPlan2.action?pageStatus=${pageStatus}&tag=ww";
	objForm.submit();
	objForm.action = "WaigouwaiweiPlanAction!findWwPlanList.action?pageStatus=${pageStatus}";
}

function showtd(jg,classname){
	var checkbox=$("#"+jg);
	if(checkbox.attr("checked")=="checked"){
		$("."+classname).show();
	}else{
		$("."+classname).hide();
	}
}
function closeWaiweiPlan(id){
	if(confirm('确定要关闭该订单明细吗?')){
		window.location.href = "WaigouwaiweiPlanAction!closeWaiweiPlan.action?id="+id;
	}
}
function backwwplan(id){
	if(confirm('确定要取消该订单明细吗?')){
		window.location.href = "WaigouwaiweiPlanAction!backwwplan.action?id="+id;
	}
}
function sdjs(id){
	if(confirm('确定要解锁该订单明细吗?')){
		window.location.href = "WaigouwaiweiPlanAction!sdjs.action?id="+id;
	}
}
function showcsbl(id){
	document.getElementById("xiugaiIframe").src = "WaigouwaiweiPlanAction!showtocsbl.action?id="+id;
	chageDiv('block');
}
function sxjg(planId,orderId){
	if(window.confirm("是否确认要刷新此价格")){
		window.location.href="WaigouwaiweiPlanAction!shuaixinPrice.action?id="+planId+"&waigouOrder.id="+orderId+"&pageStatus=${pageStatus}&tag=ww";
	}
}
function shuaixinAllPrice(obj){
	$("input[name='pageStatus']").removeAttr('name');
	$(obj).attr('action','WaigouwaiweiPlanAction!shuaixinAllPrice.action?pageStatus=findAll&tag=waiwei');
	$(obj).submit();
}
</SCRIPT>
	</body>
</html>