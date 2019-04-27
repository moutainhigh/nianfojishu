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
</style>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng">
		</div>

		<div align="center">
			<h3>
				外委待领料展示
			</h3>
			<form action="WaigouwaiweiPlanAction!showWWdailing.action" method="post">
				<table class="table" align="center">
					<tr>
						<td align="right">
							总成件号：
						</td>
						<td>
							<input type="text" name="waigoudd.rootMarkId"
								value="${waigoudd.rootMarkId}" />
						</td>
						<td align="right">
							总成批次：
						</td>
						<td>
							<input type="text" name="waigoudd.rootSlfCard"
								value="${waigoudd.rootSlfCard}" />
						</td>
					</tr>
					<tr>
						<td align="right">
							件号：
						</td>
						<td>
							<input type="text" name="waigoudd.markId"
								value="${waigoudd.markId}" />
						</td>
						<td align="right">
							批次：
						</td>
						<td>
							<input type="text" name="waigoudd.selfCard"
								value="${waigoudd.selfCard}" />
						</td>
					</tr>

					<tr>
						<td align="right">
							业务件号：
						</td>
						<td>
							<input type="text" name="waigoudd.ywmarkId"
								value="${waigoudd.ywmarkId}" />
						</td>
						<td align="right">
							状态：
						</td>
						<td>
							<SELECT name="waigoudd.status">
								<option value="${waigoudd.status}">
									${waigoudd.status}
								</option>
								<option></option>
								<option value="待打印">
									待打印
								</option>
								<option value="送货中">
									送货中
								</option>
								<option value="待存柜">
									待存柜
								</option>
								<option value="待检验">
									待检验
								</option>
								<option value="检验中">
									检验中
								</option>
								<option value="待入库">
									待入库
								</option>
								<option value="入库">
									入库
								</option>
							</SELECT>
						</td>
					</tr>
					<tr>
						<td align="right">
							内部订单号：
						</td>
						<td>
							<input type="text" name="waigoudd.neiorderNum"
								value="${waigoudd.neiorderNum}" />
						</td>
						<td align="right">
						</td>
						<td>
						</td>
					</tr>
				</table>
				<input type="submit" style="width: 100px; height: 40px;"
					value="查询(select)" />
			</form>
			<table width="100%" border="0" class="table"
				style="border-collapse: collapse;">
				<tr bgcolor="#c0dcf2" height="50px">
					<td align="center">
						序号
						<br />
						(num)
					</td>
					<td align="center">
						内部订单号
					</td>
					<td align="center">
						总成件号
					</td>
					<td align="center">
						业务件号
					</td>
					<td align="center">
						总成批次
					</td>
					<td align="center">
						件号
					</td>
					<td align="center">
						名称
					</td>
					<td align="center">
						批次
					</td>
					<td align="center">
						版本
					</td>
					<td align="center">
						工序号
					</td>
					<td align="center">
						工序名
					</td>
					<td align="center">
						仓区
					</td>
					<td align="center">
						库位
					</td>
					<td align="center">
						需求数量
						<br>
						（<font color="red">${sumMoney}</font>）
					</td>
					<td align="center">
						签收数量
						<br>
						（<font color="red">${sumNum}</font>）
					</td>
					<td align="center">
						待入库数量
						<br>
						（<font color="red">${sumbhsprice}</font>）
					</td>
					<td align="center">
						入库数量
						<br>
						（<font color="red">${sumbhsprice}</font>）
					</td>
					<td align="center">
						库存数量
						<br>
						（<font color="red">${sumbhsprice}</font>）
					</td>
					<td align="center">
						状态
					</td>
					<td align="center">
						签收时间
					</td>
					<td align="center">
						检验时间
					</td>
					<td align="center">
						入库时间
					</td>
				</tr>
				<s:iterator value="list_wdd" id="pageWdd" status="pageStatus">
					<s:if test="#pageStatus.index%2==1">
						<tr align="center" bgcolor="#e6f3fb"
							onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'#e6f3fb')">
					</s:if>
					<s:else>
						<tr align="center" onmouseover="chageBgcolor(this)"
							onmouseout="outBgcolor(this,'')">
					</s:else>
					<td>
						<s:if test="#pageStatus.index%2==1">
							<font>
						</s:if>
						<s:else>
							<font color="#c0dcf2">
						</s:else>
						<b><s:property value="#pageStatus.index+1" />
						</b>
						</font>
					</td>
					<td align="center">
						${pageWdd.neiorderNum}
					</td>
					<td
						style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
						<font size="1">${pageWdd.rootMarkId}</font>
						<ul class="qs_ul">
							<li>
								${pageWdd.rootMarkId}
							</li>
						</ul>
					</td>
					<td align="center">
						${pageWdd.ywmarkId}
					</td>
					<td align="center">
						${pageWdd.rootSlfCard}
					</td>
					<td align="center">
						${pageWdd.markId}
					</td>
					<td align="center">
						${pageWdd.proName}
					</td>
					<td align="center">
						${pageWdd.examineLot}
					</td>
					<td align="center">
						${pageWdd.banben}
					</td>
					<td align="left">
						${pageWdd.processNo}
					</td>
					<td
						style="max-width: 50px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
						<font size="1">${pageWdd.processName}</font>
						<ul class="qs_ul">
							<li>
								${pageWdd.processName}
							</li>
						</ul>
					</td>
					<td align="center">
						${pageWdd.changqu}
					</td>
					<td align="center">
						${pageWdd.kuwei}
					</td>
					<td align="right">
						${pageWdd.filnalCount}
					</td>
					<td align="right">
						${pageWdd.qrNumber}
					</td>
					
					<td align="right">
						${pageWdd.hgNumber}
					</td>
					<td align="right">
						${pageWdd.yrukuNum}
					</td>
					<td align="right">
						${pageWdd.kcCount}
					</td>
					<td align="center">
						${pageWdd.status}
					</td>
					<td>
						${pageWdd.querenTime}
					</td>
					<td align="center">
						${pageWdd.checkTime}
					</td>
					<td align="center">
						${pageWdd.rukuTime}
					</td>
