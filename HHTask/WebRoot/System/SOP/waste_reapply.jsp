<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
.ztree li a {
	color: #fff;
}

/* 带复选框的下拉框 */
ul li {
	list-style: none;
	padding: 0px;
	margin: 0px;
}

.select_checkBox {
	border: 0px solid red;
	position: relative;
	display: inline-block;
}

.chartQuota {
	height: 23px;
	float: left;
	display: inline-block;
	border: 0px solid black;
	position: relative;
}

.chartOptionsFlowTrend {
	z-index: 300;
	background-color: white;
	border: 1px solid gray;
	display: none;
	position: absolute;
	left: 0px;
	top: 23px;
	width: 150px;
}

.chartOptionsFlowTrend ul {
	float: left;
	padding: 0px;
	margin: 5px;
}

.chartOptionsFlowTrend li { /* float:left; */
	display: block;
	position: relative;
	left: 0px;
	margin: 0px;
	clear: both;
}

.chartOptionsFlowTrend li * {
	float: left;
}

a:-webkit-any-link {
	color: -webkit-link;
	text-decoration: underline;
	cursor: auto;
}

.chartQuota p a {
	float: left;
	height: 21px;
	outline: 0 none;
	border: 1px solid #ccc;
	line-height: 22px;
	padding: 0 5px;
	overflow: hidden;
	background: #eaeaea;
	color: #313131;
	text-decoration: none;
}

.chartQuota p {
	margin: 0px;
	folat: left;
	overflow: hidden;
	height: 23px;
	line-height: 24px;
	display: inline-block;
}

.chartOptionsFlowTrend p {
	height: 23px;
	line-height: 23px;
	overflow: hidden;
	position: relative;
	z-index: 2;
	background: #fefbf7;
	padding-top: 0px;
	display: inline-block;
}

