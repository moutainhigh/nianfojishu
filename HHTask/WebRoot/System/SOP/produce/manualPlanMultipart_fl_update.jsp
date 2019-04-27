<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<h2>
					辅料采购单申请
				</h2>
				<br />
				<div id="taZone" style="display: none;">
					<textarea id="ta">
						<!-- 修改结束 -->
						<tr>
								<td class="mytd_0" id="search" align="center">
									<div id="showAll_0"
									style="background-color: #ffffff; position: absolute; visibility: hidden; z-index: 1; top: 40px">
									</div>
									<input type="text" id="shortname_0" onkeyup="searchInfo('_0')"
									style="height: 20px; width: 115px;" onFocus="init('_0')"
									onBlur="hidediv('_0')">
									<input type="hidden" value="辅料" name="mod1List[0].category" />
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].markId"
									id="markId_0" class="markIdClass"
									onkeydown="keyDown(event,this,'_0')"
									onkeyup="this.size=(this.value.length>12?this.value.length:12);"
									size="12" />
								</td>
								<td align="center">
									<input type="text" name="mod1List[0].wgType"
									onkeyup="this.size=(this.value.length>4?this.value.length:4);"
									size="4" id="wgType_0"
									style="text-align: center; width: 100%; height: 100%;">
								</td>
								<td align="center" style="padding: 0;">
									<input type="text" value="" name="mod1List[0].proName"
									onkeyup="this.size=(this.value.length>4?this.value.length:4);"
									size="4" id="proName_0"
									style="text-align: center; width: 100%; height: 100%; size: 0;">
								</td>
								<td align="center">
									<input type="text" name="mod1List[0].specification"
									onkeyup="this.size=(this.value.length>4?this.value.length:4);"
									size="4" id="specification_0"
									style="text-align: center; width: 100%; height: 100%;">
								</td>
								<td align="center">
									<input type="text" name="mod1List[0].unit" id="unit_0"
									onkeyup="this.size=(this.value.length>4?this.value.length:4);"
									size="4" style="text-align: center; width: 100%; height: 100%;">
								</td>
								<td align="center">
									<input type="text" name="mod1List[0].cgnumber" id="cgnumber_0"
									onchange="numyanzheng(this)" onblur="compPriceAndNum('_0')"
									style="text-align: center; width: 100%; height: 100%;"
									onkeyup="this.size=(this.value.length>4?this.value.length:4);"
									size="4" />
							<!-- onkeydown="keyDownNextList(event,this,'_0')" -->
								</td>
								<td align="center" style="width: 80px;">
									<input type="radio" value="YES" name="mod1List[0].isurgent" />是
									<input type="radio" value="NO" name="mod1List[0].isurgent"
									checked="checked" />否
								</td>
								<td align="center">
									<select name="mod1List[0].kgliao" id="kgliao_0">
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
								<td align="center">
									<input type="text" name="mod1List[0].banben"
									onkeyup="this.size=(this.value.length>4?this.value.length:4);"
									size="4" id="banben_0"
									style="text-align: center; width: 100%; height: 100%;">
								</td>
								<td align="center">
									<input type="text" name="mod1List[0].tuhao" id="tuhao_0"
									onkeyup="this.size=(this.value.length>4?this.value.length:4);"
									size="4" style="text-align: center; width: 100%; height: 100%;">
								</td>
								
								<td align="center">
								
								<!-- 修改结束 -->
					
					
				
					</textarea>
				</div>
				<form action="ManualOrderPlanAction_updateManaualTotal.action" id = "pageForm"
					method="POST" onsubmit="return check()">
					<table class="table">
						<tr>
							<th align="center">
								申请人工号：
								<input readonly="readonly"
									onkeyup="this.value=this.value.replace(/\D/g,'')"
									name="planTotal.userCode" class="horizontalLine"
									value="${planTotal.userCode}">
							</th>
							<th align="center">
								申请人姓名：
								<input readonly="readonly"
									onkeyup="this.value=this.value.replace(/\D/g,'')"
									name="planTotal.userName" class="horizontalLine"
									value="${planTotal.userName}">
							</th>
							<th align="center">
								申请人部门：
								<input readonly="readonly"
									onkeyup="this.value=this.value.replace(/\D/g,'')"
									name="planTotal.userDept" class="horizontalLine"
									value="${planTotal.userDept}">
							</th>
							<th align="center">
								总价格：
								<input readonly="readonly" value="${planTotal.estimatePrice}"
									onkeyup="this.value=this.value.replace(/\D/g,'')"
									id="totalPrice" name="planTotal.estimatePrice"
									class="horizontalLine">
							</th>
							<th align="center">
								辅料类别:
								<SELECT name="planTotal.fltype" id="fltype">
									<option value="${planTotal.fltype}">${planTotal.fltype}</option>
									<option value="必用辅料">必用辅料</option>
									<option value="其它类型辅料">其它类型辅料</option>
								</SELECT>
							</th>
							<th align="center">
								流水单号：
								<input readonly="readonly"
									onkeyup="this.value=this.value.replace(/\D/g,'')"
									name="planTotal.totalNum" class="horizontalLine"
									value="${planTotal.totalNum}">
							</th>

							<input type="hidden" value="${planTotal.userId}"
								name="planTotal.userId" />
							<input type="hidden" value="${planTotal.id}" name="planTotal.id" />
							<input type="hidden" value="${pageStatus}" name="pageStatus" />
							<input type="hidden" value="${tag}" name="tag" />
							<input type="hidden" value="${flag}" name="flag" />
							<input type="hidden" value="${planTotal.category}"
								name="planTotal.category" />
						</tr>
					</table>
					<table class="table">
						<tbody id="tbody">
							<tr>
								<th>
									物品检索
								</th>
								<th>
									件号
								</th>
								<th>物品类别</th>
								<th>
									物品名称
								</th>
								<th>
									物品规格
								</th>
								<th>
									物品单位
								</th>
								<th>
									采购数量
								</th>
								<th>
									是否紧急
								</th>
								<th>
									供料属性
								</th>
								<th>
									版本号
								</th>
								<th>
									图号
								</th>
								<th>
									需求部门
								</th>
								<th>
									用途
								</th>
								<th>
									年累计<br/>
									请购量
								</th>
								<th>
									操作
								</th>
							</tr>
							<s:if test="planTotal.details!=null&&planTotal.details.size()>0">
								<s:iterator value="planTotal.details" id="detail"
									status="pagestatus">
									<tr>
										<td class="mytd_${pagestatus.index}" id="search" align="center">
											<div id="showAll_${pagestatus.index}"
												style="background-color: #ffffff; position: absolute; visibility: hidden; z-index: 1; top: 40px">
											</div>
											<input type="text" id="shortname_${pagestatus.index}"
												onkeyup="searchInfo('_${pagestatus.index}')"
												style="height: 20px; width: 115px;" onFocus="init('_${pagestatus.index}')"
												onBlur="hidediv('_${pagestatus.index}')">
											<input type="hidden" value="辅料" name="mod1List[${pagestatus.index}].category" />
										</td>
										<td align="center">
											<input type="text" value="${detail.markId}" name="mod1List[${pagestatus.index}].markId"
												id="markId_${pagestatus.index}" class="markIdClass" readonly="readonly"
												onkeydown="keyDown(event,this,'_${pagestatus.index}')"
												onkeyup="this.size=(this.value.length>12?this.value.length:12);"
												size="12" />
											<input type="hidden" value="${detail.id}" name="mod1List[${pagestatus.index }].id">
										</td>
										<td align="center">
											<input type="text" name="mod1List[${pagestatus.index}].wgType" readonly="readonly"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);"
												size="4" id="wgType_${pagestatus.index}" value="${detail.wgType}"
												style="text-align: center; width: 100%; height: 20px;">
										</td>
										<td align="center" style="padding: 0;">
											<input type="text" value="${detail.proName}" name="mod1List[${pagestatus.index}].proName"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);"
												size="4" id="proName_${pagestatus.index}" readonly="readonly"
												style="text-align: center; width: 100%; height: 20px; size: 0;">
										</td>
										<td align="center">
											<input type="text" name="mod1List[${pagestatus.index}].specification"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);" readonly="readonly"
												size="4" id="specification_${pagestatus.index}" value="${detail.specification}"
												style="text-align: center; width: 100%; height: 20px;">
										</td>
										<td align="center">
											<input type="text" name="mod1List[${pagestatus.index}].unit" id="unit_${pagestatus.index}"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);"
												size="4" name="${detail.unit}" readonly="readonly"
												style="text-align: center; width: 100%; height: 20px;">
										</td>
										<td align="center">
											<input type="text" name="mod1List[${pagestatus.index}].cgnumber"
												id="cgnumber_${pagestatus.index}" onchange="numyanzheng(this)"
												onblur="compPriceAndNum('_${pagestatus.index}')" value="${detail.cgnumber}"
												style="text-align: center; width: 100%; height: 20px;"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);"
												size="4" />
											<!-- onkeydown="keyDownNextList(event,this,'_0')" -->
										</td>
										<td align="center" style="width:80px;">
											<s:if test="#detail.isurgent=='YES'">
												<input type="radio" value="YES" name="mod1List[${pagestatus.index}].isurgent" 
												checked="checked" />
												是
												<input type="radio" value="NO" name="mod1List[${pagestatus.index}].isurgent"/>
												否
											</s:if>
											<s:if test="#detail.isurgent=='NO'">
												<input type="radio" value="YES" name="mod1List[${pagestatus.index}].isurgent" />
												是
												<input type="radio" value="NO" name="mod1List[${pagestatus.index}].isurgent"
													checked="checked" />
												否
											</s:if>
										</td>
										<td align="center">
											<select name="mod1List[${pagestatus.index}].kgliao" id="kgliao_${pagestatus.index}">
												<option value="${detail.kgliao}">${detail.kgliao }</option>
