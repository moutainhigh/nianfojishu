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
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/javascript/zTree/js/jquery.ztree.excheck-3.5.js">
</script>
		<div id="bodyDiv" align="center" class="transDiv">
		</div>
		<div id="contentDiv"
			style="position: absolute; z-index: 255; width: 900px; display: none;"
			align="center">
			<div id="closeDiv"
				style="position: relative; top: 165px; left: 0px; right: 200px; z-index: 255; background: url(<%=basePath%>/images/bq_bg2.gif); width: 980px;">
				<table style="width: 100%">
					<tr>
						<td>
							<span id="title">内审人员选择</span>
						</td>
						<td align="right">
							<img alt="" src="<%=basePath%>/images/closeImage.png" width="30"
								height="32" onclick="chageDiv('none')">
						</td>
					</tr>
				</table>
				<div id="operatingDiv"
					style="background-color: #ffffff; width: 100%;">
					<iframe id="showProcess" src="" marginwidth="0" marginheight="0"
						hspace="0" vspace="0" frameborder="0" scrolling="yes"
						style="width: 98%; height: 500px; margin: 0px; padding: 0px;"></iframe>
				</div>
			</div>
		</div>
		<div align="left">
			<font color="red">注:1.件号替换请填写好新的件号和版本号<br />
				2.单独升版的意思为只对同一个BOM内的此零件发生版本升级非零件替换和版本升级不允许单独升版</font>
		</div>
		<div align="left">
			<!-- 显示树形流水卡片模板 -->
			<div style="float: left; width: 39%" align="left">
				<div style="height: 100%;">
					<ul id="treeDemo" class="ztree"></ul>
				</div>
			</div>
			<!-- 添加流水卡片模板操作 -->
			<div
				style="border: 1px solid #000000; position: absolute; background-color: #ffffff; display: none;"
				id="showProDetail">
				<div align="center" id="showdateil2">
					<table style="width: 100%">
						<tr>
							<th style="font-size: 10px;">
								产品明细与维护
							</th>
						</tr>
					</table>
				</div>
				<div id="selectDiv" align="center">
					<form id="uplvForm" method="post">
						<input type="hidden" name="rootId" value="${rootId}">
						<input type="hidden" name="bbAply.id" value="${id}">
						<input type="hidden" id="ptbbJudgesid" name="ptbbJudges.id"
							value="<s:property value="ptbbJudges.id"/>">
						<table class="table" id="sbtable">
							<tr id="bttr">
								<th>
									件号
								</th>
								<th>
									新件号
								</th>
								<th>
									名称
								</th>
								<th>
									版本
								</th>
								<th>
									版次
								</th>
								<th>
									新版本
								</th>
								<th>
									设变范围
								</th>
								<th>
									单独升版
								</th>
								<th>
									工序更改
								</th>
								<th>
									图纸更改
								</th>
								<th>
									设变明细
								</th>
							</tr>
							<s:if test="ptbbList!=null&&ptbbList.size()>0">
								<s:iterator value="ptbbList" id="pageptbb" status="ptbbStatus">
									<tr id="sbTr_${pageptbb.ptId}" data="sbtr"
										data0="${pageptbb.fptId}" data1="${pageptbb.xuhao}"
										data2="${pageptbb.id}">
										<td>
											<input type="hidden" name="ptbbList[${ptbbStatus.index}].id"
												value="${pageptbb.id}">
											<input type="hidden"
												name="ptbbList[${ptbbStatus.index}].ptId"
												value="${pageptbb.ptId}">
											<input type="hidden"
												name="ptbbList[${ptbbStatus.index}].xuhao"
												value="${pageptbb.xuhao}">
											${pageptbb.markId}
										</td>
										<td>
											<input id="newmarkId${pageptbb.ptId}"
												onblur="checksbtype(${pageptbb.ptId})"
												value="${pageptbb.newmarkId}"
												name="ptbbList[${ptbbStatus.index}].newmarkId"
												style="width: 30px;">
										</td>
										<td>
											${pageptbb.proName}
										</td>
										<td>
											${pageptbb.banBenNumber}
										</td>
										<td>
											${pageptbb.banci}
										</td>
										<td>
											<input id="newBanBenNumber${pageptbb.ptId}"
												onblur="checksbtype(${pageptbb.ptId})"
												value="${pageptbb.newBanBenNumber}"
												name="ptbbList[${ptbbStatus.index}].newBanBenNumber"
												style="width: 30px;">
										</td>
										<td>
											<s:if test="pageptbb.uptype=='件号替换'">
												<input id="sbradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="设变">设变
						<input id="bbsjradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="版本升级">版本升级
						<input id="jhthradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="件号替换"
													checked="checked">件号替换
						</s:if>
											<s:elseif test="pageptbb.uptype=='版本升级'">
												<input id="sbradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="设变">设变
						<input id="bbsjradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="版本升级"
													checked="checked">版本升级
						<input id="jhthradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="件号替换">件号替换
						</s:elseif>
											<s:else>
												<input id="sbradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="设变"
													checked="checked">设变
						<input id="bbsjradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="版本升级">版本升级
						<input id="jhthradio${pageptbb.ptId}" type="radio"
													name="ptbbList[${ptbbStatus.index}].uptype" value="件号替换">件号替换
						</s:else>
										</td>
										<td>
											<s:if test="ptbbList.singleSb=='是'.toString()">
												<input type="radio"
													name="ptbbList[${ptbbStatus.index}].singleSb" value="是"
													checked="checked">是
						 <input type="radio" name="ptbbList[${ptbbStatus.index}].singleSb"
													value="否">否
						</s:if>
											<s:else>
												<input type="radio"
													name="ptbbList[${ptbbStatus.index}].singleSb" value="是">是
						 <input type="radio" name="ptbbList[${ptbbStatus.index}].singleSb"
													value="否" checked="checked">否
						</s:else>
											<s:if test="ptbbList.changeProcess=='是'.toString()">
												<input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeProcess"
													value="是" checked="checked">是
						 <input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeProcess"
													value="否">否
						</s:if>
											<s:else>
												<input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeProcess"
													value="是">是
						 <input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeProcess"
													value="否" checked="checked">否
						</s:else>
										</td>
										<td>
											<s:if test="ptbbList.changeTz=='是'.toString()">
												<input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeTz" value="是"
													checked="checked">是
				        <input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeTz" value="否">否
						</s:if>
											<s:else>
												<input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeTz" value="是">是
				        <input type="radio"
													name="ptbbList[${ptbbStatus.index}].changeTz" value="否"
													checked="checked">否
						</s:else>
										</td>
										<td>
											<textarea id="remark_${pageptbb.ptId}"
												name="ptbbList[${ptbbStatus.index}].remark" data="sbmx">${pageptbb.remark}</textarea>
											<font color="red" size="3">*<font />
										</td>
									</tr>
								</s:iterator>
							</s:if>
							<tr>
								<th colspan="3">
									设变备注
								</th>
								<td colspan="8">
									<textarea id="dhremark" name="bbAply.remark" rows="4" cols="40">${bbAply.remark}</textarea>
								</td>
							</tr>
							<tr>
								<td colspan="11" align="center">
									<input id="subFormBtn" type="button" onclick="submituplvForm()"
										value="提交" style="width: 60px; height: 40px;">
									<input id="dhremark" type="button" value="打回"
										onclick="todahui(${bbAply.id})">
								</td>
							</tr>
						</table>
					</form>
				</div>
				<div style="clear: both;"></div>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<SCRIPT type="text/javascript">
