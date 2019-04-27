<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
	<style type="text/css">
	.upload{position:relative; display:inline-block; height:33px;line-height:33px; overflow:hidden;vertical-align:middle; cursor:pointer;}
	.upload .upload-input-file{position:absolute; right:0; top:0; font-size:100px; opacity:0; filter:alpha(opacity=0);cursor:pointer;}
	.upload .upload-btn{outline:none;border:0; padding:7px 10px;cursor:pointer; margin:3px; border-radius:3px;}
	.horizontalLine{
	text-align:center;
}
  </style>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
				<h2>
					采购单申请
				</h2>
				<br />
				<div id="taZone" style="display:none;">
					<textarea id="ta">
						<tr>
								<td class="mytd_0" id="search" align="center">
									<div id="showAll_0"
										style="background-color: #ffffff; position: absolute; visibility: hidden; z-index: 1; top: 40px">
									</div>
									<input type="text" id="shortname_0" onkeyup="searchInfo('_0')"
										style="height: 20px; width: 115px;" onFocus="init('_0')"
										onBlur="hidediv('_0')">
									<input type="hidden" value="外购" name="mod1List[0].category"/>
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].markId" id="markId_0"
										class="markIdClass" onkeydown="keyDown(event,this,'_0')" 
										onkeyup="this.size=(this.value.length>12?this.value.length:12);" size="12" />
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].cgnumber"
										id="cgnumber_0" onchange="numyanzheng(this)"
										style="text-align: center;width:100%;height:100%;" 
										onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
										onblur="compPriceAndNum('_0')" onkeydown="keyDownNextList(event,this,'_0')"/>
								</td>
								<td align="center" style="width:80px;">
									<input type="radio" value="YES" name="mod1List[0].isurgent" />是<br>
									<input type="radio" value="NO" name="mod1List[0].isurgent" checked="checked" />否
								</td>
								<td align="center" style="padding: 0;">
									<input type="text" value="" name="mod1List[0].proName" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
										id="proName_0" style="text-align: center;width:100%;height:100%;size: 0;">
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
									<input type="text" value="" name="mod1List[0].wgType" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
										id="wgType_0" style="text-align: center;width:100%;height:100%;" >
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].specification" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
										id="specification_0" style="text-align: center;width:100%;height:100%;">
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].banben" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
										id="banben_0" style="text-align: center;width:100%;height:100%;">
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].unit" id="unit_0" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
										style="text-align: center;width:100%;height:100%;">
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].tuhao" id="tuhao_0" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
										style="text-align: center;width:100%;height:100%;">
								</td>
								<td align="center">
									<input type="text" value="" name="mod1List[0].needFinalDate" id="needFinalDate_0" 
										onkeyup="this.size=(this.value.length>6?this.value.length:6);" size="6	"
										style="text-align: center;width:100%;height:100%;" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})">
								</td>
								<td align="center">
									<input type="text" name="mod1List[0].estimatePrice"
								id="estimatePrice_0" size="6" onkeyup="mustBeNumber('estimatePrice_0')" class="price"
								onblur="compPriceAndNum('_0')"
								style="text-align: center; width: 100%; height: 100%;" >
								</td>
								<td align="center">
					</textarea>
					
					<!-- 上传控件 -->
					<textarea id="fileText">
						<div class="lcell span6">
							<div class="lrow">
								<div class="lcell span5">
									<span class="upload">
										<input type="file" class="upload-input-file" title="file_0" name="mod1List[0].files"
										onchange="javascript:changeFile('_0',this)" id="file_0"/>
										<input type="button" class="upload-btn" value="浏览文件" id="showFile_0" />
									</span>
								</div> 
							</div>                        
						</div>
					</textarea>
				</div>
				
				<form action="ManualOrderPlanAction_daorumaualPlan.action"
					method="post" id="fromFile">
					选择导入文件:
					<input type="file" name="file" id="file">
					<a href="<%=basePath%>/upload/file/download/wlxqb.xls">导入模版下载</a>
					<input type="button" value="导入到列表" id="sub" onclick="submitFile()">
				</form>
				
				<form action="ManualOrderPlanAction_updateManaualTotal.action" id="pageForm"
					method="POST" onsubmit="return check()" enctype="multipart/form-data">
					<table class="table">
						<tr>
							<th align="center">申请人工号：<input  readonly="readonly"
										onkeyup="this.value=this.value.replace(/\D/g,'')"
										 class="horizontalLine"
										value="${planTotal.userCode}"></th>
							<th  align="center">申请人姓名：<input  readonly="readonly"
										onkeyup="this.value=this.value.replace(/\D/g,'')"
										 class="horizontalLine"
										value="${planTotal.userName}"></th>
							<th  align="center">申请人部门：<input  readonly="readonly"
										onkeyup="this.value=this.value.replace(/\D/g,'')"
										 class="horizontalLine"
										value="${planTotal.userDept}"></th>
							<th  align="center">预估总金额(元)：<input  readonly="readonly"
										onkeyup="this.value=this.value.replace(/\D/g,'')" id="totalPrice"
										name="planTotal.estimatePrice" class="horizontalLine"
										value="${planTotal.estimatePrice}"></th>
							<th align="center">流水单号：<input readonly="readonly"
										onkeyup="this.value=this.value.replace(/\D/g,'')"
										 class="horizontalLine" 
										value="${planTotal.totalNum}"></th>
							<th>
								项目编号：<br>
								<input type="text" name="planTotal.proCode" class="horizontalLine" 
										value="${planTotal.proCode }">
							</th>
							<th>
								项目名称：<br>
								<input type="text" name="planTotal.proName" class="horizontalLine"
										value="${planTotal.proName }">
							</th>
							<input type="hidden" value="${planTotal.id}" name="planTotal.id"/>
							<input type="hidden" value="${pageStatus}" name="pageStatus"/>
							<input type="hidden" value="${tag}" name="tag"/>
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
								<th>
									采购数量
								</th>
								<th>
									是否紧急
								</th>
								<th>
									名称
								</th>
								<th>
									供料属性
								</th>
								<th>
									物料类别
								</th>
								<th>
									规格
								</th>
								<th>
									版本号
								</th>
								<th>
									单位
								</th>
								<th>
									图号
								</th>
								<th>
									送货日期
								</th>
								<th>
									预估价格
								</th>
								<th>
									备注
								</th>
								<th>
									附件
								</th>
								<th>
									操作
								</th>
							</tr>
							<s:if test="planTotal.details!=null&&planTotal.details.size()>0">
								<s:iterator value="planTotal.details" id="detail"  status="pagestatus">
									<tr>
										<td class="mytd_${pagestatus.index}" align="center">
											<div id="showAll_${pagestatus.index}"
												style="background-color: #ffffff; position: absolute; visibility: hidden; z-index: 1; top: 40px">
											</div>
											<input type="text" id="shortname_${pagestatus.index}" 
												style="height: 20px; width: 115px;" 
												 value="${pagestatus.index+1 }" disabled="disabled">
											<input type="hidden" value="外购" name="mod1List[${pagestatus.index}].category"/>
											<input type="hidden" value="${detail.id}" name="mod1List[${pagestatus.index}].id"/>
											<input type="hidden" value="${detail.addUsers}" name="mod1List[${pagestatus.index}].addUsers">
											<input type="hidden" value="${detail.addUsersCode}" name="mod1List[${pagestatus.index}].addUsersCode">
											<input type="hidden" value="${detail.addTime}" name="mod1List[${pagestatus.index}].addTime">
										</td>
										<td align="center">
											<input type="text"   name="mod1List[${pagestatus.index}].markId" id="markId_${pagestatus.index}"
												class="markIdClass" onkeydown="keyDown(event,this,'_${pagestatus.index}')" value="${detail.markId}" 
												onkeyup="this.size=(this.value.length>12?this.value.length:12);" size="12" readonly="readonly"/>
										</td>
										<td align="center">
											<input type="text"  name="mod1List[${pagestatus.index}].cgnumber" 
												id="cgnumber_${pagestatus.index}" onchange="numyanzheng(this)" value="${detail.cgnumber}"
												style="text-align: center;width:100%;height:100%;" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4"
												onblur="compPriceAndNum('')" onkeydown="keyDownNextList(event,this,'_0')" />
										</td>
										<td align="center" style="width:80px;">
											<s:if test="#detail.isurgent=='YES'">
												<input type="radio" value="YES" name="mod1List[${pagestatus.index}].isurgent" 
												checked="checked" />
												是<br>
												<input type="radio" value="NO" name="mod1List[${pagestatus.index}].isurgent"/>
												否
											</s:if>
											<s:if test="#detail.isurgent=='NO'">
												<input type="radio" value="YES" name="mod1List[${pagestatus.index}].isurgent" />
												是<br>
												<input type="radio" value="NO" name="mod1List[${pagestatus.index}].isurgent"
													checked="checked" />
												否
											</s:if>
										</td>
										<td align="center" style="padding: 0;">
											<input type="text"   name="mod1List[${pagestatus.index}].proName" readonly="readonly"
											 onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4" value="${detail.proName}"
												id="proName_${pagestatus.index}" style="text-align: center;width:100%;height:100%;size: 0;">
										</td>
										<td align="center">
											<select name="mod1List[${pagestatus.index}].kgliao" id="kgliao_${pagestatus.index}" onclick="alert('不可编辑');return false;">
												
												<option value="TK"
													<s:if test="#detail.kgliao=='TK'">
													 selected="selected"
													 </s:if>
												>
													自购(TK)
												</option>
												<option value="TK AVL"
													<s:if test="#detail.kgliao=='TK AVL'">
													 selected="selected"
													 </s:if>
												>
													指定供应商(TK AVL)
												</option>
												<option value="CS"
													<s:if test="#detail.kgliao=='CS'">
													 selected="selected"
													 </s:if>
												>
													客供(CS)
												</option>
												<option value="TK Price"
													<s:if test="#detail.kgliao=='TK Price'">
													 selected="selected"
													 </s:if>
												 >
													完全指定(TK Price)
												</option>	
												
											</select>
										</td>
										<td align="center">
											<input type="text"   name="mod1List[${pagestatus.index}].wgType" readonly="readonly"
											 onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4" value="${detail.wgType}"
												id="wgType_${pagestatus.index}" style="text-align: center;width:100%;height:100%;" >
										</td>
										<td align="center">
											<input type="text"   name="mod1List[${pagestatus.index}].specification"  readonly="readonly"
											onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4" value="${detail.specification}"
												id="specification_${pagestatus.index}" style="text-align: center;width:100%;height:100%;">
										</td>
										<td align="center">
											<input type="text"   name="mod1List[${pagestatus.index}].banben" readonly="readonly"
											onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4" value="${detail.banben}"
												id="banben_${pagestatus.index}" style="text-align: center;width:100%;height:100%;">
										</td>
										<td align="center">
											<input type="text"   name="mod1List[${pagestatus.index}].unit" id="unit_${pagestatus.index}" 
											onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4" value="${detail.unit}"
												style="text-align: center;width:100%;height:100%;" readonly="readonly">
										</td>
										<td align="center">
											<input type="text"   name="mod1List[${pagestatus.index}].tuhao" id="tuhao_${pagestatus.index}" 
											onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4" value="${detail.tuhao}"
												style="text-align: center;width:100%;height:100%;" readonly="readonly">
										</td>
										<td align="center">
											<input type="text"  name="mod1List[${pagestatus.index}].needFinalDate" id="needFinalDate_${pagestatus.index}"
												onkeyup="this.size=(this.value.length>6?this.value.length:6);" size="6" value="${detail.needFinalDate}"
												style="text-align: center;width:100%;height:100%;" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})">
										</td>
										<td align="center">
											<input type="text" value="${detail.estimatePrice}" name="mod1List[${pagestatus.index}].estimatePrice"
										id="estimatePrice_${pagestatus.index}" onblur="compPriceAndNum('')" size="4"
										style="text-align: center; width: 100%; height: 100%;" >
										</td>
										<td align="center">
											<textarea  name="mod1List[${pagestatus.index}].remarks"  id="remarks_${pagestatus.index}"
											onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4">${detail.remarks}</textarea>
										</td>
										<td>
											<div class="lcell span6">
												<div class="lrow">
													<div class="lcell span5">
														<span class="upload">
															<s:if test="#detail.fileUrl!=null&&#detail.fileUrl!=''">
																<input type="file" class="upload-input-file" title="file_${pagestatus.index}" 
																name="mod1List[${pagestatus.index}].files"
																onchange="javascript:changeFile('_${pagestatus.index}',this)"/>
																<input type="button" class="upload-btn" value="${detail.fileName}" 
																id="showFile_${pagestatus.index}"  />
																<input type="hidden" value="${detail.fileName}" id="fileName_${pagestatus.index}"
																name="mod1List[${pagestatus.index}].fileName" >
															</s:if>
															<s:else>
																<input type="file" class="upload-input-file" title="file_${pagestatus.index}" 
																name="mod1List[${pagestatus.index}].files" id="file_0"
																onchange="javascript:changeFile('_${pagestatus.index}',this)"/>
																<input type="button" class="upload-btn" value="浏览文件" 
																id="showFile_${pagestatus.index}" />
															</s:else>
														</span>
													</div> 
												</div>                        
											</div>
										</td>
										<td align="center" style="width：130px;">
											<input type="button" value="增加" onclick="addAList()" />
											<input type="button" value="删除" onclick="delBtn(this)" />
										</td>
									</tr>
								</s:iterator>
							</s:if>
							<tr>
														
						</tbody>
						<tfoot>
							<tr>
								<th colspan="20">
									物料用途：
									<textarea rows="2" cols="130" name="planTotal.application" >${planTotal.application}</textarea>
								</th>
							</tr>
							<tr>
								<th colspan="20">
									&nbsp;总备注：
									<textarea rows="2" cols="130" name="planTotal.remark">${planTotal.remark }</textarea>
								</th>
							</tr>
						</tfoot>
					</table>
					<input type="hidden" value="${fn:length(planTotal.details)}" id="listSize"/>
					<input type="button" value="提交" class="input" onclick="submitBth()">
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
		var tab_option =0;
		var allPro;
