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

.mingxi {
	background-color: #FFFFCE;
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
</style>

	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
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
							<span id="title">物料追踪明细页面</span>
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



		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<form action="ManualOrderPlanAction_findmopList.action"
					method="POST" onsubmit="return testTime()">
					<table class="table">
						<tr>
							<th align="right">
								采购类别
							</th>
							<td>
								<select name="manualPlan.category">
									<option></option>
									<s:iterator id="category" value="{'外购件','辅料'}">
										<s:if test="#category==manualPlan.category">
											<option value="${category}" selected="selected">
												${category}
											</option>
										</s:if>
										<s:else>
											<option value="${category}">
												${category}
											</option>
										</s:else>
									</s:iterator>
								</select>
							</td>
							<th align="right">
								件号
							</th>
							<td>
								<input type="text" value="${manualPlan.markId}"
									name="manualPlan.markId">
							</td>
							<th align="right">
								名称
							</th>
							<td>
								<input type="text" value="${manualPlan.proName}"
									name="manualPlan.proName">
							</td>
							<th align="right">
								物料类别
							</th>

							<td>
								<input type="text" value="${manualPlan.wgType}"
									name="manualPlan.wgType">
							</td>
							<th align="right">
								规格
							</th>
							<td>
								<input type="text" value="${manualPlan.specification}"
									name="manualPlan.specification">
							</td>

						</tr>
						<tr>


							<th align="right">
								状态
							</th>
							<td>
								<SELECT name="manualPlan.epStatus">
									<option value="${manualPlan.epStatus}">
										${manualPlan.epStatus}
									</option>
									<option></option>
									<option value="未采购">
										未采购
									</option>
									<option value="已采购">
										已采购
									</option>
									<option value="已入库">
										已入库
									</option>
									<option value="未入库">
										未入库
									</option>
									<option value="取消">
										取消
									</option>
								</SELECT>
							</td>



							<th align="right">
								供应商
							</th>

							<td>
								<input type="text" value="${manualPlan.gysName}"
									name="manualPlan.gysName">
							</td>

							<th align="right">
								采购员
							</th>

							<td>
								<input type="text" value="${manualPlan.addUsername}"
									name="manualPlan.addUsername">
							</td>

							<th align="right">
								下单时间从
							</th>
							<td>
								<input class="Wdate" type="text" name="manualPlan.startTime"
									value="${manualPlan.startTime}" size="15"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"
									id="startTime" />
								到
								<input class="Wdate" type="text" name="manualPlan.endTime"
									value="${manualPlan.endTime}" size="15"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"
									id="endTime" />
							</td>
							<th align="right" colspan="2">

							</th>




						</tr>
					</table>
					<input type="hidden" value="${status}" name="status">
					<input type="hidden" value="${tag}" name="tag">
					<input type="hidden" value="${flag}" name="flag">
					<input type="submit" value="查询" class="input" />
					<input type="button" value="导出" onclick="exportExcel(this.form);todisabledone(this)" data="downData"
						class="input" />
				</form>


				<form action="ManualOrderPlanAction_plshehe.action" method="POST"
					onsubmit="return check()">
					<table class="table">
						<tr align="center" bgcolor="#c0dcf2" height="50px"
							ondblclick="showmingxi('')">
							<s:if test='tag=="daoru"'>
								<th>
									<input type="checkbox" onclick="chageAllCheck(this)">
								</th>
							</s:if>
							<th>
								序号
							</th>

							<th>
								物料类别
							</th>

							<th>
								件号
							</th>
							<th>
								零件名称
							</th>
							<th>
								采购类别
							</th>
							<th>
								规格
							</th>
							<th>
								单位
							</th>
							<th>
								供料属性
							</th>
							<th>
								版本号
							</th>
							<th>
								状态
							</th>
							<th>
								需求量
							</th>
							<th>
								MOQ数量
							</th>
							<th>
								已采购量
							</th>
							<th>
								未送货数量
							</th>
							<th>
								签收数量
							</th>
							<th>
								合格数量
							</th>
							<th>
								到货数量
							</th>
							<th>
								取消数量
							</th>
							<th>
								供应商
							</th>

							<th>
								采购员
							</th>

							<th>
								计划时间
							</th>
							<th>
								下单时间
							</th>
							<th>
								数量变更审批动态
							</th>
							<th>
								需求部门
							</th>
							<s:if test='tag!="daoru"'>
								<th>
									操作
								</th>
							</s:if>
						</tr>


						<s:iterator value="manualPlanList" id="pageList"
							status="pagestatus">
							<s:if test="#pagestatus.index%2==1">
								<tr align="center" bgcolor="#e6f3fb"
									onmouseover="chageBgcolor(this)"
									onmouseout="outBgcolor(this,'#e6f3fb')"
									ondblclick="showmingxi(${pageList.id})" class="zong"
									id="zong_${pageList.id}">
							</s:if>
							<s:else>
								<tr align="center" onmouseover="chageBgcolor(this)"
									ondblclick="showmingxi(${pageList.id})"
									onmouseout="outBgcolor(this,'')" class="zong"
									id="zong_${pageList.id}">
							</s:else>
							<td>
								${pagestatus.index+1}
							</td>

							<td align="left">
								${pageList.wgType}
							</td>

							<td align="left">
								${pageList.markId}
							</td>
							<td align="left"
								style="max-width: 150px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
								<font size="1">${pageList.proName}</font>
								<ul class="qs_ul">
									<li>
										${pageList.proName}
									</li>
								</ul>
							</td>
							<td align="left">
								${pageList.category}
							</td>
							<td align="left"
								style="max-width: 150px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
								<font size="1">${pageList.specification}</font>
								<ul class="qs_ul">
									<li>
										${pageList.specification}
									</li>
								</ul>
							</td>
							<td>
								${pageList.unit}
							</td>
							<td>
								${pageList.kgliao}
							</td>
							<td>
								${pageList.banben}
							</td>
							<td>
								${pageList.status}
							</td>
							<td align="right">
								${pageList.number}
							</td>
							<td align="right">
								${pageList.moqNum}
							</td>
							<td align="right">
								${pageList.outcgNumber}
							</td>
							<td align="right">
								${pageList.wshCount}
							</td>
							<td align="right">
								${pageList.qsCount}
							</td>
							<td align="right">
								${pageList.hgCount}
							</td>
							<td align="right">
								${pageList.rukuNum}
							</td>
							<td align="right">
								${pageList.cancalNum}
							</td>
							<td class="lie8" align="right"
								style="max-width: 80px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
								<font size="1">${pageList.gysName}</font>
								<ul class="qs_ul">
									<li>
										${pageList.gysName}
									</li>
								</ul>
							</td>

							<td align="right">
								${pageList.addUsername}
							</td>
							<td align="right">
								${pageList.addtime}
							</td>
							<td align="right">
								${pageList.caigouTime}
							</td>
							<td>

								<a
									href="${pageContext.request.contextPath}/CircuitRunAction_findAduitPage.action?id=${pageList.epCancalId}">${pageList.epCancalStatus}</a>

							</td>
							<td>
<%-- 								${pageList.epCancalStatus} --%>
							</td>
							<td>
								<a href="javascript:;" onclick="tanchu('${pageList.id}')">详情</a>
								<s:if test="tag=='quxiao'">
									<%--									<a href="javascript:;" onclick="quxiao('${pageList.id}')">取消</a>  --%>
									<s:if
										test="#pageList.epCancalStatus!=null&&(#pageList.epCancalStatus=='未审批'||#pageList.epCancalStatus=='打回')">
										<a href="javascript:;"
											onclick="changeCount('${pageList.id}','${pageList.number}','${pageList.outcgNumber}','${pageList.cancalNum}')">修改数量</a>
									</s:if>
									<s:else>
										<a href="javascript:;"
											onclick="changeCount('${pageList.id}','${pageList.number}','${pageList.outcgNumber}','${pageList.cancalNum}')">变更数量</a>
									</s:else>
								</s:if>
							</td>
							</tr>



							<s:if test="#pageList.modLst!=null && #pageList.modLst.size()>0">
								<tr class="mingxi" id="mingxi_${pageList.id}"
									style="display: none;">
									<td colspan="25">
										<table class="table">
											<tr>
												<td>
													件号
												</td>
												<td>
													物料类别
												</td>
												<td>
													名称
												</td>
												<td>
													总成件号
												</td>
												<td>
													总成批次
												</td>
												<td>
													业务件号
												</td>
												<td>
													订单号（内）
												</td>
												<td>
													添加方式
												</td>
												<td>
													需采购量
												</td>
												<td>
													已采购量
												</td>
												<td>
													到货量
												</td>
											</tr>
											<s:iterator value="#pageList.modLst" id="pagemod">
												<tr>
													<td>
														${pagemod.markId}
													</td>

													<td>
														${pagemod.wgType}
													</td>


													<td>
														${pagemod.proName}
													</td>
													<td>
														${pagemod.rootMarkId}
													</td>
													<td>
														${pagemod.rootSelfCard}
													</td>
													<td>
														${pagemod.ywMarkId}
													</td>
													<td>
														${pagemod.orderNumber}
													</td>
													<td>
														${pagemod.type}
													</td>
													<td align="right">
														${pagemod.cgnumber}
													</td>
													<td align="right">
														${pagemod.outcgNumber}
													</td>
													<td align="right">
														${pagemod.rukuNum}
													</td>
												</tr>
											</s:iterator>
										</table>
									</td>
								</tr>
							</s:if>
						</s:iterator>
						<tr>
							<td colspan="31" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />

							</td>
						</tr>
						<s:if test='tag=="daoru"'>
							<tr>
								<td colspan="31" align="center">
									<input type="submit" value="审核" class="input" id="sub"
										onclick="todisabled(this)">
								</td>
							</tr>
						</s:if>
					</table>


				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
function tanchu(num,status){
	document.getElementById("xiugaiIframe").src = "ManualOrderPlanAction_findmanualPlanById.action?id="+ num+"&status="+status;
	chageDiv('block');
}

function toShowWW(status){
	window.location.href="ManualOrderPlanAction_findmanualPlanList.action?status="+status+"&flag=${flag}";
}
function check(){
	var bool = true;
	var selecteds = document.getElementsByName("selected");
	if(selecteds!=null && selecteds.length>0){
		for(var i=0;i<selecteds.length;i++){
			if(selecteds[i].checked == true){
				bool = false;
				break;
			}
		}
	}
	if(bool){
		alert("请至少选择一个审核");
		$("#sub").removeAttr("disabled")
		return false;
	}
	
}


function tanchu(num) {
	if(num!=''){
		document.getElementById("xiugaiIframe").src = "WaigouwaiweiPlanAction!findAllCgxinxi.action?id="
				+ num;
		chageDiv('block')
	}else{
		alert('该零件未有相关物料需求信息');
	}
}
var count =0;
function showmingxi(id){
if(count == 0){
	if(id==''){
		$(".mingxi").show();
		$(".zong").attr("style","font-weight: bold;")
	}else{
		$("#mingxi_"+id).show();
		$("#zong_"+id).attr("style","font-weight: bold;")
	}
	count =1;
}else if(count == 1){
	if(id==''){
		$(".mingxi").hide();
		$(".zong").removeAttr("style");
	}else{
		$("#mingxi_"+id).hide();
		$("#zong_"+id).removeAttr("style");
	}
	count =0;
}
}


function exportExcel(obj){//ManualOrderPlanAction_findmopList.action
		obj.action = "ManualOrderPlanAction_exportExcel.action?daochu=daochu";
	 	obj.submit();
	  	obj.action = "ManualOrderPlanAction_findmopList.action";
}

function testTime() {
				var startStr = document.getElementById("startTime").value;
				var endStr = document.getElementById("endTime").value;
				if (startStr != "" && endStr != "") {
					var start = startStr.split("-");
					var end = endStr.split("-");
					var startTime = new Date(start[0], start[1], start[2]);
					var endTime = new Date(end[0], end[1], end[2]);
					var myDate = new Date(); //获得当前时间
					myDate.setMonth(myDate.getMonth() + 1);//为当前date的月份+1后重新赋值
					if (startTime <= endTime == false) {
						alert("开始时间不能大于结束时间!请重新选择!谢谢!");
						return false;
					} else if (endTime <= myDate == false) {
						alert("结束时间不能大于当前时间!请重新选择!谢谢!");
						return false;
					}
				}
				return true;
}
function quxiao(id){
	if(confirm('确定要取消物料需求吗?')){
		window.location.href="ManualOrderPlanAction_quexiao.action?id="+id;
	}
}
function changeCount(id,number,outcgNumber,cancalNum){
	if(outcgNumber==null || outcgNumber==''){
		outcgNumber=0;
	}
	if(cancalNum==null || cancalNum==''){
		cancalNum=0;
	}
	
	if(number-outcgNumber<=0){
		alert("需求数量小于已采购数量，不能变更数量!");
		return;
	}
	window.location.href="ManualOrderPlanAction_getOrderPlanById.action?id="+id+"&tag=${tag}&cpage=${cpage}";
}

function quxiaoStatus(epId){
	window.location.href="CircuitRunAction_findAduitPage.action?id="+epId;
	
}
</SCRIPT>
	</body>
</html>