var selectIds="";
var selectMarkIds="";
var trindex=-1;//第几个被选中用来标记参数下标
//自动组装树形结构
var setting = {
	check : {//checkBox选择框
		enable : true,
		autoCheckTrigger : true
	},
	edit : {
		enable : true,
		showRemoveBtn : false,
		showRenameBtn : false,
		showTitle : true
	},
	data : {
		simpleData : {
			enable : true
		},
		key : {
			title : "title"
		}
	},
	callback : {
		onClick : onClick,
		onCheck : onCheck,
		beforeCheck
	},
	view : {
		fontCss : getFont,
		nameIsHTML : true,
		showTitle : true
	}
};


function setCheck() {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), isCopy = true, isMove = true, prev = true, inner = true, next = true;
	zTree.setting.edit.drag.isCopy = isCopy;
	zTree.setting.edit.drag.isMove = isMove;

	zTree.setting.edit.drag.prev = prev;
	zTree.setting.edit.drag.inner = inner;
	zTree.setting.edit.drag.next = next;
}

//加载树形数据
$(document).ready(function(){
	loadTree();
	var ptbbList = <s:property value="ptbbList.size()"/>;
	if(ptbbList!=null){
	var scrollTop = getScrollTop();//获取滚动条离顶部距离
	var deleteTop = (300+ptbbList*45)/2
	var mouseLeft =  500;
	var mouseTop =scrollTop;
	if (mouseTop < 80) {
		mouseTop = 80 ;
	}
		$("#showProDetail").css( {
		"top" : mouseTop,
		"left" : mouseLeft
		});
		$("#showProDetail").show();
		trindex +=ptbbList;
	}
});
//生成
function loadTree() {
	if(${rootId}==0){
		$("treeDemo").append("<li><font size='6'>无此项BOM</font></li>");
		return false;
	}
	$
			.ajax( {
				url : 'procardTemplateGyAction_findSbProcardTemByRootId.action',
				type : 'post',
				dataType : 'json',
				data : {
					id : '${rootId}'
				},
				cache : true,
				success : function(doc) {
					var zNodes = [];
					$(doc)
							.each(function() {
								var nocheck=false;
						if($(this).attr('cansb')==0){
							nocheck =false;
						}else{
							nocheck =true;
						}
								//var b = true;
									if ($(this).attr('procardStyle') == "总成") {
										totalMaxCount = $(this)
												.attr('maxCount');
									}
									//供料属性
									var glsx = '';
									if ($(this).attr('procardStyle') == "外购"
											&& $(this).attr('kgliao') != null
											&& $(this).attr('kgliao') != ""
											&& $(this).attr('kgliao') != "TK") {
										glsx = "<span style='color:green;margin-right:0px;'>"
												+ $(this).attr('kgliao')
												+ "</span>";
									}
									var procardStyle = $(this).attr(
											'procardStyle');
									if (procardStyle == "待定") {
										procardStyle = "<span style='color:red;margin-right:0px;'>待定</span>";
									}
									//单交件状态
									var danjiaojian = $(this).attr(
											'danjiaojian');
									if (danjiaojian == null) {
										danjiaojian = "";
									}
									//半成品状态
									var needProcess = $(this).attr(
											'needProcess');
									if (needProcess == "yes") {
										needProcess = "(半成品)";
									} else {
										needProcess = "";
									}
									var bzStatus = $(this).attr('bzStatus');
									if (bzStatus == null || bzStatus == "") {
										bzStatus = "初始";
									}
									if (bzStatus != "已批准") {
										bzStatus = "<span style='color:red;margin-right:0px;'>"
												+ bzStatus + "</span>";
									} else {
										bzStatus = "";
									}

									var hgstyle = "<span style='font-weight: bolder;font-size: 18px;'>--</span>";
									zNodes
											.push( {
												id : $(this).attr('id'),
												sbStatus : $(this).attr(
														'sbStatus'),
												bzStatus : $(this).attr(
														'bzStatus'),
												bomApplyStatus : $(this).attr(
														'bomApplyStatus'),
												epId2 : $(this).attr('epId2'),
												pId : $(this).attr('fatherId'),
												proStruts : $(this).attr(
														'procardStyle'),
												rootId : $(this).attr('rootId'),
												markId : $(this).attr('markId'),
												danjiaojian : danjiaojian,
												productStyle : $(this).attr(
														'productStyle'),
												belongLayer : $(this).attr(
														'belongLayer'),
												name :	"<span style='font-weight: bolder;font-size: 12px; color:red;'>"
														+ $(this).attr('belongLayer')
														+"</span><span style='font-weight: bolder;font-size: 12px;'>"
														+ procardStyle
														+ needProcess
														+ "</span>"
														+ hgstyle
														+ $(this)
																.attr('markId')
														+ hgstyle
														+ $(this).attr(
																'proName')
														+ danjiaojian
														+ " "
														+ bzStatus + " " + glsx,
												title : $(this).attr(
														'procardStyle')
														+ '--'
														+ $(this)
																.attr('markId')
														+ '--'
														+ $(this).attr(
																'proName')
														+ danjiaojian
														+ needProcess,
												click : false,
												drop : true,
												open : true,
												checked : false,
												nocheck :nocheck
											});

								});
					$.fn.zTree.init($("#treeDemo"), setting, zNodes);
			//父子关联关系(双向关联)
				$.fn.zTree.getZTreeObj("treeDemo").setting.check.chkboxType = {
				//	"Y" : "ps",
				//	"N" : "ps"
				};
				},
				error : function() {
					alert("服务器异常!");
				}
			});

}
function getFont(treeId, node) {
	return node.font ? node.font : {};
}
var moveId = 0;
function onDrag(event, treeId, treeNodes) {
	moveId = treeNodes[0].id;
}
function beforeDrag(treeId, treeNodes) {
	for ( var i = 0, l = treeNodes.length; i < l; i++) {
		if (treeNodes[i].drag === false) {
			return false;
		}
	}
	return true;
}function beforeDrop(treeId, treeNodes, targetNode, moveType) {
	if (!window.confirm("是否移动bom结构")) {
		return false;
	}
	if (targetNode.id == null) {
		return false;
	}
	var moveok = targetNode ? targetNode.drop !== false : true;
	if (!moveok) {
		return moveok;
	}
	if (moveok) {
		//alert(moveId);
		$.ajax( {
			type : "POST",
			url : "ProcardTemplateAction!moveProcardTemplate.action",
			dataType : "json",
			data : {
				moveId : moveId,
				targetId : targetNode.id
			},
			success : function(msg) {
				alert(msg.message);
				loadTree();
				return true;
			}
		});
	}
}