$(function(){
	var listSize = $("#listSize").val();
	if(listSize==null && listSize>0){
		tab_option = 0;
	}else{
		tab_option = parseInt(listSize);
	}
	
	//下拉项目菜单
	$.ajax( {
		type : "POST",
		url : "QuotedPrice_getQuotedPriceByCon.action",
		dataType : "json",
		async : false, 
		success : function(msg) {
			allPro = msg;
			/*$.each(msg, function(i, n) {
				$("#proNumber_0").appendTo(
						"<option value='" + n.id + "'>" + n.proName +"</option>");
			});*/
		}
	});
	for(var i=0;i<tab_option;i++){
		//var proNum =$("#proNumber_"+i).val();
		$.each(allPro, function(j, n) {
			$("#proNumber_"+i).append(
					"<option value='" + n.quotedNumber + "'>" + n.quotedNumber +"</option>");
		});
	}
	
});
	


function submitBth(){
	$("#pageForm").submit();
}
	
function getwgj(className){
	var markId = $("#markId"+className).val();
	var kgliao = $("#kgliao"+className).val();
	if(markId!=""){
		$.ajax( {
			type : "POST",
			url : "PriceAction!getAllNames.action",
			data : {
				'yuanclAndWaigj.markId' : markId
			},
			dataType : "json",
			success : function(data) {
				if (data != null) {
					if(data.name!=null && data.name!="null"){
						$("#proName"+className).val(data.name);
						$("#proName"+className).attr("size",data.name.length+3);
						$("#proName"+className).attr("readonly","readonly");
						$("#markId"+className).attr("readonly","readonly");
					}
					if(data.specification!=null && data.specification!="null"){
						$("#specification"+className).val(data.specification);
						$("#specification"+className).attr("size",data.name.length+3);
						$("#specification"+className).attr("readonly","readonly");
					}
					if(data.wgType!=null && data.wgType!="null"){
						$("#wgType"+className).val(data.wgType);
						$("#wgType"+className).attr("size",data.name.length+3);
						$("#wgType"+className).attr("readonly","readonly");
					}
					if(data.banbenhao!=null && data.banbenhao!="null"){
						$("#banben"+className).val(data.banbenhao);
						$("#banben"+className).attr("size",data.name.length+3);
						$("#banben"+className).attr("readonly","readonly");
					}
					if(data.kgliao!=null && data.kgliao!="null"){
						$("#kgliao"+className).val(data.kgliao);
						$("#kgliao"+className).click(function(){
							//console.log("不可编辑");
							alert("不可编辑");
					        return false;
					    });   
					}
					if(data.tuhao!=null && data.tuhao!="null"){
						$("#tuhao"+className).val(data.tuhao);
						$("#tuhao"+className).attr("size",data.tuhao.length+3);
						$("#tuhao"+className).attr("readonly","readonly");
					}
					$("#type"+className).val("外购");
					if(data.unit!=null && data.unit!="null"){
						$("#unit"+className).val(data.unit);
						$("#unit"+className).attr("size",data.unit.length+3);
						$("#unit"+className).attr("readonly","readonly");
					}
					estimatePrice(className);//预估价格
					compPriceAndNum('');
					$("#cgnumber"+className).focus();//数量聚焦
				}
			}
		})
	}
}