<!-- 												<option value="TK" -->
<!-- 													<s:if test="#detail.kgliao=='TK'"> -->
<!-- 													 selected="selected" -->
<!-- 													 </s:if> -->
<!-- 												> -->
<!-- 													自购(TK) -->
<!-- 												</option> -->
<!-- 												<option value="TK AVL" -->
<!-- 													<s:if test="#detail.kgliao=='TK AVL'"> -->
<!-- 													 selected="selected" -->
<!-- 													 </s:if> -->
<!-- 												> -->
<!-- 													指定供应商(TK AVL) -->
<!-- 												</option> -->
<!-- 												<option value="CS" -->
<!-- 													<s:if test="#detail.kgliao=='CS'"> -->
<!-- 													 selected="selected" -->
<!-- 													 </s:if> -->
<!-- 												> -->
<!-- 													客供(CS) -->
<!-- 												</option> -->
<!-- 												<option value="TK Price" -->
<!-- 													<s:if test="#detail.kgliao=='TK Price'"> -->
<!-- 													 selected="selected" -->
<!-- 													 </s:if> -->
<!-- 												 > -->
<!-- 													完全指定(TK Price) -->
<!-- 												</option>	 -->
												
											</select>
										</td>
										<td align="center">
											<input type="text" name="mod1List[${pagestatus.index}].banben" readonly="readonly"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);"
												size="4" id="banben_${pagestatus.index}" value="${detail.banben}"
												style="text-align: center; width: 100%; height: 20px;">
										</td>
										<td align="center">
											<s:if test="#detail.tuhao == '' || #detail.tuhao==null ||  #detail.tuhao=='null'">
												<input type="text" name="mod1List[${pagestatus.index}].tuhao" id="tuhao_${pagestatus.index}"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);"
												size="4" value="${detail.tuhao}" 
												style="text-align: center; width: 120px; height: 20px;">
											</s:if>
											<s:else>
												<input type="text" name="mod1List[${pagestatus.index}].tuhao" id="tuhao_${pagestatus.index}"
												onkeyup="this.size=(this.value.length>4?this.value.length:4);"
												size="4" value="${detail.tuhao}" 
												style="text-align: center; width: 120px; height: 20px;">
											</s:else>
										</td>
										<td align="center">
											<select name="mod1List[${pagestatus.index}].demanddept" id="demanddept_${pagestatus.index}"
											  onmousemove="createDept('demanddept_${pagestatus.index}')" >
												<option value="${detail.demanddept}" selected="selected">${detail.demanddept}</option>
											</select>
										</td>
										<td>
											<input type="text" value="${detail.yongtu}" name="mod1List[${pagestatus.index}].yongtu" id="yongtu_${pagestatus.index}"/>
										</td>
										<td>
											<input type="text"  style="width: 75px;" onchange="numyanzheng(this)"
											value="${detail.yearSumNum}" name="mod1List[${pagestatus.index}].yearSumNum" id="yearSumNum_${pagestatus.index}"/>
										</td>
										<td align="center" style="">
											<input type="button" value="增加" onclick="addAList()" />
											<input type="button" value="删除" onclick="delBtn(this)" />
										</td>
									</tr>
								</s:iterator>
							</s:if>
							<tr>
						</tbody>
					</table>
					<input type="hidden" value="${fn:length(planTotal.details)}"
						id="listSize" />
					<input type="button" value="修改" class="input" onclick="submitBth()">
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
		var tab_option =0;
		