//点击回调函数
function onClick(event, treeId, treeNode, clickFlag) {
}

//选中回调函数
function onCheck(e, treeId, treeNode) {
	
}
//选中函数
function beforeCheck(treeId, treeNode) {
	//记录被勾选的零件
	var count = 0;//被选中个数
	var type = "add";
	if(selectIds.length==0){
		selectIds=treeNode.id+"";
		selectMarkIds=treeNode.markId+"";
		count++;
		trindex++;
	}else{
		var ids = selectIds.split(";");
		selectIds="";
		var index=0;//是否已被选中
		for(var i=0;i<ids.length;i++){
			if(ids[i]!=treeNode.id){
				if(i==0){
					selectIds=ids[i]+"";
				}else{
					selectIds=selectIds+";"+ids[i];
				}
				count++;
			}else{
				type="remove";
				$("#sbTr_"+treeNode.id).remove();
				index++;
				count--;
			}
		}
		if(index==0){
			if(selectMarkIds.length==0){
				selectMarkIds=treeNode.markId+"";
			}else{
				var smarkIds = selectMarkIds.split(";");
				for(var i=0;i<smarkIds.length;i++){
				if(smarkIds[i]==treeNode.markId){
					alert("此件号已勾选!");
					return fasle;
				}
				}
				selectMarkIds = selectMarkIds+";"+treeNode.markId;
			}
			selectIds=selectIds+";"+treeNode.id;
			trindex++;
		}else{
			var smarkIds = selectMarkIds.split(";");
				for(var i=0;i<smarkIds.length;i++){
				if(smarkIds[i]!=treeNode.markId){
					if(i==0){
						selectMarkIds=smarkIds[i]+"";
					}else{
						selectMarkIds=selectMarkIds+";"+smarkIds[i];
					}
				}
			}
		}
	}
	$("#selectDiv").show();
	var e = event || window.event;
	var scrollTop = getScrollTop();//获取滚动条离顶部距离
	var deleteTop = (300+count*45)/2
	var mouseLeft = e.clientX + 300;
	var mouseTop = e.clientY - deleteTop+scrollTop;
	if (mouseTop < 0) {
		mouseTop = 0;
	}
	//显示悬浮窗样式的项目
	$("#showProDetail").css( {
		"top" : mouseTop,
		"left" : mouseLeft
	});
	$("#showProDetail").show();
	$("#showdateil2").show();
	if(type=="add"){
		$.ajax({
		 type : "post",
		 url : "procardTemplateGyAction_getProcardTemForsb.action",
		 dataType : "json",
		 data :{
			id : treeNode.id,
			rootId : '${rootId}'
		 },
		 success : function(data){
			 if(data!=null){
				 var xuhao=data.beforeTrid;
				 var banben="";
				 if(data.banBenNumber!=null){
					 banben=data.banBenNumber;
				 }
				 var banci=0;
				 if(data.banci!=null){
					 banci=data.banci;
				 }
				 var html = "<tr id='sbTr_"+treeNode.id+"' data='sbtr' data0='"+treeNode.pId+"' data1='"+xuhao+"' data2='"+treeNode.id+"'>";
				 html +="<td><input type='hidden' name='ptbbList["+trindex+"].ptId' value='"+treeNode.id+"'><input type='hidden' name='ptbbList["+trindex+"].xuhao' value='"+xuhao+"'>"
				 +data.markId+"</td>" +
				 "<td><input type='text' id='newmarkId"+treeNode.id+"' name='ptbbList["+trindex+"].newmarkId' onblur='checksbtype("+treeNode.id+")'></td>" +
				 "<td>"+data.proName+"</td>" +
				 "<td>"+banben+"</td>" +
				 "<td>"+data.banci+"</td>" +
				 "<td align='center'><input id='newBanBenNumber"+treeNode.id+"' onblur='checksbtype("+treeNode.id+")' name='ptbbList["+trindex+"].newBanBenNumber' style='width: 30px;' ></td>" +
				 "<td><input id='sbradio"+treeNode.id+"' type='radio' name='ptbbList["+trindex+"].uptype' value='设变' checked='checked'>设变" +
				 "<input id='bbsjradio"+treeNode.id+"' type='radio' name='ptbbList["+trindex+"].uptype' value='版本升级'>版本升级" +
				 "<input id='jhthradio"+treeNode.id+"' type='radio' name='ptbbList["+trindex+"].uptype' value='件号替换'>件号替换</td>" +
				 "<td><input type='radio' name='ptbbList["+trindex+"].singleSb' value='是' >是" +
				 "<input type='radio' name='ptbbList["+trindex+"].singleSb' value='否' checked='checked'>否</td>" +
				 "<td><input type='radio' name='ptbbList["+trindex+"].changeProcess' value='是' checked='checked'>是" +
				 "<input type='radio' name='ptbbList["+trindex+"].changeProcess' value='否'>否</td>" +
				 "<td><input type='radio' name='ptbbList["+trindex+"].changeTz' value='是' checked='checked'>是" +
				 "<input type='radio' name='ptbbList["+trindex+"].changeTz' value='否'>否</td>" +
				 "<td><textarea id='remark_"+treeNode.id+"' name='ptbbList["+trindex+"].remark' data='sbmx'></textarea><font color='red' size='3'>*<font/></td>" ;
			 	 html+="</tr>";
		 		if(count>0){
		 			var trs=$("tr[data*='sbtr']");
		 			var minxuhao = 0;
		 			var beforeTr=$("#bttr");
		 			for(var j=0;j<trs.length;j++){ 
		 				if(($(trs[j]).attr("data1")-xuhao)<0){
		 					if((minxuhao-$(trs[j]).attr("data1"))<0){
		 						minxuhao=$(trs[j]).attr("data1");
		 						beforeTr=trs[j];
		 					}
		 				}
		 			}
		 			$(beforeTr).after(html);
		 		}else{
		 			$("#sbtable").append(html);
		 		}
			 }
		 }
		});
	}else{
		
	}
}


