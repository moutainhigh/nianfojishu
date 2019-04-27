<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.task.entity.Users"%>
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
		<script type="text/javascript">
//表单检查
function chackForm() {
	var customer = document.getElementById("customer");//客户
	var markID = document.getElementById("markID");//件号
	var processNO = document.getElementById("processNO");//籍贯
	var deliveryCount = document.getElementById("deliveryCount");//交付数量
	var orderId = document.getElementById("orderId");//订单号
	var yuceCount = document.getElementById("yuceCount");//预测产量
	var procardCycle = document.getElementById("procardCycle");//加工周期
	var productCysle = document.getElementById("productCysle");//产品生命周期
	var osOneRengong = document.getElementById("osOneRengong");//外委单件人工费用
	var osOneMater = document.getElementById("osOneMater");//单件材料
	var osOneOthsers = document.getElementById("osOneOthsers");//单价其他
	var selfOneRengong = document.getElementById("selfOneRengong");//自制所需工资总额
	//var presentAddress = document.getElementById("presentAddress");//现住址
	if (customer.value == "") {
		alert("客户信息不能为空!");
		customer.focus();
		return false;
	} else if (markID.value == "") {
		alert("零件号不能为空!");
		markID.focus();
		return false;
	} else if (processNO.value == "") {
		alert("工序号不能为空!");
		processNO.focus();
		return false;
	} else if (deliveryCount.value == "") {
		alert("交付数不能为空!");
		deliveryCount.focus();
		return false;
	} else if (orderId.value == "") {
		alert("订单号不能为空!");
		orderId.focus();
		return false;
	} else if (yuceCount.value == "") {
		alert("预测产量不能为空!");
		yuceCount.focus();
		return false;
	} else if (procardCycle.value == "") {
		alert("加工周期不能为空!");
		procardCycle.focus();
		return false;
	} else if (productCysle.value == "") {
		alert("产品生命周期不能为空!");
		productCysle.focus();
		return false;
	} else if (osOneRengong.value == "") {
		alert("外委单件人工不能为空!");
		osOneRengong.focus();
		return false;
	} else if (osOneMater.value == "") {
		alert("单价材料不能为空!")
		osOneMater.focus();
		return false;
	} else if (osOneOthsers.value == "") {
		alert("单价其他不能为空!")
		osOneOthsers.focus();
		return false;
	//} else if (selfOneRengong.value == "") {
		//alert("自制所需工资总额不能为空!");
	//	selfOneRengong.focus();
	//	return false;
	} else {
		return true;
	}
}

$(function() {
	//下拉订单号
	$.ajax( {
		type : "POST",
		url : "osaAction!findOrderNum.action",
		dataType : "json",
		success : function(data) {
			$(data).each(
					function() {
						$("<option value='" + this + "'>" + this + "</option>")
								.appendTo("#orderId")
					});
		}
	});

	//报警单号
	$.ajax( {
		type : "POST",
		url : "osaAction!findMaintenance.action",
		dataType : "json",
		success : function(data) {
			var bj = "<option></option>";
			$(data).each(function() {
				bj += "<option value='" + this + "'>" + this + "</option>";
			});
			$(bj).appendTo("#alertNum")
		}
	});
	//下拉原材料
	$.ajax( {
		type : "POST",
		url : "osaAction!tosaveOSApp.action",
		dataType : "json",
		success : function(object) {
			var bj = "<option></option>";
			$.each(object, function(i, obj) {
				$(bj += "<option value='"
						+ [ obj.paihao, obj.guige, obj.jiage, obj.danwei ]
						+ "'>牌号" + obj.paihao + "                规格"
						+ obj.guige + "</option>");
			});
			$(bj).appendTo("#yuancailiao")
		}
	});
});