<%--					<td align="center" id="td_${pageWdd.id}">--%>
<%--													<s:if test='#pageWdd.status == "入库" && #pageWdd.lingliaoNum<#pageWdd.hgNumber' >--%>
<%--														<table id="ul_${pageWdd.id}" style="display: none;">--%>
<%--															<tr><td>卡号:</td><td> <input type="text" value="" name="bacode" id="bacode${pageWdd.id}"></td></tr>--%>
<%--															<tr><td>领料数量:</td><td><input type="text" name="sumMoney" value="${pageWdd.hgNumber-pageWdd.lingliaoNum}" id="sumMoney${pageWdd.id}" ></td></tr>--%>
<%--														</table>--%>
<%--														<input type="button" onclick="show1('${pageWdd.id}')"  value="刷卡领料" id="shuaka_${pageWdd.id}"  />--%>
<%--													</s:if>--%>
<%--					</td>--%>
					</tr>
				</s:iterator>
<%--				<tr>--%>
<%--					<s:if test="errorMessage==null">--%>
<%--						<td colspan="20" align="right">--%>
<%--							第--%>
<%--							<font color="red"><s:property value="cpage" /> </font> /--%>
<%--							<s:property value="total" />--%>
<%--							页--%>
<%--							<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"--%>
<%--								styleClass="page" theme="number" />--%>
<%--					</s:if>--%>
<%--					<s:else>--%>
<%--						<td colspan="20" align="center" style="color: red">--%>
<%--							${errorMessage}--%>
<%--					</s:else>--%>
<%--					</td>--%>
<%--				</tr>--%>
<%----%>
<%--				<s:if test="successMessage!=null">--%>
<%--					<tr>--%>
<%--						<td colspan="20" align="center" style="color: red">--%>
<%--							${successMessage}--%>
<%----%>
<%--						</td>--%>
<%--					</tr>--%>
<%--				</s:if>--%>
			</table>
		</div>
		<br>
		</div>
		<%@include file="/util/foot.jsp"%>

		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
function update(id) {
	window.location.href = "skillScoreAction_toupdate.action?skillScore.id="
			+ id;
}
function todelete(id, cpage) {
	window.location.href = "skillScoreAction_delete.action?skillScore.id=" + id
			+ "&cpage=" + cpage;
}
function add() {
	window.location.href = "<%=path%>/System/shizhi/skillscore_add.jsp";
}
function show1(num) {
	$("#ul_" + num).removeAttr('style');
	$("#shuaka_" + num).val('领料');
	$("#shuaka_" + num).attr('onclick', 'shuaka(' + num + ')');

}
var id = '';
function shuaka(num) {
	id = num;
	var bacode = $("#bacode" + num).val();
	if (bacode == '') {
		getcheckList2(num);
		return false;
	}
	var sumMoney = $("#sumMoney" + num).val();
	if (bacode != null && bacode != '') {
		$.ajax( {
			type : "POST",
			url : "WaigouwaiweiPlanAction!togoodsUpdate.action",
			dataType : "json",
			data : {
				bacode : bacode,
				id : num,
				sumNum : sumMoney
			},
			success : function(data) {
				if (data == "true") {
					alert('领料成功!');
					window.location.reload(true);
				} else {
					alert(data);
				}
			}
		});
	} else {
		alert('请刷卡，或者直接输入卡号');
	}
}

function getcheckList2() {
	if (typeof (myObj) != "undefined") {
		//打开扫描服务
		myObj.scanGongWei(1);
	} else {
		alert("无法打开扫描服务,请检查后重试!");
	}
}
function funFromjs(tm) {
	if (id != '') {
		$("#bacode" + id).val(tm);
		id = '';
	}

}
</script>
	</body>
</html>
