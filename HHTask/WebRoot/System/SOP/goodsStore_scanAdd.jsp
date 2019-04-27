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
						style="color: #ffffff">刷新</a>
				</div>
			</div>
			<div align="center">
				<h2>
					流水卡办理入库
				</h2>
				<form action="RunningWaterCardAction!saveGoodsStore.action"
					onsubmit="return checkGoodsStore()" method="post">
					<s:if test="%{'barcode'==tag}">
						<input type="hidden" name="id" value="${sticker.id}" />
					</s:if>
					<s:else>
						<input type="hidden" name="id" value="${runningWaterCard.id}" />
					</s:else>
					<input type="hidden" name="tag" value="${tag}" />
					<input type="hidden" name="procard.id" value="${procard.id}" />
					<table class="table">
						<tr>
							<th>
								批次:
							</th>
							<td>
								<input type="text" name="goodsStore.goodsStoreLot"
									value="${procard.selfCard }" readonly="readonly" />
							</td>
							<th>
								件号:
							</th>
							<td>
								<input type="text" name="goodsStore.goodsStoreMarkId"
									value="${procard.markId }" readonly="readonly" />
							</td>
							<th>
								名称:
							</th>
							<td>
								<input type="text" name="goodsStore.goodsStoreGoodsName"
									value="${procard.proName }" readonly="readonly" />
							</td>
						</tr>
						<tr>
							<th>
								数量:
							</th>
							<td>
								<input id="goodsStoreCount" type="text"
									name="goodsStore.goodsStoreCount"
									value="<fmt:formatNumber value="${procard.hgNumber-procard.rukuCount}" pattern="#.0000"/>"
									onkeyup="checkCount()" />
							</td>
							<th>
								单位:
							</th>
							<td>
								<select name="goodsStore.goodsStoreUnit" id="danwei">
									<option>
										件
									</option>
								</select>
							</td>
							<th>
								客户:
							</th>
							<td>
								<s:if test="%{'barcode'==tag}">

									<input type="text" name="goodsStore.goodsStoreCompanyName"
										value="${procard.gys}" />
									<!-- 千万不要被表面迷惑了啊，这个gys就是客户，不信话，查代码啊！反正你都会审查元素了。-->	
								</s:if>
								<s:else>
									<input type="text" name="goodsStore.goodsStoreCompanyName"
										value="${procard.gys }" />

								</s:else>

							</td>
						</tr>
						<tr>
							<th>
								业务件号:
							</th>
							<td>
								<input id="ywmarkId" type="text"
									name="goodsStore.ywmarkId"
									value="${procard.ywMarkId}"/>
									
							</td>
							<th>
								内部订单号:
							</th>
							<td>
								<input id="neiorderId" type="text"
									name="goodsStore.neiorderId"
									value="${procard.orderNumber}"/>
							</td>
						</tr>
						<tr id="phone_tr" >
							<th>库位</th>
							<td>
								<input type="text" value="${wn.number}" name="goodsStore.goodsStorePosition"/>
								<input type="hidden" value="${wn.barCode}" name="barCode"/>
							</td>
							<th>库位状态:</th>
							<td>
								<input type="radio" value="未满" name="wn_status" checked="checked"/>未满
								<input type="radio" value="已满" name="wn_status"/>已满
								<input type="hidden" value="phone" name="message" />
							</td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td colspan="3">
								<font style="color: red; size: 12;">${message }</font>
							</td>
						</tr>
						<th colspan="6">
							<input type="submit" value="提交" class="input"  id = "sub" />
							&nbsp;
							<input type="reset" value="放弃" class="input" />
						</th>
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
	getUnit("danwei");
	if (/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
			
	}else{
		$("#phone_tr").remove();
	}
	
	
})
//提交验证
function checkCount() {
	var maxRuku = parseFloat("${procard.tjNumber-procard.rukuCount}");
	var nowRuku = parseFloat($("#goodsStoreCount").val());
	if (nowRuku > maxRuku) {
		alert("入库量不能大于" + maxRuku);
		$("#goodsStoreCount").val(maxRuku);
	}
	
}

function checkGoodsStore(){
	$("#sub").attr("disabled","disabled");
}

</script>
	</body>
</html>
