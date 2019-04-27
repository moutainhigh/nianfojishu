<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
	<body onload="createDept('sellchardept')">
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng"
			style="width: 100%; border: solid 1px #0170b8; margin-top: 10px;">
			<div id="xitong"
				style="width: 100%; font-weight: bold; height: 50px;" align="left">
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
				<h2>
					出库管理

					<s:if test="successMessage!=null">
						<br />
						<font color="red"><s:property value="successMessage" /> </font>
					</s:if>
				</h2>
				<form action="goodsAction!updateGoods.action?tag=saveSell"
					onsubmit="return checkFormm()" method="post">
					<input type="hidden" name="role" value="${role}">
					<input type="hidden" name="goods.goodsId" value="${goods.goodsId}">
					<table class="table" style="width: 95%;">
						<tr>
							<th>
								批次:
							</th>
							<td>
								<input type="text" name="sell.sellLot"
									value="${goods.goodsLotId }" readonly="readonly" />
							</td>
							<th>
								件号:
							</th>
							<td>
								<input type="text" name="sell.sellMarkId" id="sellMarkId"
									value="${goods.goodsMarkId }" readonly="readonly" />
								<s:if
									test='goods.goodsClass =="成品库" ||goods.goodsClass =="备货库"  '>
									<span>(<font color="66ff00">${goods.ywmarkId}</font>)</span>
									<input type="hidden" value="${goods.ywmarkId}"
										name="sell.ywmarkId" />
								</s:if>

							</td>
							<th>
								品名:
							</th>
							<td>
								<input type="text" name="sell.sellGoods"
									value="${goods.goodsFullName }" readonly="readonly" />
							</td>
							<th>
								规格：
							</th>
							<td>
								<input type="text" name="sell.sellFormat" id="sellFormat"
									value="${goods.goodsFormat }" readonly="readonly" />
							</td>

							</th>
						</tr>

						<tr>
							<th>
								转换数量:
							</th>
							<td>
								<input type="text" name="sell.sellZhishu" id="sellZhishu"
									value="${goods.goodsZhishu }" onchange=" numyanzhen(this)" />
								<span id="span1"></span>
							</td>
							<th>
								转换单位:
							</th>
							<td>
								<input type="text" name="sell.goodsStoreZHUnit"
									value="${goods.goodsStoreZHUnit }" />
							</td>


							<th>
								出库类型
							</th>
							<td>
								<select name="sell.style" id="sellStyle">
									<option value="">
									</option>
									<option value="工装出库">
										工装出库
									</option>
									<option value="销售出库">
										销售出库
									</option>
									<option value="领料出库">
										领料出库
									</option>
									<option value="返修出库">
										返修出库
									</option>
									<option value="退料出库">
										退料出库
									</option>
									<option value="报废出库">
										报废出库
									</option>
									<option value="转仓出库">
										转仓出库
									</option>
									<option value="损耗出库">
										损耗出库
									</option>
									<option value="研发耗用">
										研发耗用
									</option>
									<option value="售后出库">
										售后出库
									</option>
								</select>
								<span style="color: red">*</span>
							</td>
							<th>
								出库日期
							</th>
							<td>
								<input class="Wdate" type="text" id="goodsChangeTime"
									name="sell.sellDate" size="15"
									onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})" />
								<span style="color: red">*</span>
							</td>

						</tr>
						<tr>
							<th>
								客户:
							</th>
							<td>
								<input type="text" name="sell.sellCompanyName"
									id="sellCompanyName" value="${goods.goodsCustomer }" />
							</td>
							<th>
								负责人
							</th>
							<td>
								<input id="sellCompanyPeople" type="text" value=""
									name="sell.sellCompanyPeople" />
							</td>
							<th>
								领用部门:
							</th>
							<td>
								<select id="sellchardept" name="sell.sellchardept"
									style="width: 155px;">
									<option value="">
										--请选择部门--
									</option>
								</select>
								<span style="color: red">*</span>
								<input id="sellCharger" name="sell.sellCharger" type="hidden" />
								<input id="sellcharId" name="sell.sellcharId" type="hidden" />
							</td>
							<th>
								领料人:
							</th>
							<td id="cangqu_td">
								<select id="sellName" style="width: 155px;"
									onclick="deptNotNull()">
									<option></option>
								</select>
								<span style="color: red">*</span>
							</td>
						</tr>
						<tr>
							<th>
								工艺卡号:
							</th>
							<td>
								<input type="text" name="sell.sellArtsCard"
									value="${goods.goodsArtsCard }" readonly="readonly" />
							</td>
							<th>
								总成件号
							</th>
							<td>
								<input type="text" name="sell.sellProMarkId" />
							</td>
							<th>
								返回状态
							</th>
							<td>
								<select name="sell.sellPeople">
									<option>
										已确认
									</option>
									<option>
										在途
									</option>
								</select>
							</td>
							<th>
								送货单运费(总)
							</th>
							<td>
								<input id="sellSendCost" type="text" name="sell.sellSendCost" />
							</td>
						</tr>
						<tr>
							<th>
								送货单号
							</th>
							<td>
								<input id="sellSendnum" type="text" name="sell.sellSendnum" />
							</td>
							<th>
								订单关联
							</th>
							<td>
								<input id="isNeed1" name="isNeed" type="radio" value="全"
									onclick="showOrder('全')" checked="checked">
								都关联
								<input id="isNeed2" name="isNeed" type="radio" value="外"
									onclick="showOrder('外')">
								仅外部订单号
								<input id="isNeed3" name="isNeed" type="radio" value="否"
									onclick="showOrder('否')">
								否
							</td>
							<th>
								订单号（内部）
							</th>
							<td>
