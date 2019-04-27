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
.ztree li a {
	color: #fff;
}
</style>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng">

			<div align="center">
				<h3>
					添加外购件
				</h3>
				<%--					<h3><font color="red">${successMessage}</font></h3>--%>
				<form action="yuanclAndWaigjAction_add.action" method="post"
					onsubmit="return validate()">
					<table class="table">
						<%--						<tr>--%>
						<%--							<th align="right">--%>
						<%--								材料类型--%>
						<%--							</th>--%>
						<%--							<td>--%>
						<%--								<SELECT name="yuanclAndWaigj.clClass" id="clClass"--%>
						<%--									onchange="changeClClass()">--%>
						<%--									<option>--%>
						<%--										外购件--%>
						<%--									</option>--%>
						<%--									<option>--%>
						<%--										原材料--%>
						<%--									</option>--%>
						<%--									<option>--%>
						<%--										辅料--%>
						<%--									</option>--%>
						<%--								</SELECT>--%>
						<%--							</td>--%>
						<%--						</tr>--%>

						<tr>
							<th align="right">
								物料类别
							</th>
							<td>
								<div class="zTreeDemoBackground left">
									<ul class="list">
										<li class="title">
											<input id="wgType" type="text" readonly="readonly" value=""
												style="width: 120px;" name="yuanclAndWaigj.wgType" />
											<a id="menuBtn" href="#" onclick="showMenu(); return false;">选择</a><font
												color="red">*</font>
										</li>
									</ul>
								</div>
								<div id="menuContent" class="menuContent">
									<ul id="treeDemo" class="ztree"
										style="margin-top: 0; width: 160px;"></ul>
								</div>
							</td>
						</tr>
						<tr id="jianhaoTr">
							<th align="right">
								件号
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.markId" id="markId" onblur="jywgj()"/>
								<font color="red">*</font>
								<font id="tishi" color="red" style="display: none;">此件号在试制库已存在，批产类型只能添加以下规格名称。如果想添加其他属性，请更换件号后再添加</font>
							</td>
						</tr>
						<tr>
							<th align="right">
								名称
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.name" id="name" />
								<font color="red">*</font>
							</td>
						</tr>
						<tr>
							<th align="right">
								专用料
							</th>
							<td>
								<input type="radio" value="否" name="yuanclAndWaigj.iszyl" checked="checked"/>否
								<input type="radio" value="是" name="yuanclAndWaigj.iszyl" />是
								<font color="red">*</font>
							</td>
						</tr>
						<tr>
							<th align="right">
								规格
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.specification"
									id="specification" />
							</td>
						</tr>
						<tr>
							<th align="right">
								材质
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.caizhi" id="caizhi" />
							</td>
						</tr>
						<%--						<tr id="paihaoTr">--%>
						<%--							<th align="right">--%>
						<%--								牌号--%>
						<%--							</th>--%>
						<%--							<td>--%>
						<%--								<input type="text" name="yuanclAndWaigj.trademark"--%>
						<%--									id="trademark" />--%>
						<%--							</td>--%>
						<%--						</tr>--%>
						<tr id="biliTr">
							<th align="right">
								单件重量
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.bili" id="bili"
									onkeyup="mustBeNumber('bili')" />
								<font color="red">★★★★★</font>
								<br />
								需要持续消耗的材料,比如一张铁板/一卷钢卷/一根钢管,请务必填写其单件的重量
							</td>
						</tr>
						<tr>
							<th align="right">
								版本号
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.banbenhao"
									id="banbenhao" />
							</td>
						</tr>
						<tr>
							<th align="right">
								BOM单位:
							</th>
							<td>
								<select id="unit" name="yuanclAndWaigj.unit"
									style="width: 155px;">
								</select>
							</td>
						</tr>
						<tr>
							<th align="right">
								仓库单位:
							</th>
							<td>
								<select id="unit2" name="yuanclAndWaigj.ckUnit"
									style="width: 155px;">
								</select>
							</td>
						</tr>

						<tr>
							<th align="right">
								供料属性:
							</th>
							<td>
								<select name="yuanclAndWaigj.kgliao" style="width: 155px;">
									<option value="TK">
										自购(TK)
									</option>
									<option value="TK AVL">
										指定供应商(TK AVL)
									</option>
									<option value="CS">
										客供(CS)
									</option>
									<option value="TK Price">
										完全指定(TK Price)
									</option>
								</select>
							</td>
						</tr>

						<tr>
							<th align="right">
								状态
							</th>
							<td>
								<select name="yuanclAndWaigj.status" style="width: 155px;">
									<option>
										使用
									</option>
									<option>
										禁用
									</option>
								</select>
							</td>
						</tr>
						<tr>
							<th align="right">
								产品类型
							</th>
							<td>
								<input type="radio" value="试制" name="yuanclAndWaigj.productStyle" />试制
								<input type="radio" value="批产" name="yuanclAndWaigj.productStyle" checked="checked"/>批产
							</td>
						</tr>
						<tr>
							<th align="right">
								质检周期:
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.round"
									onchange="numyanzhen(this)" id="round"/>
								(天)
							</td>
						</tr>
						<tr>
							<th align="right">
								每公斤喷粉面积:
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.areakg"
									onchange="numyanzhen(this)" />(㎡/kg)
							</td>
						</tr>
						<tr>
							<th align="right">
								密度:
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.density"
									onchange="numyanzhen(this)" />
							</td>
						</tr>
						<tr>
							<th align="right">
								图号
								<br />
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.tuhao" id="tuhao" />
							</td>
						</tr>
						<tr>
							<th align="right">
								总成号
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.zcMarkId" id="zcMarkId" />
							</td>
						</tr>
						<tr>
							<th align="right">
								最小库存
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.minkc" id="minkc" onchange="numyanzheng(this)" />
								低于此库存，将自动采购
							</td>
						</tr>
						<tr>
							<th align="right">
								采购量
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.cgcount" id="cgcount" onchange="numyanzheng(this)" />
								安全库存自动采购时采购量
							</td>
						</tr>
						<tr>
							<th align="right">
								最大库存
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.maxkc" id="maxkc" value="" />
									库存中允许的最大库存数
							</td>
						</tr>
						<tr>
							<th align="right">
								采购周期
							</th>
							<td>
								<input type="text" name="yuanclAndWaigj.cgperiod" id="cgperiod" onchange="numyanzheng(this)" />
							</td>
						</tr>
						<tr>
							<th align="right">
								LT等级
							</th>
							<td>
								<SELECT  name="" id="waigoult" onchange="chnaglt(this)">
									<option></option>
								</SELECT>
								<input type="hidden" value="" id="ltdengji" name="yuanclAndWaigj.ltdengji" >
								<input type="hidden" value="" id="ltuse" name="yuanclAndWaigj.ltuse" >
							</td>
						</tr>
						<tr>
							<th align="right">
								损耗值类型
							</th>
							<td>
								<input type="radio" value="0" name="yuanclAndWaigj.sunhaoType" checked="checked"/>百分比
								<input type="radio" value="1" name="yuanclAndWaigj.sunhaoType"/>固定值
							</td>
						</tr>
						<tr>
							<th align="right">
								损耗值
							</th>
							<td>
								<input type="text" value="" name="yuanclAndWaigj.sunhao" onchange="numyanzheng(this)"/>
							</td>
						</tr>
						<tr>
							<th align="right">
								备注
							</th>
							<td>
								<textarea rows="4" cols="70" name="yuanclAndWaigj.remark"
									id="remark"></textarea>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" value="提交(submit) "
									style="width: 80px; height: 50px;" id="sub" />
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<font color="red">★★★★★</font>表示重点注意以及识别
								<br />
								<font color="red">*</font>表示必填项
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
function validate() {
	var markId = document.getElementById("markId").value;
	var unit = document.getElementById("unit").value;
	var name = document.getElementById("name").value;
	var wgType = document.getElementById("wgType").value;
	if (wgType == "") {
		alert("请选择 物料类别");
		return false;
	} else if (markId == "") {
		alert("请填写件号");
		return false;
	} else if (name == "") {
		alert("请填写名称");
		return false;
	}
	document.getElementById("sub").disabled = "disabled";
}
$(document).ready(function() {
	getUnit("unit",'PCS');
	getUnit("unit2",'PCS');
	getCaizhi("wgType");
	var successMessage = "${successMessage}";
	if (successMessage != null && successMessage != "") {
		alert(successMessage);
	}
})
function changeClClass() {
	var clClass = $("#clClass").val();
	if (clClass == "外购件") {
		$("#jianhaoTr").show();
		$("#markId").removeAttr("disabled");
		$("#kg").show();
		$("#kgliao").removeAttr("disabled");
		$("#paihaoTr").hide();
		$("#trademark").attr("disabled", "disabled");
		$("#biliTr").hide();
		$("#bili").attr("disabled", "disabled");
	} else if (clClass == "原材料") {
		$("#kg").hide();
		$("#kgliao").attr("disabled", "disabled");
		$("#biliTr").show();
		$("#bili").removeAttr("disabled");
	} else if (clClass == "辅料") {
		$("#paihaoTr").hide();
		$("#jianhaoTr").show();
		$("#kg").hide();
		$("#kgliao").attr("disabled", "disabled");
		$("#biliTr").hide();
		$("#bili").attr("disabled", "disabled");
	}
}
function numyanzhen(obj) {
	var ty1 = '^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$';
	var re = new RegExp(ty1);
	if (obj != null && obj.value.match(re) == null) {
		obj.value = "";
		obj.focus();
		obj.select();
	}
}

