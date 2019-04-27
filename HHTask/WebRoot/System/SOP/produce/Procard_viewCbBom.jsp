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
		<script src="<%=basePath%>/javascript/jquery.corner.js"
			type="text/javascript">
</script>
		<style type="text/css">
body {
	background: #ffffff;
}

#processbar {
	height: 13px;
	width: 30%;
	margin-top: 200px;
	margin-left: 35%;
	background-color: #fff;
	border: 1px solid #999;
}

#processbar .finish {
	height: 13px;
	width: 40%;
	background-color: #999;
}

.topDiv {
	filter: Alpha(Opacity =      
		                                                         
		         95);
	background: #000;
	opacity: 1;
	-moz-opacity: 0.5;
	-khtml-opacity: 0.5;
	opacity: 0.5;
	background: #000;
}
</style>
		<script type="text/javascript">
$(document).ready(function() {
	$("#processbar").corner();
	$("#processbar .finish").corner();
})
</script>
		<script type="text/javascript">
var maxBelongLayer = parseInt('${maxBelongLayer}');//总层数
var allProcard;
$(function() {
	if (maxBelongLayer < 5) {
		maxBelongLayer = 5;
	}
	var avgWeight = 98 / maxBelongLayer;//平均每层宽度
	var id = "${id}";//rootId
	for ( var i = 1; i <= maxBelongLayer; i++) {
		$(
				"<div id='pro" + i
						+ "' style=' border-left: solid 0px #000000;width: "
						+ avgWeight + "%;float: left;'></div>").appendTo(
				"#viewProcard");
	}
	$.ajax( {
		async : false,
		type : "post",
		url : "ProcardAction!findProByBel.action?pageStatus=cb",
		data : {
			id : id
		},
		dataType : "json",
		success : function(obj) {
			allProcard = obj;
		}
	})

	//递归生成进度图
	findPro(id, 1);
	$("#processbar").hide();
	$("#viewProcard").show();
});

var jdt = 0;
function findPro(fatherId, belongLayer, index) {
	jdt += 5;
	if (jdt <= 100) {
		$("#processbar .finish").css("width", jdt + "%");
	}

	var jiange2 = "<div style='border-left: solid 0px #BABABA;height:54px;'></div>";//间隔2div
	var eachHeight = 0;
	//挑选出所有的子层
	var obj = $.grep(allProcard, function(n, i) {
		if (belongLayer == 1) {
			return n.id == fatherId;
		} else {
			return n.fatherId == fatherId;
		}
	});
	if (obj.length > 0) {
		//生成子层
		$
				.each(obj, function(i, procard) {
					eachHeight += 54;
					//判断激活状态
						var bgcolor = "";
						var color = "#BABABA";
						var cursor = "pointer";
						var jihuoStatus = "wei";
						var borderCorlor = "#BABABA";
						var backGround = "#BABABA";
						if (procard.jihuoStatua == "激活") {
							borderCorlor = " #008B00";
							backGround = "#008B00";
							color = "#000000";
							cursor = "pointer";
							jihuoStatus = "all";
						} else {
							cursor = "pointer";
						}

						//计算提交百分比
						var width = 0;
						var nextWidth = 100;
						var tjNumber = procard.tjNumber;
						if (tjNumber != null) {
							width = (procard.tjNumber / procard.filnalCount * 100)
									.toFixed(0);
							nextWidth = 100 - width;
						} else {
							tjNumber = 0;
						}
						var flstatus = "";
						var wgMoney = 0;
						if (procard.procardStyle == '自制'
								|| procard.procardStyle == '总成') {
							flstatus = "(<b><font color='red'>"
									+ procard.status + "</font></b>)";
						}
						var otherStatus = "";
						if (procard.procardStyle == '外购'
								&& procard.needProcess == 'yes') {
							flstatus = "(<b><font color='red'>"
									+ procard.status + "</font></b>)";
							otherStatus = "-半成品";
						}
						if (procard.procardStyle == '外购') {
							wgMoney = "外购费:" + procard.wgFei;
						} else {
							wgMoney = "人工费:" + procard.rengongfei;
						}

						if (procard.danjiaojian == '是') {
							otherStatus = "-单交件";
						}
						var banben = "";
						if (procard.banBenNumber != ""
								&& procard.banBenNumber != null) {
							banben = "<font color='blue'>[<b>"
									+ procard.banBenNumber + "</b>]</font>";
						}

						var nextWidthCon = "[<b>" + procard.procardStyle
								+ otherStatus + "</b>]" + wgMoney + "<br/>"
								+ banben + procard.markId + "(<b>" + tjNumber
								+ "/" + procard.filnalCount + "</b>)";
						var widthCon = "";
						if (width > 60) {
							color = "#ffffff";
							widthCon = nextWidthCon;
							nextWidthCon = "";
						}

						//生成件号Div
						$(
								"<div align='left' style='overflow:hidden;cursor: "
										+ cursor
										+ "; border: solid 2px "
										+ borderCorlor
										+ ";background-color:"
										+ bgcolor
										+ ";color:"
										+ color
										+ "' onclick=showProcessForSb("
										+ procard.id
										+ ",'"
										+ jihuoStatus
										+ "','"
										+ procard.status
										+ "','"
										+ procard.procardStyle
										+ "','"
										+ procard.markId
										+ "') ><div align='left' style='float: left;background-color: #ffffff;width:"
										+ nextWidth
										+ "%;text-overflow:ellipsis;white-space: nowrap;height:40px;'>"
										+ nextWidthCon
										+ "</div><div align='left' class='topDiv' style='float: left;background-color: #008B00;width:"
										+ width
										+ "%;text-overflow:ellipsis;white-space: nowrap;height:40px;'>"
										+ widthCon
										+ "</div><div style='clear: both;'></dvi></div>")
								.appendTo("#pro" + belongLayer);

						var border = 0;
						if (obj.length - 1 != i) {
							border = 1;
						}

						//每个件号间隔
						$(
								"<div style='border-left: solid " + border
										+ "px " + borderCorlor
										+ ";height:10px;'></div>").appendTo(
								"#pro" + (belongLayer));

						//递归生成
						var sonHeight = findPro(procard.id, belongLayer + 1, i);
						if (sonHeight > 54) {
							eachHeight = eachHeight - 54 + sonHeight;
						}

					});
		//本层最后 一个div的边框清空
		$("#pro" + belongLayer + " div:last")
				.css("border", "solid 0px #000000");

		//计算高度，填补上层的高度
		var fatherHeight = (eachHeight - 54) / 2;
		var border = 0;
		if (index > 0) {
			border = 1;
		}
		//上部
		$("#pro" + (belongLayer - 1) + " div").eq(-5).before(
				"<div style='height:" + fatherHeight + ";border-left: solid "
						+ border + "px #BABABA;'></div>");
		//下部
		$(
				"<div style='border-left: solid 1px #BABABA;height:"
						+ fatherHeight + "px;'></div>").appendTo(
				"#pro" + (belongLayer - 1));

	} else {
		if (belongLayer <= maxBelongLayer) {
			for ( var i = belongLayer; i <= maxBelongLayer; i++) {
				$(jiange2).appendTo("#pro" + (i));
			}
		}
	}
	//}

	return eachHeight;
}