<%--								<s:if test="orderNumberList.size()>0">--%>
<%--									<SELECT id="orderNum" name="sell.orderNum"--%>
<%--										onchange="changeNumber()">--%>
<%--										<option>--%>
<%--											未确定--%>
<%--										</option>--%>
<%--										<s:iterator value="orderNumberList" id="pageNumber">--%>
<%--											<option>--%>
<%--												<s:property value="#pageNumber" />--%>
<%--											</option>--%>
<%--										</s:iterator>--%>
<%--									</SELECT>--%>
<%--								</s:if>--%>
<%--								<s:else>--%>
									<input id="orderNum" type="text" name="sell.orderNum" value="${goods.neiorderId}"/>
<%--								</s:else>--%>
								<%--								<input id="orderNum" type="text" name="sell.orderNum" />--%>
							</td>

							<th>
								订单号（外部）
							</th>
							<td>
								<input name="sell.outOrderNumer" id="outOrderNumer" value="${goods.waiorderId}">
							</td>
						</tr>
						<tr>
							<th>
								库别
							</th>
							<td>
								<input id="warehouse" type="text" name="sell.sellWarehouse"
									value="${goods.goodsClass}" readonly="readonly" />
							</td>


							<th>
								仓区
							</th>
							<td>
								<input id="godHouseName" type="text" name="sell.goodHouseName"
									value="${goods.goodHouseName}" readonly="readonly" />
							</td>
							<!--  库位sell.sellWarehouse-->

							<th>
								库位
							</th>
							<td>
								<input id="warehouse" type="text" name="sell.kuwei"
									value="${goods.goodsPosition}" readonly="readonly" />
							</td>
							<th>
								项目编号：
							</th>
							<td colspan="1">
								<%--								<s:if test="goods.proNumber==null||goods.proNumber==''">--%>
								<%--									<input type="text" name="sell.proNumber"--%>
								<%--										value="${goods.proNumber}" />--%>
								<%--								</s:if>--%>
								<%--								<s:else>--%>
								<input type="text" name="sell.proNumber"
									value="${goods.proNumber}" />
								<%--								</s:else>--%>
							</td>
						</tr>

						<tr>
							<th>
								数量：
							</th>
							<td>
								<%--
								<input type="text" name="sell.sellCount" id="sellCount"
									value="${goods.goodsCurQuantity }" onchange="numyanzhen(this)" />
								
								--%>
								<input type="text" name="sell.sellCount" id="sellCount"
									value="<fmt:formatNumber value="${goods.goodsCurQuantity }" pattern="#0.0000" />"
									onchange="numyanzhen(this)" />

								<span id="span"></span>
							</td>
							<th>
								计量单位：
							</th>
							<td>
								<input type="text" name="sell.sellUnit"
									value="${goods.goodsUnit }" readonly="readonly" />
							</td>
							<th>
								供应商:
							</th>
							<td>
								<input type="text" name="sell.sellSupplier"
									value="${goods.goodsSupplier}" />
							</td>
							<th>
								供料属性${goods.kgliao}
							</th>
							<td>
								<select name="sell.kgliao" style="width: 136px;">
									<option value="${goods.kgliao}" selected="selected">
										<s:if test="goods.kgliao=='TK'">
										自购(TK)
										</s:if>
										<s:elseif test="goods.kgliao=='TK AVL'">
										指定供应商(TK AVL)
										</s:elseif>
										<s:elseif test="goods.kgliao=='CS'">
										客供(CS)
										</s:elseif>
										<s:elseif test="goods.kgliao=='TK Price'">
										完全指定(TK Price)
										</s:elseif>
									</option>
									<%--									<option></option>--%>
									<%--									<option value="TK">--%>
									<%--										自购(TK)--%>
									<%--									</option>--%>
									<%--									<option value="TK AVL">--%>
									<%--										指定供应商(TK AVL)--%>
									<%--									</option>--%>
									<%--									<option value="CS">--%>
									<%--										客供(CS)--%>
									<%--									</option>--%>
									<%--									<option value="TK Price">--%>
									<%--										完全指定(TK Price)--%>
									<%--									</option>--%>

								</select>
							</td>
						</tr>
						<tr>
							<th>
								备注：
							</th>
							<td colspan="7">
								<input type="text" name="sell.sellGoodsMore" style="width: 96%;" />
							</td>
						</tr>
						<tr>
							<th colspan="8">
								<input type="submit" value="出库" class="input" onclick="todisabled(this)"/>
								&nbsp;
								<input type="reset" value="取消" class="input" />
							</th>
						</tr>
					</table>
				</form>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">