<%--function loadcpry(){//加载初评人员--%>
<%--	$.ajax({--%>
<%--		 type : "post",--%>
<%--		 url : "procardTemplateGyAction_findCpryList.action",--%>
<%--		 dataType : "json",--%>
<%--		 data :{--%>
<%--		 },--%>
<%--		 success : function(data){--%>
<%--			 if(data!=null){--%>
<%--				 var html="";--%>
<%--				 var n=0;--%>
<%--				 $(data).each(function() {--%>
<%--					 html+="<input type='checkbox' name='checkboxs' value='"+$(this).attr("id")+"'>"+$(this).attr("dept")+"-"+$(this).attr("userName")+"&nbsp;&nbsp;";--%>
<%--						n++;		--%>
<%--				 });--%>
<%--				 $("#cprytd").append(html);--%>
<%--			 }--%>
<%--		 }--%>
<%--		});--%>
<%--}--%>

function selectnsPeople(id){
	chageDiv('block');
	var ids= $("#selectUserId").val();
	$("#showProcess").attr("src","procardTemplateGyAction_findAboutDept.action?ids="+ids);
}
function checksbtype(bbIndex){
	var newmarkId=$("#newmarkId"+bbIndex).val();
	var newbbNumber=$("#newBanBenNumber"+bbIndex).val();
	if(newmarkId!=null&&newmarkId.length>0){
		$("#sbradio"+bbIndex).removeAttr("checked");
		$("#bbsjradio"+bbIndex).removeAttr("checked","checked");
		$("#jhthradio"+bbIndex).attr("checked","checked");
	}else if(newbbNumber!=null&&newbbNumber.length>0){
		$("#sbradio"+bbIndex).removeAttr("checked");
		$("#bbsjradio"+bbIndex).attr("checked","checked");
		$("#jhthradio"+bbIndex).removeAttr("checked","checked");
	}else{
		$("#sbradio"+bbIndex).attr("checked");
		$("#bbsjradio"+bbIndex).removeAttr("checked","checked");
		$("#jhthradio"+bbIndex).removeAttr("checked","checked");
	}
	
	
	
}
function submituplvForm(){
	$("#subFormBtn").attr("disabled","disabled");
	$ .ajax( {
					type : "post",
					url : "procardTemplateSbAction_upptlvNew.action",
					data :   $("#uplvForm").serialize(),
					dataType : "json",
					success : function(data) {
						$("#subFormBtn").removeAttr("disabled");
						alert(data);
						if(data=="版本升级申请成功!"){
							parent.window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id=${id}";
						}
			}
	});
}

function needDeptJudege(need){
	if(need=="是"){
		$("#selectUsers").show();
		$("#selectpjBtn").show();
	}else{
		$("#selectUsers").hide();
		$("#selectpjBtn").hide();
	}
}
function todahui(id){
	if(confirm("是否确定关闭!")){
		$ .ajax( {
					type : "post",
					url : "procardTemplateGyAction_dahuisb.action",
					data :  {
						id : id,
						remark : $("#dhremark").val()
					},
					dataType : "json",
					success : function(data) {
						if(data=="true"){
							alert("关闭成功!");
							parent.window.location.href="procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="+id;
						}else{
							alert(data);
						}
						
					}
		});
	}
}
</SCRIPT>
	</body>
</html>