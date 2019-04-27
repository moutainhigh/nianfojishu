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
	<body>
		<div id="bodyDiv" align="center" class="transDiv" style="z-index: 2"
			onclick="chageDiv('none')">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none; top: 20px;"
			align="center">
			<div id="closeDiv"
				style="background: url(<%=basePath%>/images/bq_bg2.gif); width: 100%;">
				<div id="submitProcessDiv" style="display: none;">
					<table style="width: 100%; margin-top: ">
						<tr>
							<td>
								您正在上传发票:
							</td>
							<td align="right">
								<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
									height="32" onclick="chageDiv('none');reload();">
							</td>
						</tr>
					</table>
					<div id="operatingDiv"
						style="background-color: #ffffff; width: 100%;">
						<form action="CorePayableAction!uploadFapiao.action"
							enctype="multipart/form-data" method="post"
							onsubmit="return validate()">
							<input type="hidden" id="coreId" name="corePayable.id" />
							<table class="table" style="width: 40%" align="center">
								<tr>
									<th align="right">
										发票号:
									</th>
									<th align="left">
										<input name="corePayable.fapiaoNum" />
									</th>
								</tr>
								<tr>
									<th align="right">
										发票附件:
									</th>
									<th align="left">
										<input name="attachment" type="file" />
									</th>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<input type="submit" value="提交"
											style="width: 65px; height: 40px;" />
									</td>
								</tr>
							</table>
						</form>
					</div>
				</div>
			</div>
		</div>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%; margin-top: 10px;">
			<div align="center">
				<h3>
					<font color="red">${nonCoreReceivables.chengzufang}</font>主营业务应付表
					<br />
					<s:if test="successMessage!=null">
						<font color="red">${successMessage}</font>
					</s:if>
				</h3>
				<s:if
					test="corePayable==null||corePayable.id==null||corePayable.fk_CPMId==null">
					<form action="CorePayableAction!findCorePaybleList.action"
						method="post">
						<input name="pageStatus" value="${pageStatus}" type="hidden">
						<table class="table">
							<tr>
								<th align="right">
									供应商
								</th>
								<td>
									<input value="${corePayable.supplierName}"
										name="corePayable.supplierName">
								</td>
								<th align="right">
									订单号
								</th>
								<td>
									<input value="${corePayable.orderNumber}"
										name="corePayable.orderNumber">
								</td>
							</tr>
							<tr>
								<th align="right">
									入库时间从
								</th>
								<td>
									<input value="${firstTime}"
										name="firstTime" class="Wdate" type="text"
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd 00:00:00',skin:'whyGreen'})">
								</td>
								<th align="right">
									至
								</th>
								<td>
									<input value="${endTime}"
										name="endTime" class="Wdate" type="text"
										onClick="WdatePicker({dateFmt:'yyyy-MM-dd 23:59:59',skin:'whyGreen'})">
								</td>
							</tr>
							<tr>
								<th align="right">
									送货单号
								</th>
								<td>
									<input value="${corePayable.deliveryNumber}"
										name="corePayable.deliveryNumber">
								</td>
								<th align="right">
									单据编号
								</th>
								<td>
									<input value="${corePayable.rukuNumber}"
										name="corePayable.rukuNumber">
								</td>
							</tr>
							<tr>
								<th align="right">
									订单类型
								</th>
								<td>
									<SELECT name="corePayable.cgOrderType">
										<option value=""></option>
										<option value="${corePayable.rukuNumber}">${corePayable.rukuNumber}</option>
										<option value="外购">外购</option>
										<option value="工序外委">工序外委</option>
										<option value="包工包料">包工包料</option>
										<option value="辅料">辅料</option>
									</SELECT>
								</td>
								<th align="right">
									摘要（件号）
								</th>
								<td>
								<input value="${corePayable.zhaiyao}"
										name="corePayable.zhaiyao">
								</td>
							</tr>
						</table>
						<input type="submit" value="查询" class="input" />
							<input type="button" value="导出" class="input"
								onclick="exprot(this.form);todisabledone(this)" data="downData" />
					</form>
				</s:if>
				<s:if test="pageStatus=='gysdz'">
					<form action="CorePayableAction!gyshdzd.action" method="post"
						onsubmit="return validate()">
				</s:if>
				<s:elseif test="pageStatus=='gysdkp'||pageStatus=='dkp'">
					<form action="CorePayableAction!uploadFapiao.action"
						enctype="multipart/form-data" method="post"
						onsubmit="return validate()">
				</s:elseif>
				<s:elseif test="pageStatus=='fpfh'">
					<form action="CorePayableAction!fpfh.action" method="post"
						onsubmit="return validate()">
				</s:elseif>
				<s:elseif test="pageStatus=='dfk'">
					<form action="CorePayableAction!shenqingfukuan.action"
						method="post" onsubmit="return window.confirm('你确定要申请付款吗？')">
				</s:elseif>
				<div align="left">
				<input type="checkbox" value="供应商" name="" onclick="showtd(this,'gys_td')"/>供应商
				</div>
				<table class="table">
					<s:if test="pageStatus=='dfk'">
						<tr>
							<th colspan="25">
								<s:if test="corePayable.jzMonth!=null">
									<s:if test='qrTag=="true"'>
										<input type="submit" value="申请付款" class="input">
									</s:if>
									<s:else>
										<font color="red">未到付款日期，无法申请付款!</font>
									</s:else>
								</s:if>
								<s:else>
									<input type="submit" value="申请付款" class="input">
								</s:else>
							</th>
						</tr>
					</s:if>
					<tr bgcolor="#c0dcf2" height="50px">
						<%--<s:if
								test="pageStatus=='dfk'||pageStatus=='dkp'||pageStatus=='gysdkp'">
								--%>
						<td align="center">
							<input type="hidden" name="pageStatus" value="${pageStatus}">
							<input type="hidden" name="qrTag" id="qrTag" value="yes">
							<input type="checkbox" id="checkId"
								onclick="chageAllCheck(this,'showCheckDetail')">
						</td>
						<%--</s:if>
							--%>
						<td align="center">
							序号
						</td>
						<td align="center">
							采购订单号
						</td>
						<td align="center">
							摘要(件号)
						</td>
						<td align="center">
							零件名称
						</td>
						<td align="center">
							规格
						</td>
						<td align="center">
							单位
						</td>
						<td align="center">
							含税单价
						</td>
						<td align="center">
							送货单号
						</td>
						<td align="center">
							入库时间
						</td>
						<td align="center">
							入库单号
						</td>
						<td align="center">
							合格数量
						</td>
						<td align="center">
							仓区
						</td>
						<td align="center">
							总额(含税)
						</td>
						<td align="center" class="gys_td" style="display: none;">
							供应商
						</td>
						<td align="center">
							采购员(负责人)
						</td>
						<td align="center">
							不含税单价
						</td>
						<td align="center">
							总额(不含税)
						</td>
						<td align="center">
							送货数量
						</td>
						<td align="center">
							送货时间
						</td>
						<td align="center">
							供货属性
						</td>
						<td align="center">
							类型(采购)
						</td>
						<td align="center">
							业务件号
						</td>
						<td align="center">
							月结天数
						</td>
						<td align="center">
							税率
						</td>
						<td align="center">
							应付金额
						</td>
						<td align="center">
							已付金额
						</td>
						<td align="center">
							未付金额
						</td>
						<td align="center">
							应付款日期
						</td>
						<td align="center">
							需求部门
						</td>
						<td align="center">
							添加人
						</td>
						<td align="center">
							状态
						</td>
						<%--<s:if test="pageStatus=='fpfh'">
							--%>
						<td align="center">
							发票号
						</td>
						<%--</s:if>
						--%>
						<td align="center">
							操作类型
						</td>
					</tr>
					<s:iterator value="corePayableList" id="pagecorePayable"
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
							<s:if test="pageStatus=='gysdz'">
								<s:if test='#pagecorePayable.status=="对账"'>
									<input type="checkbox"
										onclick="chageNum(this,'showCheckDetail')" name="ids"
										value="${pagecorePayable.id}"
										data="${pagecorePayable.yingfukuanJine-pagecorePayable.yipizhunJine}">
								</s:if>
								<s:else>
									<input type="checkbox" disabled="disabled">
								</s:else>
							</s:if>
							<s:else>
								<input type="checkbox"
									onclick="chageNum(this,'showCheckDetail')" name="ids"
									value="${pagecorePayable.id}"
									data="${pagecorePayable.yingfukuanJine-pagecorePayable.yipizhunJine}">
							</s:else>
						</td>
						<td>
							<s:property value="#pageStatus.index+1" />
						</td>
						<td>
							<s:if test="#pagecorePayable.fk_fundApplyId>0">
								<a
									href="FundApplyAction_findfundDetailedList.action?id=${pagecorePayable.fk_fundApplyId}">${pagecorePayable.orderNumber}</a>
							</s:if>
							<s:else>
							${pagecorePayable.orderNumber}
							</s:else>
						</td>
						<td>
							${pagecorePayable.zhaiyao}
						</td>
						<td>
							${pagecorePayable.proName}
						</td>
						<td>
							${pagecorePayable.specification}
						</td>
						<td>
							${pagecorePayable.unit}
						</td>
						<td align="right">
							<a target="_showPriceDetail"
								href="PriceAction!findPriceById.action?id=${pagecorePayable.priceId}&statue=find">
								<fmt:formatNumber type="number"
									value="${pagecorePayable.hsPrice}" pattern="0.0000"
									maxFractionDigits="4" /> </a>
						</td>
						<td>
							<a target="_showWpd"
								href="WaigouwaiweiPlanAction!findWaigouPlanDNDetail.action?id=${pagecorePayable.orderId}">
								${pagecorePayable.deliveryNumber} </a>
						</td>
						<td>
							${pagecorePayable.saveTime}
						</td>
						<td>
							${pagecorePayable.rukuNumber}
						</td>
						<td align="right">
							<fmt:formatNumber type="number" value="${pagecorePayable.number}"
								pattern="0.0000" maxFractionDigits="4" />
						</td>
						<td>
							${pagecorePayable.cangqu}
						</td>
						<td>
							<fmt:formatNumber type="number"
									value="${pagecorePayable.hsPrice*pagecorePayable.number}" pattern="0.00"
									maxFractionDigits="2" />
						</td>
						<td class="gys_td" style="display: none;">
							<a
								href="CorePayableAction!findSupplierCorePayableList.action?supplierCorePayable.supplierId=${pagecorePayable.supplierId}">
								${pagecorePayable.supplierName} </a>
						</td>
						<td>
							${pagecorePayable.fuzeren}
						</td>
						<td align="right">
								<fmt:formatNumber type="number"
									value="${pagecorePayable.bhsPrice}" pattern="0.0000"
									maxFractionDigits="4" />
						</td>
						<td>
							<fmt:formatNumber type="number"
									value="${pagecorePayable.bhsPrice*pagecorePayable.number}" pattern="0.00"
									maxFractionDigits="2" />
						</td>
						<td>
							<fmt:formatNumber type="number"
									value="${pagecorePayable.shNumber}" pattern="0.0000"
									maxFractionDigits="4" />
						</td>
						<td>
							${pagecorePayable.shDte}
						</td>
						<td>
							${pagecorePayable.kgliao}
						</td>
						<td>
							${pagecorePayable.subjectItem}
						</td>
						<td>
							${pagecorePayable.ywmarkId}
						</td>
						<td>
							${pagecorePayable.fukuanZq}
						</td>
						<td align="right">
								${pagecorePayable.taxprice}
						</td>
						<td align="right">
							<fmt:formatNumber type="number"
								value="${pagecorePayable.yingfukuanJine}" pattern="0.00"
								maxFractionDigits="2" />
						</td>
						<td align="right">
							<fmt:formatNumber type="number"
								value="${pagecorePayable.realfukuanJine}" pattern="0.00"
								maxFractionDigits="2" />
						</td>
						<td>
							<fmt:formatNumber type="number"
								value="${pagecorePayable.yingfukuanJine-pagecorePayable.realfukuanJine}"
								pattern="0.00" maxFractionDigits="2" />
						</td>
						<td>
							${pagecorePayable.fukuanDate}
						</td>
						<td>
							${pagecorePayable.demanddept}
						</td>
						<td>
							${pagecorePayable.saveUser}
						</td>
						<td>
							${pagecorePayable.status}
						</td>
						<%--<s:if test="pageStatus=='fpfh'">
							--%>
						<td align="center">
							<s:if test="#pagecorePayable.fapiaoNum!=''">
								<a
									<%--									href="<%=basePath%>/upload/file/fapiao/${pagecorePayable.fujian}">--%>
									href="FileViewAction.action?FilePath=/upload/file/fapiao/${pagecorePayable.fujian}">
									${pagecorePayable.fapiaoNum}</a>

							</s:if>
						</td>
						<%--</s:if>
						--%>
						<td>
							<%--							<s:if test="pageStatus=='dkp'||pageStatus=='gysdkp'">--%>
							<%--								<a href="javascript:;"--%>
							<%--									onclick="addFapiao('${pagecorePayable.id}')">上传发票</a>--%>
							<%--							</s:if>--%>
						</td>
					</s:iterator>
					<tr>
						<td colspan="35" align="left" style="color: red; font-size: 20px;">
							<span id="showCheckDetail"></span>
						</td>
					</tr>
					<s:if test="pageStatus=='dfk'">
						<tr>
							<th colspan="35">
								<s:if test="corePayable.jzMonth!=null">
									<s:if test='qrTag=="true"'>
										<input type="submit" value="申请付款" class="input">
									</s:if>
									<s:else>
										<font color="red">未到付款日期，无法申请付款!</font>
									</s:else>
								</s:if>
								<s:else>
									<input type="submit" value="申请付款" class="input">
								</s:else>
							</th>
						</tr>
					</s:if>
					<s:if test="pageStatus=='gysdz'">
						<tr>
							<th align="left" colspan="35">
								<input type="submit" value="账单无异议" style="height: 40px;" />
								<input type="button" value="账单有异议" style="height: 40px;"
									onclick="tosubmitNo(this.form)" />
							</th>
						</tr>
					</s:if>
					<s:elseif test="pageStatus=='dkp'||pageStatus=='gysdkp'">
						<tr>
							<th align="left" colspan="35">
								发票号:
								<input name="corePayable.fapiaoNum" />
								发票附件:
								<input name="attachment" type="file" />
								<input type="submit" value="提交"
									style="width: 65px; height: 40px;" />
							</th>
						</tr>
					</s:elseif>
					<s:elseif test="pageStatus=='fpfh'">
						<tr>
							<th align="left" colspan="35">
								<input type="submit" value="发票核实无误" style="height: 40px;" />
							</th>
						</tr>
					</s:elseif>
					<tr>
						<s:if test="errorMessage==null">
							<td colspan="35" align="right">
								第
								<font color="red"><s:property value="cpage" /> </font> /
								<s:property value="total" />
								页
								<fenye:pages cpage="%{cpage}" total="%{total}" url="%{url}"
									styleClass="page" theme="number" />
						</s:if>
						<s:else>
							<td colspan="35" align="center" style="color: red">
								${errorMessage}
							</td>
						</s:else>
					</tr>
				</table>
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
//财务确认 
function addFapiao(coreId) {
	$("#coreId").val(coreId);
	$("#submitProcessDiv").show();
	chageDiv("block");
	//单独设置弹出层的高度
	var thisTopHeight = $(obj).offset().top;
	$('#contentDiv').css( {
		'top' : thisTopHeight + 'px'
	});
}

function tosubmitNo(obj) {
	$("#qrTag").val("no");
	obj.submit();
}
function exprot(obj) {
	$(obj).attr("action", "CorePayableAction!exprotCorePayable.action");
	$(obj).submit();
	$(obj).attr("action", "CorePayableAction!findCorePaybleList.action");
}

function showtd(obj,classname){
	if($(obj).attr("checked")=="checked"){
		$("."+classname).show();
	}else{
		$("."+classname).hide();
	}
}
</script>
	</body>
</html>
