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
		<div id="gongneng">

			<div align="center">
				<form action="PrintedOutAction_findSellprintList.action"
					method="post">
					<input type="hidden" name="status" value="${status}">
					<input type="hidden" name="tag" value="${tag}" id="tag">
					<table class="table" style="width: 95%;">
						<tr>
							<th>
								件号:
							</th>
							<td>
								<input type="text" name="sell.sellMarkId"
									value="${sell.sellMarkId }" />
							</td>
							<th>
								业务件号:
							</th>
							<td>
								<input type="text" name="sell.ywmarkId"
									value="${sell.ywmarkId }" />
							</td>
							<th>
								客户：
							</th>
							<td>
								<input type="text" name="sell.sellCompanyName"
									value="${sell.sellCompanyName}" />
							</td>
						</tr>
						<tr>
							<th>
								生产批次:
							</th>
							<td>
								<input type="text" name="sell.sellLot" value="${sell.sellLot}" />
							</td>
							<th>
								库别:
							</th>
							<td>
								<select name="sell.sellWarehouse" id="wareHouseName" onchange="changvalue(this)" >
									<option value="${sell.sellWarehouse}">${sell.sellWarehouse}</option>
									<option value=""></option>
								</select>
							</td>
							<th>
								仓区:
							</th>
							<td>
								<select name="sell.goodHouseName" id="goodHouseName">
									<option value="${sell.goodHouseName}">${sell.goodHouseName}</option>
									<option value=""></option>
								</select>
							</td>
						</tr>
						<tr>
							<th>
								领料人:
							</th>
							<td>
								<input type="text" name="sell.sellCharger"
									value="${sell.sellCharger}" />
							</td>
							<th>
								总成件号:
							</th>
							<td>
								<input type="text" name="sell.sellProMarkId"
									value="${sell.sellProMarkId}" />
							</td>
							<th>
								打印单号:
							</th>
							<td>
								<input type="text" name="sell.printNumber"
									value="${sell.printNumber}" />
							</td>
						</tr>
						<tr>
							<th>
								出库日期从:
							</th>
							<td>
								<input type="text" name="startTime"
									value="${startTime}"  onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"
									class="Wdate"/>
							</td>
							<th>
								止:
							</th>
							<td>
								<input type="text" name="endTime"
									value="${endTime}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"
									class="Wdate"/>
							</td>
							<th>
								内部订单号:
							</th>
							<td>
								<input type="text" name="sell.orderNum" value="${sell.orderNum}" />
							</td>
						</tr>
					</table>
					<input type="button" value="查找" class="input" onclick="showOrHid_PrintNumber(this.form,'')" />
					<input type="button" onclick="showOrHid_PrintNumber(this.form,'show')" value="查找(显示单号)" style="width: 175px;height:55px;" />
				</form>
				<form action="sellAction!printStorage.action" method="post"
					onsubmit="return vali()">
					<table class="table" style="width: 95%;">
						<tr bgcolor="#c0dcf2" height="30px"
							style="border-collapse: separate;">
							<th>
								<input type="checkbox" id="checkAll" onchange="checkAllBoxs()">
								全选未打印
							</th>
							<th align="center">
								序号
							</th>
							<th align="center" style="width: 57px;">
								客户
							</th>
							<th align="center">
								打印单号
							</th>
							<th align="center">
								业务件号
							</th>
							<th align="center">
								件号
							</th>
							<th align="center">
								生产批次
							</th>
							<th align="center">
								产品名称
							</th>
							<th align="center">
								规格型号
							</th>
							<th align="center">
								单位
							</th>
							<th align="center">
								数量
							</th>
							<th align="center">
								送货单号
							</th>
							<th align="center">
								外部订单号
							</th>
							<th align="center">
								内部订单号
							</th>
							<th align="center">
								出库类型
							</th>
							<th align="center">
								库别
							</th>
							<th align="center">
								仓区
							</th>
							<th align="center">
								领料人
							</th>
							<th align="center">
								出库日期
							</th>
						</tr>
						<s:iterator value="list" status="se" id="sellPrint">

							<tr align="center" bgcolor="#FFB6C1"
								onmouseover="chageBgcolor(this)"
								onmouseout="outBgcolor(this,'#FFB6C1')">
								<td>
									<input type="checkbox" name="selected"
										value="${sellPrint.sellId}" class="toCheckSingle"
										onchange="checkSingle()" />
								</td>
								<td>
									<s:property value="#se.index+1" />
								</td>
								<td>
									${sellPrint.sellCompanyName}
								</td>
								<td>
									${sellPrint.printNumber}
								</td>
								<td>
									${sellPrint.ywmarkId}
								</td>
								<td>
									${sellPrint.sellMarkId}
								</td>
								<td>
									${sellPrint.sellLot}
								</td>
								<td>
									${sellPrint.sellGoods}
								</td>
								<td>
									${sellPrint.sellFormat}
								</td>
								<td>
									${sellPrint.sellUnit}
								</td>
								<td>
									${sellPrint.sellCount}
								</td>
								<td>
									${sellPrint.sellSendnum}
								</td>
								<td>
									${sellPrint.outOrderNumer}
								</td>
								<td>
									${sellPrint.orderNum}
								</td>
								<td>
									${sellPrint.style}
								</td>
								<td>
									${sellPrint.sellWarehouse}
								</td>
								<td>
									${sellPrint.goodHouseName}
								</td>
								<td>
									${sellPrint.sellCharger}
								</td>
								<td>
									${sellPrint.sellDate}
								</td>
							</tr>
						</s:iterator>
						<tr>
							<td colspan="30" align="right">
								共
								<s:property value="total" />
								页 第
								<s:property value="cpage" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />

							</td>
						</tr>
						<tr>
							<td colspan="22">
								<input type="hidden" value="print_sell" name="tag" />
								<input type="hidden" value="${status}" name="status" />
								<input type="submit" style="width: 80px; height: 50px;"
									value="打印" onclick="todisabled(this)" />
							</td>
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
$(function() {
	$.ajax( {
		type : "POST",
		url : "WarehouseAreaAction_getAllwarehouse.action",
		data : {
		},
		dataType : "json",
		success : function(data) {
			if (data != null) {
				$(data).each(function(){
						$("#wareHouseName").append('<option value='+this.name+'>'+this.name+'</option>');
					});
				$("#wareHouseName").tinyselect();
			}
		}
	})
})
function changvalue(obj) {
	if (obj != null && obj.value != "") {
		$
				.ajax( {
					type : "POST",
					url : "WarehouseAreaAction_findwaListByNO.action",
					data : {
						wareHouseName : obj.value
					},
					dataType : "json",
					success : function(data) {
						if (data != null && data.length > 0) {
							$("#goodHouseName").empty();
							$("#goodHouseName").append('<option value="${sell.goodHouseName}">${sell.goodHouseName}</option>');
							$("#goodHouseName").append('<option value=""></option>');
							$(data).each(
									function() {
										$("#goodHouseName").append(
												'<option value='
														+ this.goodHouseName
														+ '>'
														+ this.goodHouseName
														+ '</option>');
									});
							$("#goodHouseName").tinyselect();
							var tinyselect = $(".tinyselect");
				if (tinyselect[2] != null) {
							$(tinyselect[2]).remove();
						}
						}
					}
				});
	}
}
function vali() {
	var nums = document.getElementsByName("selected");
	for ( var i = 0; i < nums.length; i++) {
		if (nums[i].checked) {
			return true;
		}
	}
	alert("请选择需要打印的记录！谢谢");
	return false;
}