var count;
$(function() {
	count = $("#sellCount").val();
});
//提交验证
function checkFormm() {
	var warehouse = document.getElementById("warehouse");
	var sellStyle = document.getElementById("sellStyle");
	var orderNum = document.getElementById("orderNum");
	var countObj = document.getElementById("sellCount");
	var sellCharger = document.getElementById("sellCharger");
	var countVal = $("#sellCount").val().trim();

	if ($("#goodsChangeTime").val() == "") {
		alert("请填写出库时间!");
		$("#goodsChangeTime").focus();
		return false;
	}
	if ($("#sellStyle").val() == "" || $("#sellStyle").val() == null) {
		alert("请出库类型!");
		$("#sellStyle").focus();
		return false;
	}
	
	if ($("#warehouse").val() != '成品库' &&( $("#sellCharger").val() == "" || $("#sellCharger").val() == null)) {
		alert("请选择领料部门和领料人!");
		return false;
	}

	if ($("#sellSendnum").val() != "" && $("#sellCompanyName").val() == "") {
		alert("请填写客户!");
		return false;
	}
	if ($("#sellSendnum").val() != "" && $("#sellCompanyPeople").val() == "") {
		alert("请填写对方负责人!");
		return false;
	}

	//库存数量
	if (countVal == "") {
		alert("请输入出库数量");
		$("#sellCount").focus();
		return false;
	} else {
		var flag = numyanzheng(countObj, "");
		if (!flag) {
			return false;
		} else {
			if ((count - countVal) < 0) {
				alert("出库数量不能大于库存数量");
				$("#sellCount").focus();
				$("#sellCount").val(count);
				return false;
			}
		}
	}

	if (warehouse.value == "成品库" && sellStyle.value == "销售出库") {
		if ($("#sellSendnum").val() == "") {
			alert("成品销售出库时送货单号不能为空!");
			$("#sellSendnum").focus();
			return false;
		}
		if (orderNum.value == "") {
			alert("订单号不能为空!");
			orderNum.focus();
			return false;
		}
	}

	return true;
}
function changeNumber() {
	if ($("#orderNum").val() == '未确定') {

		return;
	}

	//加载所有分组
	$.ajax( {
		type : "post",
		url : "orderManager_getOutNumerByNumber.action",
		dataType : "json",
		data : {
			'orderNum' : $("#orderNum").val()
		},
		success : function(data) {
			//填充部门信息
		if (data != null && data != "") {
			$("#outOrderNumer").val(data);
		}
	}
	});
}
function showOrder(obj) {
	if (obj == "全") {
		$("#orderNum").show();
		$("#outOrderNumer").show();
	} else if (obj == "外") {
		$("#orderNum").hide();
		$("#outOrderNumer").show();
	} else {
		$("#orderNum").hide();
		$("#outOrderNumer").hide();
	}
}
$(function() {
	if ($("#warehouse").val() == "原材料库") {
		$("#span")
				.html(
						'<input type="button" onclick=getbili("sellCount") value="计算"/>');
		$("#span1")
				.html(
						'<input type="button" onclick=getbili("sellZhishu") value="计算"/>');
	}

})