$(function(){
	var listSize = $("#listSize").val();
	if(listSize==null && listSize>0){
		tab_option = 0;
	}else{
		tab_option = parseInt(listSize);
	}
	
});
	


function submitBth(){
	$("#pageForm").submit();
}
		
function getwgj(className){
	//$("#hint").empty();
	var wlcode = $("#markId"+className).val();
	if(wlcode!=null&& wlcode!="" ){
		$.ajax({
			type : "POST",
			url : "OAAppDetailTemplateAction!getTempByCode.action",
			dataType : "json",
			data : {
				"template.wlcode" : wlcode
			},
			success : function(data) {
				if(data!=null){
					//$('#wlcode').val(data.); //用户从下拉中选择某项后，刷新那个只读的personId值
					if(data.detailAppName!=null){
						$("#proName"+className).val(data.detailAppName);
						$("#proName"+className).attr("readonly","readonly");
						$("#proName"+className).attr("size",data.detailAppName.length+3);
						$("#markId"+className).attr("readonly","readonly");
					}
					if(data.detailFormat!=null && data.detailFormat!="null"){
						$("#specification"+className).val(data.detailFormat);
						$("#specification"+className).attr("size",data.detailFormat.length+3);
						$("#specification"+className).attr("readonly","readonly");
					}
					if(data.detailChildClass!=null && data.detailChildClass!="null"){
						$("#wgType"+className).val(data.detailChildClass);
						$("#wgType"+className).attr("size",data.detailChildClass.length+3);
						$("#wgType"+className).attr("readonly","readonly");
					}
					if(data.detailUnit!=null && data.detailUnit!="null"){
						$("#unit"+className).val(data.detailUnit);
						$("#unit"+className).attr("size",data.detailUnit.length+3);
						$("#unit"+className).attr("readonly","readonly");
					}
					if(data.kgliao!=null && data.kgliao!="null"){
						$("#kgliao"+className).val(data.kgliao);
						$("#kgliao"+className).click(function(){
							//console.log("不可编辑");
							alert("不可编辑");
					        return false;
					    }); 
					}
					if(data.banben!=null && data.banben!="null"){
						$("#banben"+className).val(data.banben);
						$("#banben"+className).attr("size",data.banben.length+3);
						$("#banben"+className).attr("readonly","readonly");
					}
					if(data.tuhao!=null && data.tuhao!=""){
						$("#tuhao"+className).val(data.tuhao);
						$("#tuhao"+className).attr("size",data.tuhao.length+3);
					}
					
					estimatePrice(className);//预估价格
					compPriceAndNum();//预估总价格
					$("#cgnumber"+className).focus();//数量聚焦
				}else{
					$("#proName"+className).val();
					$("#specification"+className).val();
					$("#wgType"+className).val();
					$("#unit"+className).val();
				}
			}
		});
	}
	
}
function check(){
	var index = 0;
	var fltype = $("#fltype").val();
	if(fltype == ''){
		alert("请选择辅料类型");
		$("#fltype").focus();
		return false;
	}
	for(var i=0;i<=tab_option;i++){
		var obj = $("#markId_"+i);
		if(obj[0]!=null){
			index++;
			var markId = $("#markId_"+i).val();
			if(markId==null || markId==""){
				
				alert("第"+index+"行件号没有填写");
				$("#markId_"+i).focus();
				return false;
			}
			
			var number = $("#cgnumber_"+i).val();
			if(number==null || number==""){
				alert("第"+index+"行数量没有填写");
				$("#cgnumber_"+i).focus();
				return false;
			}
		}
	}
}