.chartOptionsFlowTrend p a {
	border: 1px solid #fff;
	margin-left: 15px;
	color: #2e91da;
}
</style>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="bodyDiv" align="center" class="transDiv" onclick="chageDiv('none')">
		</div>
		<div align="center">
			<h2>您正在进行处理废品申请操作</h2>
			<table class="table" style="width: 95%;">
				<tr bgcolor="#c0dcf2" height="30px"
					style="border-collapse: separate;">
					<th align="center">序号</th>
					<th align="center">仓区</th>
					<th align="center">库位</th>
					<th align="center">件号</th>
					<th align="center">供料属性</th>
					<th align="center">规格</th>
					<th align="center">物料类别</th>
					<th align="center">品名</th>
					<th align="center">供应商</th>
					<th align="center" >入库类型</th>
					<th align="center" >入库时间</th>
					<th align="center">库存数量</th>
					<th align="center">单位</th>
					<th align="center">原价格（单价）</th>
					<th align="center">处理数量</th>
					<th align="center">处理价格</th>
					<th aligh="center">操作</th>
				</tr>
			<form action="wasteDisposeAction!reapplywd.action" method="post" id="frm">
				<input type="hidden" name="wasteDisponsalTotal.totalId" value="${wasteDisponsalTotal.totalId}"/>
				<input type="hidden" name="wasteDisponsalTotal.epId" value="${wasteDisponsalTotal.epId}" />
				<s:if test="{wasteDisponsalList.size()>0}">
					<s:iterator value="wasteDisponsalList" status="see" id="wdt">
						<s:if test="#see.index%2==1">
							<tr align="center" bgcolor="#e6f3fb" onmouseover="chageBgcolor(this)" onchange="outBgcolor(this,'#e6f3fb')">
						</s:if>
						<s:else>
							<tr align="center" onmouseover="chageBgcolor(this)" onmouseout="outBgcolor(this,'')">
						</s:else>
							<td><s:property value="#see.index+1" /></td>
							<td align="left" style="color: gray;">${gs.goodssHouseName}</td>
							<td align="left" style="color: gray;">${gs.goodsPosition}</td>
							<td align="right">${wdt.partNumber}</td>
							<td align="right">${wdt.goodsKgliao}</td>
							<td align="center">${wdt.goodsFormat}</td>
							<td align="right">${wdt.goodsWgType}</td>
							<td align="right">${wdt.goodsFullName}</td>
							<td align="right">${wdt.goodsSupplier}</td>
							<td align="right">${wdt.goodsStyle}</td>
							<td align="right">${wdt.goodsChangeTime}</td>
							<td align="right">${wdt.goodsPrice}</td>
							<td align="right" class="goodsCurQuantity">${wdt.goodsCurQuantity}</td>
							<td align="right">${wdt.goodsUnit}</td>
							<td align="right">
								<input type="hidden" name="wdId" value="${wdt.id}"/>
								<input type="text" size="7" name="reducedNum" onchange="reducedNumBlur(this)" class="reducedNum" value="${wdt.disposeNum}"/>
							</td>
							<td align="right">
								<input type="text" size="7" name="reducedPrice" class="disposePrice" value="${wdt.disposePrice}"/>
							</td>
							<td align="center">
								<input type="button" onclick="delwasteDis(this,${wdt.id})" value="删除"/>
							</td>
						</tr>
					</s:iterator>
					</table>
					<table class="table">
						<tr>
							<th align="right" width="500px">购买方信息：</th>
							<th align="left">
								<input type="text" name="wasteDisponsalTotal.sellToName"  size="80"/>
							</th>
						</tr>
						<tr>
							<th align="right">购买人：
							<th align="left">
								<input type="text" name="wasteDisponsalTotal.sellPerson" size="80"/>
							</th>
						</tr>
						<tr>
							<th align="right">联系电话：
							<th align="left">
								<input type="text" name="wasteDisponsalTotal.sellPhone" size="80"/>
							</th>
						</tr>
						<tr>
							<th align="right">
								处理说明：
							</th>
							<th align="left">
								<input type="text" name="wasteDisponsalTotal.explain" size="80"/>
							</th>
						</tr>
					<tr>
						<td colspan="17" align="center">
							<input id="buttonKeyDown" type="button" style="height: 40px" value="再次申请报废单"> 
							<input onclick="location.href='wasteDisposeAction!findWhereWaste.action'" type="button" style="height: 40px" value="返回"> 
						</td>
					</tr>
				</form>
				</s:if>
				<s:else>
					<tr>
						<td colspan="21" style="font-size: 15px; color: red;">
							对不起，没有查到相关的库存信息
						</td>
					</tr>
				</s:else>
			</table>
		</div>
		<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		<script type="text/javascript">
		//鼠标离开事件
		function reducedNumBlur(number){
			//var reducedNum = $(checkbox).parent().parent().children("td").children(".goodsCurQuantity").text();
			//.parent().children(".goodsCurQuantity")
			var goodsCurQuantity = $(number).parent().parent().find(".goodsCurQuantity").html();//text().trim();
			//alert(goodsCurQuantity);
			
			var reducedNum = $(number).val().trim();
			//alert(reducedNum+" "+goodsCurQuantity);
			/*if(reducedNum>goodsCurQuantity || reducedNum<=0){
				alert("待处理的数量不正确");
				$(number).attr("value",goodsCurQuantity);
				$(number).focus();
			}*/
		}
		function delwasteDis(element,id){
			var goodsCurQuantity = $(element).parent().parent().html();//text().trim();
			if(confirm("确定要删除吗？")){
				alert(id);
				//$(element).parent().parent().remove();
				
			}
		}
		</script>
		<script type="text/javascript">
			$(document).ready(function(){
				$("#buttonKeyDown").click(function(){
					$.ajax({
						cache:true,
						type:"POST",
						url:"wasteDisposeAction!reapplywdCommit.action",
						data:$("#frm").serialize(),
						success:function(data){
							alert("再次申请成功");
							window.location.href="wasteDisposeAction!findWhereWaste.action";
						},
						error:function(){
							alert("再次申请失败");
						}
					});
				});
			});
		</script>
	</body>
</html>
