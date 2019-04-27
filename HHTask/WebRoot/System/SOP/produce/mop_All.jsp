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
				<h2>
					件号:${manualPlan.markId}名称:${manualPlan.proName}此次共需求数:
					<font color="red" size="5">${manualPlan.number}</font> 共采购量:
					<font color="red" size="5">${manualPlan.outcgNumber}</font>
				</h2>
				<table class="table">
					<tr>
						<th style="font-size: large" colspan="25">
							件号:${manualPlan.markId}需求明细
						</th>
					</tr>
					<tr align="center" bgcolor="#c0dcf2" height="50px">
						<th>
							序号
						</th>
						<th>
							物料类别
						</th>
						<th>
							内部订单号
						</th>
						<th>
							业务件号
						</th>
						<th>
							件号
						</th>
						<th>
							名称
						</th>
						<th>
							来源
						</th>
						<th>
							需采购量
						</th>
						<th>
							下单量
						</th>
						<th>
							入库量
						</th>
						<th>
							确认时间
						</th>
						<th>
							交货时间
						</th>
						<th>
							备注
						</th>
						<th>
							查看图纸
						</th>
					</tr>
					<s:iterator id="pagemopd" value="list" status="statussdf1">
						<s:if test="#statussdf1.index%2==1">
							<tr align="center" bgcolor="#e6f3fb"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:property value="#statussdf1.index+1" />
						</td>
						<td align="left">
							${pagemopd.wgType}
						</td>
						<td align="left">
							${pagemopd.orderNumber}
						</td>
						<td align="left">
							${pagemopd.ywMarkId}
						</td>
						<td align="left">
							<a
								href="ProcardAction!findProcardByRunCard.action?id=${pagemopd.procardId}&pageStatus=history">
								${pagemopd.markId}</a>
						</td>
						<td align="left">
							${pagemopd.proName}
						</td>
						<td align="left">
							${pagemopd.type}
							<s:if test='#pagemopd.procard!=null'>
								<br>(<font color="green">${pagemopd.procard.selfCard}</font>)
									</s:if>
							<s:elseif test='#pagemopd.order!=null'>
								<br>(<font color="green">${pagemopd.order.orderNum}</font>)
									</s:elseif>
						</td>
						<td align="right">
							${pagemopd.cgnumber}
						</td>
						<td align="right">
							${pagemopd.outcgNumber}
						</td>
						<td align="right">
							${pagemopd.rukuNum}
						</td>
						<td align="right">
							${pagemopd.addTime}
						</td>
						<td align="right">
							${pagemopd.needFinalDate}
						</td>
						<td align="right" style="width: 50px;">
							${pagemopd.remarks}
						</td>
						<td>
							<a
								href="WaigouwaiweiPlanAction!gysTzview0.action?id=${pagemopd.id}">图纸</a>
						</td>
					</s:iterator>
				</table>
				<br />
				<br />



				<table class="table">
					<tr>
						<th style="font-size: large" colspan="25">
							件号:${manualPlan.markId}采购明细
						</th>
					</tr>
					<tr align="center" bgcolor="#c0dcf2" height="50px">
						<th>
							序号
						</th>
						<th>
							订单号
						</th>
						<th>
							物料类别
						</th>
						<th>
							件号
						</th>
						<th>
							名称
						</th>
						<th>
							采购员
						</th>
						<th>
							供应商
						</th>
						<th>
							状态
						</th>
						<th>
							下单量
						</th>
						<th>
							签收量
						</th>
						<th>
							合格量
						</th>
						<th>
							入库量
						</th>
						<th>
							未送货量
						</th>
						<th>
							下单时间
						</th>
					</tr>
					<s:iterator id="pagewaigou" value="wwPlanList" status="statussdf0">
						<s:if test="#statussdf0.index%2==1">
							<tr bgcolor="#e6f3fb" onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'')">
						</s:else>
						<td>
							<s:property value="#statussdf0.index+1" />
						</td>
						<td>
							<a
								href="WaigouwaiweiPlanAction!findWgPlanList.action?id=${pagewaigou.waigouOrder.id}&pageStatus=findAll&noOperation=noOperation">
								${pagewaigou.waigouOrder.planNumber} </a>
						</td>
						<td>
							${pagewaigou.wgType}
						</td>
						<td>
							${pagewaigou.markId}
						</td>
						<td>
							${pagewaigou.proName}
						</td>
						<td>
							${pagewaigou.waigouOrder.addUserName}
							<br />
							(${pagewaigou.waigouOrder.addUserCode})
						</td>
						<td>
							${pagewaigou.gysName}
						</td>
						<td>
							${pagewaigou.status}
						</td>
						<td align="right">
							${pagewaigou.number}
						</td>
						<td align="right">
							${pagewaigou.qsNum}
						</td>
						<td align="right">
							${pagewaigou.hgNumber}
						</td>
						<td align="right">
							${pagewaigou.hasruku}
						</td>
						<td align="right">
							${pagewaigou.syNumber}
						</td>
						<td align="right">
							${pagewaigou.addTime}
						</td>
					</s:iterator>
				</table>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
//将主页iframe高度自适应
$("#xiugaiIframe", window.parent.document).load(function() {//绑定事件
			var main = $("#xiugaiIframe", window.parent.document);//找到iframe对象
			//获取窗口高度 
			var thisheight;
			thisheight = document.body.scrollHeight;
			thisheight = parseFloat(thisheight);
			var conHeight = parseFloat($("body").css("height"));//contentDiv div的宽度
			//				alert("thisheight--"+thisheight);
			//				alert("conHeight--"+conHeight);
			if (conHeight > thisheight) {
				thisheight = conHeight;
			}
			if (thisheight < 500) {
				thisheight = 500;
			}
			main.height(thisheight);//为iframe高度赋值如果高度小于500，则等于500，反之不限高，自适应
		});
</script>

	</body>
</html>