function checkAllBoxs() {
	var checkAll = document.getElementById("checkAll");
	var checkboxs = $(".toCheckSingle");
	if (checkAll.checked == true) {
		for ( var i = 0; i < checkboxs.length; i++) {
			checkboxs[i].checked = true;
		}
	} else {
		for ( var i = 0; i < checkboxs.length; i++) {
			checkboxs[i].checked = false;
		}
	}

}

function checkSingle() {
	var checkAll = document.getElementById("checkAll");
	var checkboxs = $(".toCheckSingle");
	var count = 0;
	for ( var i = 0; i < checkboxs.length; i++) {
		if (checkboxs[i].checked == false) {
			checkAll.checked = false;
			return;
		} else {
			count++;
		}
	}
	if (count == checkboxs.length) {
		checkAll.checked = true;
	}
}
function addSelect() {
	$
			.ajax( {
				type : "POST",
				url : "GoodsStoreAction!getViewAuth.action",
				data : {},
				dataType : "json",
				success : function(msg) {
					if (msg.success) {
						for (k in msg.data) {
							$('#whView').append(
									"<option>" + msg.data[k] + "</option>");
						}
					} else {
						alert(msg.message);
					}
				}
			});

}

function showOrHid_PrintNumber(obj,tag){
	$("#tag").val(tag);
	$(obj).submit();
}
addSelect();
</script>
	</body>
</html>