//初始化显示div位置
function init(className) {
	count_seach++;
	var shortname = document.getElementById("shortname" + className);
	var showAll = document.getElementById("showAll" + className);
	showAll.style.top = getTop(shortname) + 20;
	showAll.style.left = getLeft(shortname);
	showAll.style.visibility = "visible";
}

function hidediv(className) {
	count_seach--;
	if (count_seach == 0) {
		var showAll = document.getElementById("showAll" + className);
		showAll.style.visibility = "hidden";
	}
}
function outdiv(obj, className) {
	obj.style.background = "#ffffff";
	// var showAll=document.getElementById(".showAll"+className);
	hidediv(className);
}

//检索信息
function searchInfo(className){
	var obj = document.getElementById("shortname" + className);
	if ($(obj).val() == null || $(obj).val() == "") {
		//$("#showAll"+openIndex).empty();
		return;
	}
	$.ajax( {
		type : "POST",
		url : "OAAppDetailTemplateAction!searchAppName.action",
		dataType : "json",
		cache:false,
		data : {
			'template.detailAppName' : obj.value
		},
		success : function(data) {
			$("#showAll" + className).empty();
			$(data).each(function() {
				var markId = $(this).attr('wlcode')
					.replace($("#shortname"+ className).val(),
						"<font color='red'>"
								+ $("#shortname"+ className).val()
								+ "</font>");
				$("#showAll" + className).append(
					"<div onmouseover='ondiv(this)' onmouseout='outdiv(this,\""
						+ className+ "\")' "
						+ "onclick='selectdiv(this,\"" + className + "\")' align='left'>"
						+ markId
						+ "__"
						+ $(this).attr('detailAppName')
						+ "__"
						+ $(this).attr('detailChildClass')
						+ "__"
						+ $(this).attr('detailFormat')
						+ "__"
						+ $(this).attr('detailUnit')
						+ "__"
						+ $(this).attr('kgliao')
						+ "__"
						+ $(this).attr('banben')
						+ "__"
						+ $(this).attr('tuhao')
						+ "<span style='visibility: hidden;'>"
						+ $(this).attr('wlcode')
						+ "__"
						+ $(this).attr('detailAppName')
						+ "__"
						+ $(this).attr('detailChildClass')
						+ "__"
						+ $(this).attr('detailFormat')
						+ "__"
						+ $(this).attr('detailUnit')
						+ "__"
						+ $(this).attr('kgliao')
						+ "__"
						+ $(this).attr('banben')
						+ "__"
						+ $(this).attr('tuhao')
						+ "</span>"
						+ "</div>");
			});
		}
	});
}