function jisuanWage() {
	var selfCode = $("#selfCode"), selfRenshu = $("#selfRenshu"), selfOneRengong = $("#selfOneRengong"), selfUserName = $("#selfUserName");
	if (selfCode.val() == "") {
		alert("请填写工号!");
		return false;
	} else {
		//计算所需工资
		$.ajax( {
			type : "POST",
			url : "osaAction!jsWage.action",
			data : {
				selfCode : selfCode.val()
			},
			dataType : "json",
			success : function(data) {
				selfUserName.val(data[0]);
				selfOneRengong.val(data[1]);
				selfRenshu.val(data[2]);
				selfCode.val(data[3])
				if (data[4] != "") {
					alert(data[4]);
				}
			}
		});
	}
}
function getJiaGe(Value) {
	var aaa = Value.split(",");
	document.getElementById("paihao").value = aaa[0];
	document.getElementById("guige").value = aaa[1];
	document.getElementById("jiage").value = aaa[2];
	document.getElementById("danwei").value = aaa[3];

	var shuliang = document.getElementById("shuliang").value;
	if (shuliang != "") {
		var jiage = document.getElementById("jiage").value;
		document.getElementById("osOneMater").value = jiage * shuliang;
	}

}
function getZonge() {
	var jiage = document.getElementById("jiage").value;
	var shuliang = document.getElementById("shuliang").value;
	document.getElementById("osOneMater").value = jiage * shuliang;
}
</script>
	</head>
	<body bgcolor="#ffffff">
		<center>
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
							style="color: #ffffff">添加外委产品申报单</a>
					</div>
				</div>

				<div align="center">
					<form action="osaAction!saveOSApp.action" method="post"
						onsubmit="return chackForm()" enctype="multipart/form-data">
						<table width="100%" border="0" class="table">
							<tr>
								<th colspan="6" align="center">
									外委产品信息登记
								</th>
							</tr>
							<tr>
								<th colspan="6" align="left">
									<br />
									基本信息
									<br />
								</th>
							</tr>
							<tr>
								<th align="right">
									申报部门:
								</th>
								<td>
									<input type="text" name="osa.dept" class="horizontalLine"
										value="${sessionScope.Users.dept}" readonly="readonly"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> *</font>
								</td>
								<th align="right">
									客户名称:
								</th>
								<td>
									<input id="customer" name="osa.customer" class="horizontalLine"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> *</font>
								</td>
								<th align="right">
									加急:
								</th>
								<td>
									<input type="radio" name="osa.isJiaji" value="是"
										checked="checked">
									是
									<input type="radio" name="osa.isJiaji" value="否">
									否
									<font color="red"> *</font>
								</td>

							</tr>
							<tr>

								<th align="right">
									零件号:
								</th>
								<td>
									<input id="markID" name="osa.markID" class="horizontalLine"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> *</font>
								</td>
								<th align="right">
									工序号:
								</th>
								<td>
									<input id="processNO" name="osa.processNO"
										class="horizontalLine" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> *</font>
								</td>
								<th align="right">
									外委交付数量:
								</th>
								<td>
									<input onkeyup="this.value=this.value.replace(/\D/g,'')"
										id="deliveryCount" name="osa.deliveryCount"
										class="horizontalLine" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> 件*</font>
								</td>
							</tr>
							<tr>

								<th align="right">
									交付时间:
								</th>
								<td>
									<input class="Wdate" type="text" name="osa.deliveryDate"
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
								</td>

								<th align="right">
									订单号:
								</th>
								<td>
									<select id="orderId" name="osa.orderId" style="width: 155px;">

									</select>
									<font color="red"> *</font>
								</td>
								<th align="right">
									预测产量:
								</th>
								<td>
									<input id="yuceCount" name="osa.yuceCount"
										class="horizontalLine" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> 件*</font>
								</td>
							</tr>

							<tr>
								<th align="right">
									加工周期:
								</th>
								<td>
									<input id="procardCycle" name="osa.procardCycle"
										class="horizontalLine" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red">天 *</font>
								</td>
								<th align="right">
									产品生命周期:
								</th>
								<td>
									<input id="productCysle" name="osa.productCysle"
										class="horizontalLine" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red">年 *</font>
								</td>
							</tr>

							<tr>
								<th colspan="6" align="left">
									<br />
									申报原因
									<br />
								</th>
							</tr>
							<tr>
								<th align="right">
									申报时限:
								</th>
								<td>
									<input type="radio" name="osa.TimeLimit" value="长期"
										checked="checked">
									长期
									<input type="radio" name="osa.TimeLimit" value="短期">
									短期
									<font color="red"> *</font>
								</td>
								<th align="right">
									报警单号:
								</th>
								<td>
									<select id="alertNum" name="osa.alertNum" style="width: 155px;">

									</select>
									<font color="red"> *</font>
								</td>
								<th align="right">
									维修周期:
								</th>
								<td>
									<input onkeyup="this.value=this.value.replace(/\D/g,'')"
										name="osa.repairCycle" class="horizontalLine"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> 天*</font>
								</td>
							</tr>
							<tr>
								<th align="right">
									故障原因:
								</th>
								<td>
									<select id="nation" name="osa.machineFail"
										style="width: 130px;">
										<option value="">
											请选择原因
										</option>
										<option value="人为破坏">
											人为破坏
										</option>
										<option value="延迟修理">
											延迟修理
										</option>
										<option value="保养不力">
											保养不力
										</option>
										<option value="违章操作">
											违章操作
										</option>
										<option value="修理再故障">
											修理再故障
										</option>
										<option value="设备老化">
											设备老化
										</option>
										<option value="其他">
											其他
										</option>
									</select>
								</td>
								<th align="right">
									维修预算:
								</th>
								<td>
									<input name="osa.repairBudget" class="horizontalLine"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red">元 *</font>
								</td>

								<th align="right">
									上传维修报价:
								</th>
								<td>
									<input type="file" name="machine" style="width: 150px;">
									<font color="red"> *</font>
								</td>
							</tr>
							<tr>
								<th align="right">
									产能不足:
								</th>
								<td>
									<select id="nation" name="osa.abilityLack"
										style="width: 130px;">
										<option value="">
											请选择原因
										</option>
										<option value="人员不足">
											人员不足
										</option>
										<option value="没有设备">
											没有设备
										</option>
										<option value="设备限制">
											设备限制
										</option>
										<option value="工艺限制">
											工艺限制
										</option>

										<option value="其他">
											其他
										</option>
									</select>
								</td>
								<th align="right">
									其他原因:
								</th>
								<td>
									<SELECT name="osa.othersLack" style="width: 130px">
										<option value="">
											请选择原因
										</option>
										<option value="自然灾害">
											自然灾害
										</option>
										<option value="事故">
											事故
										</option>
									</SELECT>
								</td>

								<th align="right">
									其他原因上传:
								</th>
								<td>
									<input type="file" name="othersLack" style="width: 150px;">
								</td>
							</tr>
							<tr>
								<th colspan="6" align="left">
									<br />
									成本核算
									<br />
								</th>
							</tr>
								<tr>
								<th align="right">
									原材料规格 牌号:
								</th>
								<td>
									<select id="yuancailiao" name="yuancailiao"
										style="width: 155px;" onchange="getJiaGe(this.value)"></select>
									<input type="hidden" id="paihao" name="osa.paihao"
										style="width: 50px;" />
									<input type="hidden" id="guige" name="osa.guige"
										style="width: 50px;" />
									<input type="hidden" id="jiage" name="osa.jiage"
										style="width: 50px;" />
								</td>

								<th align="right">
									单件重量:
								</th>
								<td>
									<input type="text" id="shuliang" name="osa.shuliang"
										onchange="getZonge();" />
								</td>
								<th align="right">
								
									单位:
								</th>
								<td>
									<input type="text" id="danwei" name="osa.danwei"
										style="width: 50px;border: 0px;" readonly="readonly" />
								</td>
							</tr>
							<tr>
								<th align="right">
									外委单件人工:
								</th>
								<td>
									<input id="osOneRengong" name="osa.osOneRengong"
										class="horizontalLine" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine');isEmail(this.value)" >
									<font color="red"> 元/件*</font>
								</td>
								<th align="right">
									外委单件材料:
								</th>
								<td>
									<input id="osOneMater" name="osa.osOneMater"
										class="horizontalLine" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')" readonly="readonly" style="border: 0em;" >
									<font color="red"> 元/件*</font>
								</td>
								<th align="right">
									外委单件其他:
								</th>
								<td>
									<input id="osOneOthsers" name="osa.osOneOthsers"
										class="horizontalLine"  onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')" >
									<font color="red"> 元/件*</font>
								</td>
							</tr>

						

							<tr>
								<th align="right">
									员工号(自制):
									<br />
									人数(自制):
								</th>
								<td>
									<input id="selfCode" name="osa.selfCode" class="horizontalLine"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine'),jisuanWage()">
									(员工号以","分割)
									<br />
									<input id="selfRenshu" name="osa.selfRenshu"
										readonly="readonly" class="horizontalLine">
									<font color="red"> 位*</font>
								</td>
								<th align="right">
									姓名(自制):
								</th>
								<td>
									<input id="selfUserName" name="osa.selfCode"
										class="horizontalLine" readonly="readonly">
									<input type="hidden" id="selfOneRengong" value="100"
										name="osa.selfOneRengong" readonly="readonly"
										class="horizontalLine">
								</td>
								<th align="right">
									上传外委报价:
								</th>
								<td>
									<input type="file" name="infor" style="width: 150px;">
									<font color="red"> *</font>
								</td>
							</tr>
							<tr>
								<th colspan="6" align="left">
									<br />
									自制新增成本
									<br />
								</th>
							</tr>
							<tr>
								<th align="right">
									增加人数:
								</th>
								<td>
									<input id="addWorker"
										onkeyup="this.value=this.value.replace(/\D/g,'')"
										name="osa.addWorker" class="horizontalLine" value="0"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')" maxlength="11">
									<font color="red"> 位</font>
								</td>
								<th align="right">
									累计人工成本:
								</th>
								<td>
									<input id="oneWorkerMoney" name="osa.oneWorkerMoney"
										class="horizontalLine" value="0" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> 元/月</font>
								</td>

								<th align="right">
									新增设备报价:
								</th>
								<td>
									<input type="file" name="abilityLack" style="width: 150px;">
								</td>
							</tr>
							<tr>
								<th align="right">
									增加设备成本:
								</th>
								<td>
									<input id="addMachineCost" name="osa.addMachineCost"
										class="horizontalLine" value="0" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> 元*</font>
								</td>

								<th align="right">
									回收期:
								</th>
								<td>
									<input id="recoryPeriod" name="osa.recoryPeriod"
										class="horizontalLine" value="9" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> 年*</font>
								</td>
							</tr>


							<tr>
								<th align="right">
									增加辅助人数:
								</th>
								<td>
									<input id="addAssistWorker"
										onkeyup="this.value=this.value.replace(/\D/g,'')"
										name="osa.addAssistWorker" class="horizontalLine" value="0"
										onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')" maxlength="11">
								</td>
								<th align="right">
									辅助累计成本:
								</th>
								<td>
									<input id="oneAssistWorkerMoney"
										name="osa.oneAssistWorkerMoney" class="horizontalLine"
										value="0" onfocus="chageClass(this,'')"
										onblur="chageClass(this,'horizontalLine')">
									<font color="red"> 元/月</font>
								</td>
							</tr>
							<tr>
								<td colspan="6" align="center">
									<br />
									<br />
									<input type="submit" value="添加"
										style="width: 80px; height: 50px;" />
									<input type="reset" value="重置"
										style="width: 80px; height: 50px;" />
									<br />
									<br />
								</td>
							</tr>
						</table>
					</form>
					<div>
						<font color="red">${successMessage}</font>
						<font color="red">${errorMessage}</font>
					</div>
				</div>
				<br>
			</div>
			<%@include file="/util/foot.jsp"%>
		</center>
	</body>
</html>
