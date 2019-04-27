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
		<style type="text/css">
@media print {
	#yufukuan {
		width: 95%;
	}
	.table10 {
		font-size: 1px;
		text-indent: 6px;
		padding: 0px;
		margin: 0px;
		border-collapse: collapse;
		border: solid #999;
		border-width: 0px 0 0 0px;
		width: 99%;
	}
	.table10 th,.table10 td {
		border: solid #000;
		border-width: 2 2px 2px 2;
		font-size: 1mm;
		line-height: 11px;
	}
}

@media screen {
	.table10 {
		font-size: 14px;
		padding: 0px;
		margin: 0px;
		border-collapse: collapse;
		border: solid #999;
		border-width: 0px 0 0 0px;
		width: 99%;
	}
	.table10 th,.table10 td {
		border: solid #000;
		border-width: 2 2px 2px 2;
		padding: 2px;
	}
}
</style>
	</head>
	<body style="font-family: '黑体'">
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%; margin-top: 10px;">
			<div id="yufukuan" align="center" style="width: 95%;">
				<h3 style="width: 100%;" align="center">
					<font size="1" style="line-height: 11px;"><br />
						<p>
							${companyInfo.name}
						</p>
						<p>
							预付款申请单
						</p> 
					</font>
				</h3>
				<div align="left" style="width: 90%; font-size: 1px; line-height: 11px;">
					<div style="float: left;">
						&nbsp;&nbsp;
						<b>申请部门：</b>${prepayApp.addDept}
					</div>
					<div style="float: right;">
						<b>申请日期：</b>${prepayApp.addTime}&nbsp;&nbsp;
					</div>
				</div>
				<table class="table10" align="center" style="width: 100%; ">
					<tr style="height: 75px;">
						<%--					line-height: 25px;border-right-width: 0px;--%>
						<td align="left" colspan="2" style="height: 50px;font-size: 15px;line-height: 20px;width: 25%" >
							<div style="height: 20%">
							<b>预付项目：</b>
							</div>
							<div style="height: 50%">
							<b>PO&nbsp;&nbsp;号码：</b>
							</div>
							<div style="height: 17%">
							<b>采购(合同)总额：</b>
							</div>
							<div style="height: 9%">
							<b>预付比例：</b>
							</div>
							<div style="height: 4%">
							</div>
						</td>
						<%--						line-height: 25px;border-left-width: 0px;--%>
						<td align="left" colspan="5" style="height: 50px; font-size:15px;line-height:20px;">
						<div style="height: 20%">
							${prepayApp.yyName}
						</div>
						<div style="height: 50%">
							${prepayApp.poNumber}
						</div>
						<div style="height: 17%">
						<fmt:formatNumber pattern="0.00" value="${prepayApp.allMoneys}"></fmt:formatNumber>&nbsp;
						</div>
						<div style="height: 9%">
						${prepayApp.yfbl}%
						</div>
						<div style="height: 4%">
						</div>
						</td>
						<td rowspan="6"
							style="width: 1px; border-width: 0px; font-size: 1mm;">
							<ul>
								<li>
									第
								</li>
								<li>
									一
								</li>
								<li>
									联
								</li>
								<li>
									付
								</li>
								<li>
									款
								</li>
								<li>
									依
								</li>
								<li>
									据
								</li>
								<li>
									︵
								</li>
								<li>
									白
								</li>
								<li>
									︶
								</li>
								<li>
									第
								</li>
								<li>
									二
								</li>
								<li>
									联
								</li>
								<li>
									二
								</li>
								<li>
									联
								</li>
								<li>
									二
								</li>
								<li>
									：
								</li>
								<li>
									二
								</li>
								<li>
									联
								</li>
								<li>
									请
								</li>
								<li>
									位
								</li>
								<li>
									︵
								</li>
								<li>
									红
								</li>
								<li>
									︶
								</li>
							</ul>
							<%--						<br/><br><br>--%>
							<%--						<br><br>联<br>：<br>联<br>请<br>单<br>位<br>︵<br>红<br>︶</td>--%>
					</tr>
					<tr style="height: 20px;">
						<th colspan="2" style="height: 20px;">
							预付金额(大写人民币)：
						</th>
						<td align="right" colspan="5" style="height: 20px;">
							<s:if test="prepayApp.yfMoneyDX==null">
							仟&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							佰&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							拾&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							万&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							仟&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							佰&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							拾&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							元&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							角&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							分
						</s:if>
							<s:else>
							${prepayApp.yfMoneyDX}&nbsp;&nbsp;
						</s:else>
						</td>
					</tr>
					<tr style="height: 20px;">
						<th colspan="2" style="height: 20px;">
							预 计 报 销 日 期：
						</th>
						<td align="right" colspan="3" style="height: 20px;">
							${prepayApp.expectedTime}
						</td>
						<td align="left" colspan="2" style="height: 25px;">
							<FONT size="5"><b>￥</b>&nbsp;&nbsp;<fmt:formatNumber pattern="0.00" value="${prepayApp.yfMoneys}"></fmt:formatNumber></FONT>&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
						<td align="center" style="height: 50px; font-size: 1mm;">
							<b>票据类别：</b>
						</td>
						<td align="left" style="border-width: 0px; height: 50px; font-size: 1mm">
							&nbsp;&nbsp;支票 口
							&nbsp;&nbsp;No:
						</td>
						<td style="border-width: 0px; height: 50px; font-size: 1mm"></td>
						<td align="left" style="border-width: 0px; height: 50px; font-size: 1mm">
							电汇 口
							No:
						</td>
						<td style="border-width: 0px; height: 50px;" style="height: 50px; font-size: 1mm"></td>
						<th colspan="2" align="left" style="height: 50px; font-size: 2mm;">
							&nbsp;&nbsp;签收人：
						</th>
					</tr>
					<tr style="font-size: 1mm;height: 20px;" >
						<th>
							总经理
						</th>
						<th>
							会计主管
						</th>
						<th>
							核算会计
						</th>
						<th>
							出纳
						</th>
						<th>
							主管副总
						</th>
						<th>
							部门主管
						</th>
						<th>
							申请人
						</th>
					</tr>
					<tr style="font-size: 1mm;height: 20px;">
						<td style=""></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<th>
							<font size="2px;">${prepayApp.jbName}</font>
						</th>
					</tr>
				</table>
				<div align="left" style="width: 96%;">
					<b><font style="font-family: '楷体'; "  size="1">
							&nbsp;&nbsp;备注：1、签核时请详细核对，签名时请填写日期在签名处。<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、生产采购类：最少需由申请人->主管副总->会计主管(后附报价单/采购订单复印件)<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3、设备(其他)采购类：最少需由申请人->主管副总->会计主管->总经理(后附合同复印件)
					</font>
					</b>
				</div>
			</div>
			<div align="center" style="margin: 75px 300px 0px 0px;">
				<input type="submit" value="打印" onclick="pagePrintOld('yufukuan')"
					style="width: 80px; height: 50px;" />
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
</script>
	</body>
</html>