function keyDown(e,obj,className) {
    var ev= window.event||e;
	//13是键盘上面固定的回车键
    if (ev.keyCode == 13) {
		//你要执行的方法
	  	getwgj(className)
  	}
}


var addIndex = $("#listSize").val();
function addAList(){
	var str = $("#ta").val();
	addIndex++;
	var append = '<select name="mod1List['+addIndex+'].demanddept" id="demanddept_'+addIndex+'" onmousemove="createDept(&apos;demanddept_'+addIndex+'&apos;)"  ><option></option></select>'+
					'</td>' +
					'<td><input type="text" name="mod1List['+addIndex+'].yongtu" id="yongtu_'+addIndex+'"> </td><td align="center" style="width：130px;">' +
					'<input type="text"  style="width: 75px;" onchange="numyanzheng(this)" ' +
					' name="mod1List['+addIndex+'].yearSumNum" id="yearSumNum_'+addIndex+'"/> </td>' +
					'<td align="center" style="width：130px;"><input type="button" value="增加" onclick="addAList()" /><input type="button" value="删除" onclick="delBtn(this)" />'+
					'			</td></tr>';
	str+=append;
	console.debug(str);
	tab_option++;
	while (str.indexOf('_0') >= 0){
       str = str.replace('_0', '_'+tab_option);
       str = str.replace('[0]', '['+tab_option+']');
    }
	var firstNo=0;
	var endNo = 0;
	var middleStr="";
	while(str.indexOf('<font')>0){
		firstNo = str.indexOf('<font');
		endNo = str.indexOf("</font>")+7;
		middleStr = str.substring(firstNo,endNo);
		str=str.replace(middleStr,"");
	}
	str = "<tr>"+str+"</tr>"
	$("#tbody").append(str);
	//addKgLiao();
	
	//设置供料属性
	//addKgLiao();
	//$(".goodsStoreMarkId_"+tab_option).focus();
}