function check(){
	var index = 0;
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
			var status = "${param.status}";
			if(status==null || status!="nofile" ){
				var file = document.getElementById("file_"+i);
				var showFile = $("#showFile_"+i).val();
				if ((file ==null || file.value == "") && showFile =="浏览文件") {
					alert("第"+index+"行必须上传附件");
					return false;
				}
			}
			
			
			var flag = false;
			//查询件号在外购件库中是否在外购件库中存在
			var name = $("#proName_"+i).val();
			var specification = $("#specification_"+i).val();
			var wgType = $("#wgType_"+i).val();
			var banbenhao = $("#banben_"+i).val();
			var kgliao = $("#kgliao_"+i).val();
			var unit = $("#unit_"+i).val();
			$.ajax( {
				type : "POST",
				url : "yuanclAndWaigjAction_checkYuanclAndWaigjByinfo.action",
				dataType : "json",
				async : false, 
				data : {
					'yuanclAndWaigj.markId' : markId,
					'yuanclAndWaigj.wgType' : wgType,
					'yuanclAndWaigj.name' : name,
					'yuanclAndWaigj.kgliao' : kgliao,
					'yuanclAndWaigj.specification' : specification,
					'yuanclAndWaigj.unit' : unit,
					'yuanclAndWaigj.banbenhao' : banbenhao,
					'tag':'getWaiGouJian'
				},
				success : function(data) {
					if(data!=null){
						flag=true;
					}else{
						flag=false;
						alert("第"+index+"行物料在外购件库中不存在，请到外购件库中添加物料。");
						$("#markId_"+i).focus();
					}
				},error:function(){
					alert("系统异常");
					return false;
				}
			});
			if(!flag){
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
		url : "PriceAction!searchAllNames.action",
		dataType : "json",
		cache:false,
		data : {
			'yuanclAndWaigj.markId' : obj.value
		},
		success : function(data) {
			$("#showAll" + className).empty();
			$(data).each(function() {
				var markId = $(this).attr('markId')
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
						+ $(this).attr('name')
						+ "__"
						+ $(this).attr('wgType')
						+ "__"
						+ $(this).attr('specification')
						+ "__"
						+ $(this).attr('unit')
						+ "__"
						+ $(this).attr('banbenhao')
						+ "__"
						+ $(this).attr('tuhao')
						+ "__"
						+ $(this).attr('kgliao')
						+ "<span style='visibility: hidden;'>"
						+ $(this).attr('markId')
						+ "__"
						+ $(this).attr('name')
						+ "__"
						+ $(this).attr('wgType')
						+ "__"
						+ $(this).attr('specification')
						+ "__"
						+ $(this).attr('unit')
						+ "__"
						+ $(this).attr('banbenhao')
						+ "__"
						+ $(this).attr('tuhao')
						+ "__"
						+ $(this).attr('kgliao')
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

var addIndex = 1;
function addAList(){
	var str = $("#ta").val();
	var fileText = $("#fileText").val();
	var append = '	<textarea  name="mod1List[0].remarks" id="remarks_0" onkeyup="this.size=(this.value.length>4?this.value.length:4);" size="4" ></textarea>'+
					'</td></td><td>'+fileText+'</td><td align="center" style="width：130px;">' +
					'<input type="button" value="增加" onclick="addAList()" /><input type="button" value="删除" onclick="delBtn(this)" />'+
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
	$.each(allPro, function(i, n) {
		$("#proNumber_"+tab_option).append(
				"<option value='" + n.quotedNumber + "'>" + n.quotedNumber +"</option>");
	});
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
			if(tab_option>index){
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
	var arrEntities={'lt':'<','gt':'>','nbsp':' ','amp':'&','quot':'"'};
	html = html.replace(/&(lt|gt|nbsp|amp|quot);/ig,function(all,t){return arrEntities[t];});
	var htmls = html.split("__");
	$(".shortname" + className).val(html);
	$("#markId" + className).val(htmls[0]);
	var name=htmls[1];
	var specification=htmls[3];
	var wgType = htmls[2];
	var banbenhao=htmls[5];
	var kgliao = htmls[7];
	var tuhao = htmls[6];
	var unit = htmls[4];
	
	if(name!=null && name!="null"){
		$("#proName"+className).val(name);
		$("#proName"+className).attr("size",name.length+3);
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
	if(banbenhao!=null && banbenhao!="null"){
		$("#banben"+className).val(banbenhao);
		$("#banben"+className).attr("size",banbenhao.length+3);
		$("#banben"+className).attr("readonly","readonly");
	}
	if(kgliao!=null && kgliao!="null"){
		$("#kgliao"+className).val(kgliao);
		$("#kgliao"+className).click(function(){
			//console.log("不可编辑");
			alert("不可编辑");
	        return false;
	    });   
	}
	if(tuhao!=null && tuhao!="null"){
		$("#tuhao"+className).val(tuhao);
		$("#tuhao"+className).attr("size",tuhao.length+3);
		$("#tuhao"+className).attr("readonly","readonly");
	}
	
	if(unit!=null && unit!="null"){
		$("#unit"+className).val(unit);
		$("#unit"+className).attr("size",unit.length+3);
		$("#unit"+className).attr("readonly","readonly");
	}
	
	estimatePrice(className);//预估价格
	compPriceAndNum('');//计算总价格
	$("#cgnumber"+className).focus();//数量聚焦
	
	$("#cgnumber"+className).select();
}

function submitFile(){
      var form = new FormData(document.getElementById("fromFile"));
      var file = $("#file").val();
      if(file!=null){
      	$.ajax({
                url:"${pageContext.request.contextPath}/ManualOrderPlanAction_analysisFromFile.action",
                type:"post",
                data:form,
                processData:false,
                contentType:false,
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
                    				$("#markId"+className).attr("readonly","readonly");
                    			}
                    			if(name!=null && name!="null"){
									$("#proName"+className).val(name);
									$("#proName"+className).attr("size",name.length+3);
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
								if(banbenhao!=null && banbenhao!="null"){
									$("#banben"+className).val(banbenhao);
									$("#banben"+className).attr("size",banbenhao.length+3);
									$("#banben"+className).attr("readonly","readonly");
								}
								if(kgliao!=null && kgliao!="null"){
									$("#kgliao"+className).attr("value",kgliao);
									$("#kgliao"+className).click(function(){
										//console.log("不可编辑");
										alert("不可编辑");
								        return false;
								    });   
								}
								if(tuhao!=null && tuhao!="null"){
									$("#tuhao"+className).val(tuhao);
									$("#tuhao"+className).attr("size",tuhao.length+3);
									$("#tuhao"+className).attr("readonly","readonly");
								}
								
								if(unit!=null && unit!="null"){
									$("#unit"+className).val(unit);
									$("#unit"+className).attr("size",unit.length+3);
									$("#unit"+className).attr("readonly","readonly");
								}
								estimatePrice(className);//查找价格
								compPriceAndNum('');//计算总价格
							                    			
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
 
 
 //添加文件
 function changeFile(className,obj){
	 
	 var fileUrl = $(obj).val();
	 var urlArr = fileUrl.split("\\");
	 var getName = urlArr[urlArr.length-1];
	 if(fileUrl!=null && getName!=null && getName !=""){
		 $("#showFile"+className).val(getName);
	 }else{
		 $("#showFile"+className).val("浏览文件");
		 $("#fileName"+className).val(null);//旧文件名称
	 }
 }
 
 function cancalFile(className){
	 $("#showFile"+className).val("浏览文件");
	 $("#fileName"+className).val("");
 }
 
</SCRIPT>
	</body>
</html>