//显示工序详细
function showProcessForSb(id, jihuoStatus, proStatus, procardStyle, markId) {
	//历史查看,不显示工序领取
	if ('${pageStatus}' == 'history' || '${pageStatus}' == 'cb') {
		if ("外购" == procardStyle) {
			$("#showProcess").attr("src",
					"QuotedPrice_allProcardSelsct.action?allId=" + markId);
		} else {
			$("#showProcess").attr(
					"src",
					"ProcardAction!findProcardByRunCard2.action?id=" + id
							+ "&pageStatus=history&viewStatus=zjl");
		}

		chageDiv('block');
	} else {
		if (jihuoStatus != "wei") {
			if (proStatus != "初始" && proStatus != "已发卡") {
				$("#showProcess")
						.attr(
								"src",
								"ProcardAction!findProcardByRunCard2.action?id="
										+ id
										+ "&pageStatus=${pageStatus}&viewStatus=${viewStatus}");
				chageDiv('block');
			} else {
				alert("该生产周转卡尚未领料!请先领料!");
			}
		} else {
			alert("该流水卡片尚未激活!");
		}
	}
}

window.onscroll = function() {
	var oWatermark = document.getElementById("divWatermark");
	oWatermark.style.top = document.body.scrollTop;

}
</script>
	</head>
	<body style="background-color: #ffffff; font-family: 黑体">
		<div id="bodyDiv" align="center" class="transDiv"
			onclick="chageDiv('none')">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 1024px; display: none;"
			align="center">
			<div id="closeDiv">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">工序信息</span>
						</td>
						<td align="right">
							<s:if test="viewStatus=='zjl'">
								<a href="javascript:history.go(-1);">返回</a>
							</s:if>
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<iframe id="showProcess" src="" marginwidth="0" marginheight="0"
						hspace="0" vspace="0" frameborder="0" scrolling="yes"
						style="width: 100%; margin: 0px; padding: 0px;"></iframe>
				</div>
			</div>
		</div>
		<!-- 加载进度 -->
		<div id="processbar" align="left">
			<div class="finish"></div>
		</div>
		<center>
			<div align="center" id="divWatermark"
				style="font-weight: bolder; font-size: 20px; position: absolute; top: 5px; width: 100%">
				${procard.proName}的生产进度查看
				<a href="javascript:;" onclick="javascript:location.reload(true)">
					[刷新]</a> [
				<a href="javascript:;"
					onclick="javascript:location.href='<%=basePath%>/System/SOP/produce/Procard_gxjdck.jsp?id=${procard.id}'">工序列表模式</a>]
				[
				<a href="javascript:;"
					onclick="javascript:location.href='<%=basePath%>/System/SOP/produce/Procard_wllbck.jsp?id=${procard.id}'">物料列表模式</a>]
				<br />
				(件号:${procard.markId}
				<s:if test="procard.ywMarkId!=null&&procard.ywMarkId!=''">
					<font color="green">[${procard.ywMarkId}]</font>
				</s:if>
				批次:${procard.selfCard} 数量:${procard.filnalCount}${procard.unit})
			</div>
			<!-- 生产进度 -->
			<div id="viewProcard"
				style="width: 99%; border: solid 0px #000000; display: none; margin: 50 0 0 0px;"
				align="center">

			</div>
		</center>
	</body>
</html>