function delBtn(obj){
	 var html = $(obj).parent().parent().html();
		 $(obj).parent().parent().empty();
		 compPriceAndNum("");	
}

function keyDownNextList(event,obj,className){
	var ev= window.event||e;
	//13是键盘上面固定的回车键
    if (ev.keyCode == 13) {
    	if(null!=obj.value && ""!=obj.value){
			//你要执行的方法
			index = parseInt(className.substring(1,2))+1;
			if(tab_option>className){
				$("#cgnumber_"+index).focus();//数量聚焦
				$("#cgnumber_"+index).select();
			}else{
			  	addAList();
				$("#markId_"+index).focus();//数量聚焦	
				$("#markId_"+index).select();
			}
    	}
    	
  	}
}


function selectdiv(obj, className) {
	var html = $(obj).find("span").html();
	var showAll = document.getElementById("showAll" + className);
	showAll.style.visibility = "hidden";
	var htmls = html.split("__");
	$(".shortname" + className).val(html);
	$("#markId" + className).val(htmls[0]);
	$("#markId"+className).attr("readonly","readonly");
	var name=htmls[1];
	var wgType = htmls[2];
	var specification=htmls[3];
	var unit = htmls[4];
	var kgliao = htmls[5];
	var banben = htmls[6];
	var tuhao = htmls[7]
	if(name!=null && name!="null"){
		$("#proName"+className).val(name);
		$("#proName"+className).attr("readonly","readonly");
	}
	if(specification!=null && specification!="null"){
		$("#specification"+className).val(specification);
		$("#specification"+className).attr("size",specification.length+3);
		$("#specification"+className).attr("readonly","readonly");
	}
	if(wgType!=null && wgType!="null"){
		$("#wgType"+className).val(wgType);
		$("#wgType"+className).attr("size",wgType.length+3);
		$("#wgType"+className).attr("readonly","readonly");
	}
	if(unit!=null && unit!="null"){
		$("#unit"+className).val(unit);
		$("#unit"+className).attr("size",unit.length+3);
		$("#unit"+className).attr("readonly","readonly");
	}
	if(kgliao!=null && kgliao !="null"){
		$("#kgliao"+className).val(kgliao);
		$("#kgliao"+className).click(function(){
			//console.log("不可编辑");
			alert("不可编辑");
	        return false;
	    }); 
	}
	if(banben!=null && banben!="null"){
		$("#banben"+className).val(banben);
		$("#banben"+className).attr("size",banben.length+3);
		$("#banben"+className).attr("readonly","readonly");
	}
	if(tuhao!=null && tuhao!=""){
		$("#tuhao"+className).val(tuhao);
		$("#tuhao"+className).attr("size",tuhao.length+3);
	}
	estimatePrice(className);//预估价格
	compPriceAndNum();
	$("#cgnumber"+className).focus();//数量聚焦
}

 function submitFile(){
      var form = new FormData(document.getElementById("fromFile"));
      var file = $("#file").val();
      if(file!=null){
      	$.ajax({
                url:"${pageContext.request.contextPath}/ManualOrderPlanAction_analysisflFromFile.action",
                type:"post",
                data:form,
                processData:false,
                contentType:false,
                async : false, 
                success:function(data){
                    if(data!=null && data!=""){
                    	var obj = eval('('+data+')');
                    	if(obj.users!=null && obj.users.length>0){
                    		for(var i=0;i<obj.users.length;i++){
                    			
                    			addAList();
                    			var yuan = obj.users[i];
                    			var className = "_"+tab_option;
                    			var name =yuan.proName;
                    			var specification = yuan.specification;
                    			var wgType = yuan.wgType;
                    			var banbenhao = yuan.banben;
                    			var tuhao = yuan.tuhao;
                    			var unit = yuan.unit;
                    			var kgliao = yuan.kgliao;
                    			var markId = yuan.markId;
                    			var cgnumberstr = yuan.cgnumber;
                    			var needFinalDate = yuan.needFinalDate;
                    			var remarks = yuan.remarks;
                    			if(needFinalDate!=null && needFinalDate!=""){
                    				$("#needFinalDate"+className).val(needFinalDate);
                    				$("#needFinalDate"+className).attr("size",needFinalDate.length+3);
                    			}
                    			if(remarks!=null && remarks!=""){
                    				$("#remarks"+className).val(remarks);
                    				$("#remarks"+className).attr("size",remarks );
                    			}
                    			if(cgnumberstr==null || cgnumberstr==""){
                    				cgnumberstr=1;
                    				$("#cgnumber"+className).val(cgnumberstr);
                    				$("#cgnumber"+className).attr("size",cgnumberstr.length+3);
                    			}else{
                    				$("#cgnumber"+className).val(cgnumberstr);
                    				$("#cgnumber"+className).attr("size",cgnumberstr.length+3);
                    			}
                    			if(markId!=null && markId!=""){
                    				$("#markId"+className).val(markId);
                    				$("#markId"+className).attr("size",markId.length+3);
                    			}
                    			if(name!=null && name!="null"){
									$("#proName"+className).val(name);
									$("#proName"+className).attr("size",name.length+3);
								}
								if(specification!=null && specification!="null"){
									$("#specification"+className).val(specification);
									$("#specification"+className).attr("size",specification.length+3);
								}
								if(wgType!=null && wgType!="null"){
									$("#wgType"+className).val(wgType);
									$("#wgType"+className).attr("size",wgType.length+3);
								}
								if(banbenhao!=null && banbenhao!="null"){
									$("#banben"+className).val(banbenhao);
									$("#banben"+className).attr("size",banbenhao.length+3);
								}
								if(kgliao!=null && kgliao!="null"){
									$("#kgliao"+className).attr("value",kgliao);
								}
								if(tuhao!=null && tuhao!="null"){
									$("#tuhao"+className).val(tuhao);
									$("#tuhao"+className).attr("size",tuhao.length+3);
								}
								
								if(unit!=null && unit!="null"){
									$("#unit"+className).val(unit);
									$("#unit"+className).attr("size",unit.length+3);
								}
								estimatePrice(className);
							     compPriceAndNum();               			
                    		}
                    	}
	                    alert(obj.msg);
                    	
                    }
                },
                error:function(e){
                    alert("错误！！");
                }
            });
      }
                    
}
 /**
  * 加载预估价格
  */