$(document).ready(function() {
	loadzTree("CategoryAction_findcyListByrootId.action");
});

function loadzTree(needUrl) {
	$.ajax( {
		url : needUrl,
		type : 'post',
		dataType : 'json',
		data:{status:'物料'},
		cache : true,
		success : function(doc) {
			var zNodes = [];
			$(doc).each(function() {
				zNodes.push( {
					id : $(this).attr('id'),
					pId : $(this).attr('fatherId'),
					name : $(this).attr('name'),
					round: $(this).attr('round'),
					target : "main",
					click : false
				});
			});
			var setting = {
				view : {
					dblClickExpand : false
				},
				data : {
					simpleData : {
						enable : true
					}
				},
				callback : {
					beforeClick : beforeClick,
					onClick : onClick
				}
			};
			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			zTree.expandAll(true);
		},
		error : function() {
			alert("服务器异常!");
		}
	});
};
function beforeClick(treeId, treeNode) {
	var check = (treeNode && !treeNode.isParent);
	if (!check)
		alert("只能选择最底层");
	return check;
}

function onClick(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), nodes = zTree
			.getSelectedNodes(), v = "",round=0;
	nodes.sort(function compare(a, b) {
		return a.id - b.id;
	});
	for ( var i = 0, l = nodes.length; i < l; i++) {
		v = nodes[i].name;
		round = nodes[i].round;
	}
	//if (v.length > 0 ) v = v.substring(0, v.length-1); 
	cityObj = $("#wgType").val(v);
	$("#round").val(round);
	
}

