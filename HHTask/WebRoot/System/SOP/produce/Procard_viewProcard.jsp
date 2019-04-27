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
		url : "ProcardAction!findProByBel.action",
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
						var lingliaowidth = 0;
						var tjNumber = procard.tjNumber;
						if (tjNumber != null) {
							width = (procard.tjNumber / procard.filnalCount * 100)
									.toFixed(0);
							if(width>100){
								backGround="#FFD700";
								width=100;
							}
							nextWidth = 100 - width;
						} else {
							tjNumber = 0;
						}
						var flstatus = "";
						//if (procard.procardStyle == '自制'
						//		|| procard.procardStyle == '总成') {
							flstatus += "(<b><font color='red'>"
									+ procard.status + "</font></b>)";
						//}
						if(procard.wwblCount!=null&&procard.wwblCount>0){
							flstatus +="(<b><font color='red'>包"
									+ procard.wwblCount + "</font></b>)"
						}
						var otherStatus = "";
						if (procard.procardStyle == '外购'
								&& procard.needProcess == 'yes') {
							flstatus += "(<b><font color='red'>"
									+ procard.status + "</font></b>)";
							otherStatus = "-半成品";
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
						var proName = procard.proName;
						if (procard.lingliaostatus == '否'
								|| procard.cgStatus == '否') {
							proName = "<font color='red'>[<b>"
									+ procard.proName + "</b>]</font>";
						}

						var nextWidthCon = "[<b>" + procard.procardStyle
								+ otherStatus + "</b>]" + flstatus
								+ proName + "<br/>" + banben
								+ procard.markId + "(<b>" + tjNumber + "/"
								+ procard.filnalCount + "</b>)";
						var widthCon = "";
<%--						if (width >60) {--%>
<%--							color = "#ffffff";--%>
<%--							widthCon = nextWidthCon;--%>
<%--							nextWidthCon = "";--%>
<%--						}--%>
						var lingliaoHTML = "";
						if(width > 0 && procard.procardStyle == '外购' ){
							var allwid=130;
							lingliaowidth =100- (procard.hascount/procard.filnalCount*100).toFixed(0);
							width = width-lingliaowidth;
							if(width<0){
								width =0;
							}
							nextWidth = 100-lingliaowidth-width;
								color = "#000000";
							if (lingliaowidth >=100) {
								widthCon = nextWidthCon;
								nextWidthCon = "";
								allwid=100;
							}
							lingliaoHTML = "<div align='left'  style='float: left;background-color: #2A8FBD;width:" 
											+lingliaowidth
											+ "%;text-overflow:ellipsis;white-space: nowrap;height:40px; border:  #2A8FBD;opacity:"+(lingliaowidth/allwid)+"'>"
											+""
											+"</div>"
							//widthCon="";
						}
						//异常
						if(width>100){
							backGround="#FFD700";
							width=100;
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
										+ "') ><div align='left' style='float: left;background-color: #ffffff;width:"
										+ nextWidth
										+ "%;text-overflow:ellipsis;white-space: nowrap;height:40px;'>"
										+ nextWidthCon
										+ "</div><div align='left' class='topDiv' style='float: left;background-color: "+backGround+";width:"
										+ width
										+ "%;text-overflow:ellipsis;white-space: nowrap;height:40px;'>"
										+widthCon
										+ "</div>"
										+lingliaoHTML
										+"<div style='clear: both;'></dvi></div>")
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
function showProcessForSb(id, jihuoStatus, proStatus) {
	//历史查看,不显示工序领取
	if ('${pageStatus}' == 'history' || '${pageStatus}' == 'viewUpdate') {
		$("#showProcess").attr(
				"src",
				"ProcardAction!findProcardByRunCard2.action?id=" + id
						+ "&pageStatus=${pageStatus}&viewStatus=${viewStatus}");
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
						style="width: 100%; margin: 0px; padding: 0px; height: 700px;"></iframe>
				</div>
			</div>
		</div>
		<!-- 加载进度 -->
		<div id="processbar" align="left">
			<div class="finish"></div>
		</div>
		<center>

			<div align="center" id="divWatermark"
				style="font-weight: bolder; font-size: 20px; position: absolute; top: 5px; width: 100%; background-color: #000; opacity: 0.7; color: #fff;">
				件号:${procard.markId}
				<s:if test="procard.ywMarkId!=null&&procard.ywMarkId!=''">
					<font color="green">[${procard.ywMarkId}]</font>
				</s:if>
				<span>批次:${procard.selfCard} 订单号:${procard.orderNumber}
					数量:${procard.filnalCount}${procard.unit}</span>
				<div style="clear: both;"></div>
				<div align="center"
					style="font-size: 14px; text-align: center; float: right;">
					<ul>
						<li style="float: left; font-weight: bolder; font-size: 16px;text-align: center">
							${procard.proName}
						</li>
						<li style="float: left; margin-left: 20px; font-size: 16px;">
							[<font style="color:blue;background-color: #ffffff">${procard.productStyle}</font>]
							[
							<a href="javascript:;"
								onclick="javascript:location.href='<%=basePath%>/System/SOP/produce/Procard_gxjdck.jsp?id=${procard.id}&pageStatus=${pageStatus}&viewStatus=${viewStatus}'">工序列表模式</a>]
							[
							<a href="javascript:;"
								onclick="javascript:location.href='<%=basePath%>/System/SOP/produce/Procard_wllbck.jsp?id=${procard.id}&pageStatus=${pageStatus}&viewStatus=${viewStatus}'">物料列表模式</a>]
							[<a href="javascript:;" onclick="javascript:location.reload(true)">刷新</a>]
						</li>
					</ul>
				</div>
				<div style="clear: both;"></div>
				<div align="right"
					style="font-size: 14px; text-align: right; float: right; margin-right: 20px;">
					<ul>
						<li style="height: 20px; float: left; margin-left: 20px;">
							<div>
								<span style="float: left;">未激活:</span>
								<div class=""
									style="float: left; background-color: #ffffff; height: 15px; width: 40px; border: solid 2px #BABABA">
									&nbsp;
								</div>
							</div>
						</li>
						<li style="height: 20px; float: left;">
							<div>
								<span style="float: left;">已激活:</span>
								<div class=""
									style="float: left; background-color: #ffffff; height: 15px; width: 40px; border: solid 2px #008B00">
									&nbsp;
								</div>
							</div>
						</li>
						<li style="height: 20px; float: left;">
							<div>
								<span style="float: left;" title="外购物料配齐">外购配齐:</span>
								<div class=""
									style="float: left; background-color: #008B00; height: 15px; width: 40px; border: solid 2px #008B00">
									&nbsp;
								</div>
							</div>
						</li>
						<li style="height: 20px; float: left;">
							<div>
								<span style="float: left;" title="自制件已经加工完成">自制完成:</span>
								<div class=""
									style="float: left; background-color: #008B00; height: 15px; width: 40px; border: solid 2px #008B00">
									&nbsp;
								</div>
							</div>
						</li>
						<li style="height: 20px; float: left;">
							<div>
								<span style="float: left;" title="外购件已领料">已领料:</span>
								<div class=""
									style="float: left; background-color: #2A8FBD; height: 15px; width: 40px; border: solid 1px #008B00">
									&nbsp;
								</div>
							</div>
						</li>
						<li style="height: 20px; float: left;">
							<div>
								<span style="float: left;" title="总数异常">异常:</span>
								<div class=""
									style="float: left; background-color: #FFD700; height: 15px; width: 40px; border: solid 1px #008B00">
									&nbsp;
								</div>
							</div>
						</li>
					</ul>
				</div>
			</div>
			<!-- 生产进度 -->
			<div id="viewProcard"
				style="width: 99%; border: solid 0px #000000; display: none; margin: 80 0 0 0px;"
				align="center">

			</div>
		</center>
	</body>
</html>