function estimatePrice(className){
	var markId = $("#markId"+className).val();
	var name = $("#proName"+className).val();
	var specification = $("#specification"+className).val();
	if(markId!=null && markId !=""){
		$.ajax({
			type:"post",
			url:"${pageContext.request.contextPath}/PriceAction!findprice.action",
			data:{
				"price.partNumber":markId,
				"price.name":name,
				"price.specification":specification
			},
			dataType:"json",
			success:function(data){
				if(data!=null){
					$("#estimatePrice"+className).val(data.hsPrice);
				}
			}
		});
	}
}


 function compPriceAndNum(className){
	/* var totalPrice = 0;//总价格
	 var number = $("#"+className).val();
	 var price = $("#"+className).val();*/
	 var totalPrice = 0;//总价格
	 for(var i=0;i<=tab_option;i++){
		var obj = $("#markId_"+i);
		if(obj[0]!=null){
			
			var markId = $("#markId_"+i).val();
			if(markId!=null && markId!=""){
				var cgnumber = $("#cgnumber_"+i).val();
				var price = $("#estimatePrice_"+i).val();
				if(price!=null && price!=""){
					if(cgnumber!=null && cgnumber!=""){
						totalPrice+=parseFloat(price)*parseFloat(cgnumber);
						
					}else{
						totalPrice+=parseFloat(price);
					}
				}
				
				
			}
		}
	}
	 $("#totalPrice").val(totalPrice);
	 
	 
}
</SCRIPT>
	</body>
</html>