function showMenu() {
	var cityObj = $("#wgType");
	var cityOffset = $("#wgType").offset();
	$("#menuContent").css( {
		left : cityOffset.left + "px",
		top : cityOffset.top + cityObj.outerHeight() + "px"
	}).slideDown("fast");
	$("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
	if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
			event.target).parents("#menuContent").length > 0)) {
		hideMenu();
	}
}
$(function(){
		$.ajax( {
		type : "POST",
		url : "yuanclAndWaigjAction_findALllt.action",
		data : {
			},
		dataType : "json",
		success : function(data) {
			$("#waigoult").empty();
			$("#waigoult").append("<option></option>");
			$(data).each(function(){
				$("#waigoult").append("<option value="+this.ltdengji+";"+this.ltuse+">"+this.ltdengji+"</option>");
			})
		}
	})
})
function chnaglt(obj){
	if(obj!=null && obj.value!=''){
		var strs = obj.value.split(";");
		if(strs!=null && strs.length==2){
			$("#ltdengji").val(strs[0]);
			$("#ltuse").val(strs[1]);
		}
	}
}

function jywgj(){
	var markId = $("#markId").val();
	var productStyle = '';
	var style = document.getElementsByName('yuanclAndWaigj.productStyle');
	for (i=0;i<style.length;i++){
		if(style[i].checked == true) {
		   	productStyle=style[i].value;
		}
	}
	if(markId!=""&&productStyle=='批产'){
		var markIds = markId.replace(/\s+/g, "");
		$("#markId").val(markIds);
		markId = markIds;
		$.ajax( {
			type : "POST",
			url : "PriceAction!getAllNames.action",
			data : {
				'yuanclAndWaigj.markId' : markId,
				'yuanclAndWaigj.productStyle' : productStyle
			},
			dataType : "json",
			success : function(data) {
				if (data != null) {
					$("#name").val(data.name);	
					$("#specification").val(data.specification);
					$("#wgType").val(data.wgType);
					$("#banben").val(data.banbenhao);
					$("#kgliao").val(data.kgliao);
					$("#tuhao").val(data.tuhao);
					$("#unit").val(data.unit);
					alert(markId+"此件号已经在试制库中存在，批产类型只能添加"+data.specification+data.name+"规格名称。如果想添加其他属性，请更换件号后再添加");
					$("#tishi").show();
				}else{
					$("#tishi").hide();
					$("#name").val('');	
					$("#specification").val('');
					$("#wgType").val('');
					$("#banben").val('');
					$("#kgliao").val('');
					$("#tuhao").val('');
					$("#unit").val('');
				}
			}
		})
	}else{
		$("#tishi").hide();
		$("#name").val('');	
		$("#specification").val('');
		$("#wgType").val('');
		$("#banben").val('');
		$("#kgliao").val('');
		$("#tuhao").val('');
		$("#unit").val('');
	}
}

</script>
	</body>
</html>