function numyanzhen(obj) {
	var ty1 = '^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$';
	var re = new RegExp(ty1);
	if (obj != null && obj.value.match(re) == null) {
		obj.value = "";
		obj.focus();
		obj.select();
	}
}

function getbili(obj) {
var value = $("#" + obj).val();
if(value !=null && value != ""){
	  	$.ajax( {
		type : "POST",
		url : "yuanclAndWaigjAction!getYWbytrademark.action",
		data : {
			'yuanclAndWaigj.trademark':$("#sellMarkId").val(),
			'yuanclAndWaigj.specification':$("#sellFormat").val(),
		},
		dataType : "json",
		success : function(data) {
			if(data!=null && data>0){
				if(obj == "sellCount"){
					$("#sellZhishu").val((value*data).toFixed(2))
				}else if(obj == "sellZhishu"){
				 	
					$("#sellCount").val((value/data).toFixed(2))
				}
				
			}else{
			alert("该原材料没有相关的单张重量")
		}
		}
	})

  }else{
	  alert("请先输入数量或转换数量")
  }
}
//根据部门显示人员
$(function(){
	//显示部门对应的员工信息
	$("#sellchardept").bind(
			"change",
			function() {
				if ($("#sellchardept").val() != "") {
					$.ajax( {
						url : "UsersAction!findUsersByDept.action",
						type : 'post',
						dataType : 'json',
						cache : false,//防止数据缓存
						data : {
							deptName : $("#sellchardept").val()
						},
						success : function(useradsfa) {
							$("#sellName").empty();//清空
							$("#sellCharger").val("");
							$("#sellcharId").val("");
							$("#ncode").val("");
							$("<option></option>").appendTo("#sellName");
							$(useradsfa).each(
									function() {
										$(
												"<option value='"
															+ this.name + "|"
															+ this.id + "'>"
															+ this.name
															+ "</option>")
												.appendTo("#sellName")
									});
								$("#sellName").bind("change", function() {
								var name = $("#sellName").val();
								var usersData = name.split("|");
								var name1 = usersData[0];
								var nid = usersData[1];
								$("#sellCharger").val(name1);
								$("#sellcharId").val(nid);
							});
							$("#sellName").tinyselect();
							var tinyselect = $(".tinyselect");
							if (tinyselect[2] != null) {
							document.getElementById("cangqu_td").removeChild(
									tinyselect[2]);
						}
						},
						error : function() {
							alert("服务器异常!");
						}
					});
				} else {
					$("#sellName").empty();//清空
					$("#sellCharger").val("");
					$("#sellcharId").val("");
				}
		});
});
function deptNotNull() {
	if ($("#sellchardept").val() == "" || $("#sellchardept").val() == "") {
		alert("领料人部门不能为空！");
		return false;
	}
}
</script>
	</body>
</html>
