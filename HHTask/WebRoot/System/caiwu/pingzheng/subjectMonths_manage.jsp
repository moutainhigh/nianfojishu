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
<style>
/*按钮*/
.icon_div {
	display: inline-block;
	height: 25px;
	width: 35px;
	background:
		url(http://c.csdnimg.cn/public/common/toolbar/images/f_icon.png)
		no-repeat 12px -127px;
}

.icon_div a {
	display: inline-block;
	width: 27px;
	height: 20px;
	cursor: pointer;
}

/*end--按钮*/ /*ztree表格*/
.ztree {
	padding: 0;
	border: 2px solid #CDD6D5;
}

.ztree li a {
	vertical-align: middle;
	height: 30px;
}

.ztree li a.curSelectedNode {
	height: 30px;
}

.ztree li>a {
	width: 100%;
}

.ztree li>a,.ztree li a.curSelectedNode {
	padding-top: 0px;
	background: none;
	border: none;
	cursor: default;
	opacity: 1;
}

.ztree li ul {
	padding-left: 0px
}

.ztree div.diy span {
	line-height: 30px;
	vertical-align: middle;
}

.ztree div.diy {
	height: 100%;
	width: 22%;
	line-height: 30px;
	border-top: 1px dotted #ccc;
	border-left: 1px solid #000000;
<%--	text-align: center;--%>
	display: inline-block;
	box-sizing: border-box;
	color: #000000;
	font-family: "SimSun";
	font-size: 14px;
	overflow: hidden;
	font-weight: bolder;
}

.ztree div.diy:first-child {
<%--	text-align: left;--%>
	text-indent: 10px;
	border-left: thin;
}

.ztree .head {
	background: #5787EB;
}

.ztree .head div.diy {
	border-top: none;
	border-right: 1px solid #CDD2D4;
	color: #fff;
	font-family: "Microsoft YaHei";
	font-size: 14px;
}
/*end--ztree表格*/
</style>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: auto;">
			<div align="center" style="width: auto;">
				<input type="text" value="${months}" name="months" id="months"
							onClick="WdatePicker({dateFmt:'yyyy-MM',skin:'whyGreen'})"
							class="Wdate"
							/> 
				<input type="button" value="查询"  onclick="loadTree()"/>
							<br />
				<div align="left" >
					<!-- 显示科目树形 -->
					<div style=" display:inline-block;*display:inline; *zoom:1;" >
					<div style="height: 100%;">
						<ul id="treeDemo" class="ztree" style="border: 1px solid #000;"></ul>
					</div>
				</div>
					<div style="clear: both;"></div>
				</div>
			</div>
			<br>
		</div>
		<%@include file="/util/foot.jsp"%>
		</center>
		
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
		<script type="text/javascript">
//========================================zTree显示
var id, pId, name, subjectRate, rootId, belongLayer, qichuMoney, money;

//自动组装树形结构
var setting = {
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
	},
	view : {
		fontCss : getFont,
		nameIsHTML : true,
		showTitle : true,
		showLine : true,
		showIcon : true,
		addDiyDom : addDiyDom
	}
};




//读取树形数据
<%--$(document).ready(function() {--%>
<%--	mfzTree();--%>
<%--});--%>
/**
 * 自定义DOM节点
 */
function addDiyDom(treeId, treeNode) {
	var spaceWidth = 15;
	var liObj = $("#" + treeNode.tId);
	var aObj = $("#" + treeNode.tId + "_a");
	var switchObj = $("#" + treeNode.tId + "_switch");
	var icoObj = $("#" + treeNode.tId + "_ico");
	var spanObj = $("#" + treeNode.tId + "_span");
	aObj.attr('title', '');
	aObj.append('<div class="diy swich"></div>');
	var div = $(liObj).find('div').eq(0);
	switchObj.remove();
	spanObj.remove();
	icoObj.remove();
	div.append(switchObj);
	div.append(spanObj);
	var editStr = '';
	var borrowYearBegingMoney = treeNode.borrowYearBegingMoney;
	var lendYearBegingMoney = treeNode.lendYearBegingMoney;
	var borrowYearSumMoney = treeNode.borrowYearSumMoney;
	var lendYearSumMoney = treeNode.lendYearSumMoney;
	var borrowQichuMoney = treeNode.borrowQichuMoney;
	var lendQichuMoney = treeNode.lendQichuMoney;
	var borrowMoney = treeNode.borrowMoney;
	var lendMoney = treeNode.lendMoney;
	var borrowJieyuMoney = treeNode.borrowJieyuMoney;
	var lendJieyuMoney = treeNode.lendJieyuMoney; 
<%--	editStr+='<div class="diy" style="text-align: right;width:120px;">'+treeNode.subNumber+'</div>'--%>
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(borrowYearBegingMoney==null?'&nbsp;':Math.round(borrowYearBegingMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(lendYearBegingMoney==null?'&nbsp;':Math.round(lendYearBegingMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(borrowQichuMoney==null?'&nbsp;':Math.round(borrowQichuMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(lendQichuMoney==null?'&nbsp;':Math.round(lendQichuMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(borrowMoney==null?'&nbsp;':Math.round(borrowMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(lendMoney==null?'&nbsp;':Math.round(lendMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(borrowJieyuMoney==null?'&nbsp;':Math.round(borrowJieyuMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(lendJieyuMoney==null?'&nbsp;':Math.round(lendJieyuMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(borrowYearSumMoney==null?'&nbsp;':Math.round(borrowYearSumMoney*100)/100)+'</div>'
	editStr+='<div class="diy" style="text-align: right;width:100px;">'+(lendYearSumMoney==null?'&nbsp;':Math.round(lendYearSumMoney*100)/100)+'</div>'
	aObj.append(editStr);
}
<%--var mfzTree = function() {--%>
<%--	$.ajax( {--%>
<%--		url : 'SubjectBudgetAction!findAllSBRate.action',--%>
<%--		type : 'post',--%>
<%--		dataType : 'json',--%>
<%--		cache : true,--%>
<%--		success : function(doc) {--%>
<%--			var zNodes = [];--%>
<%--			$(doc).each(function() {--%>
<%--				zNodes.push( {--%>
<%--					id : $(this).attr('id'),--%>
<%--					name : $(this).attr('name') + "--" + $(this).attr('money'),--%>
<%--					hiddenName : $(this).attr('name'),--%>
<%--					money : $(this).attr('money'),--%>
<%--					qichuMoney : $(this).attr('qichuMoney'),--%>
<%--					pId : $(this).attr('fatherId'),--%>
<%--					rootId : $(this).attr('rootId'),--%>
<%--					belongLayer : $(this).attr('belongLayer'),--%>
<%--					click : false--%>
<%--				});--%>
<%--			});--%>
<%--			$.fn.zTree.init($("#treeDemo"), setting, zNodes);--%>
<%--		},--%>
<%--		error : function() {--%>
<%--			alert("服务器异常!");--%>
<%--		}--%>
<%--	});--%>
<%--};--%>
$(document).ready(loadTree());
var totalMaxCount = 0;
//生成
function loadTree() {
	$("#treeDemo").empty();
	$
			.ajax( {
				url : 'SubjectBudgetAction!findSubMonths.action',
				type : 'post',
				dataType : 'json',
				data:{months:$("#months").val()},
				cache : true,
				success : function(doc) {
					var zNodes = [];
					$(doc)
							.each(function() {
								$("#months").val($(this).attr('bookKDate'));
									var hgstyle = "<span style='font-weight: bolder;font-size: 18px;'>--</span>";
									var subNumber = $(this).attr('subNumber')==null?'':$(this).attr('subNumber');
				zNodes.push( {
					id : $(this).attr('id'),
					name :"<font color ='red'>"+subNumber+"</font>&nbsp;"+ $(this).attr('name'),
					hiddenName : $(this).attr('name'),
					fatherName : $(this).attr('fatherName'),
					subNumber : subNumber,
					money : $(this).attr('money'),
					borrowYearBegingMoney:$(this).attr('borrowYearBegingMoney'),
					lendYearBegingMoney:$(this).attr('lendYearBegingMoney'),
					borrowYearSumMoney:$(this).attr('borrowYearSumMoney'),
					lendYearSumMoney:$(this).attr('lendYearSumMoney'),
					borrowQichuMoney:$(this).attr('borrowQichuMoney'),
					lendQichuMoney:$(this).attr('lendQichuMoney'),
					borrowMoney:$(this).attr('borrowMoney'),
					lendMoney:$(this).attr('lendMoney'),
					borrowJieyuMoney:$(this).attr('borrowJieyuMoney'),
					lendJieyuMoney:$(this).attr('lendJieyuMoney'),
					pId : $(this).attr('fatherId'),
					rootId : $(this).attr('rootId'),
					belongLayer : $(this).attr('belongLayer'),
					click : false,
					drop : true,
					open : true
				});
			});
					$.fn.zTree.init($("#treeDemo"), setting, zNodes);
					//添加表头
					var li_head = ' <li class="head" style="width: auto;"><a><div class="diy" >科目名称</div><div class="diy" style="width:100px;">年初借方余额</div><div class="diy" style="width:100px;">年初贷方余额</div><div class="diy" style="width:100px;">期初借方余额</div><div class="diy"  style="width:100px;">期初贷方余额</div><div class="diy"  style="width:100px;">本期借方发生</div><div class="diy" style="width:100px;">本期贷方发生</div><div class="diy" style="width:100px;">期末借方余额</div>' +
					'<div class="diy" style="width:100px;">期末贷方余额</div><div class="diy" style="width:100px;">本年借方累计</div><div class="diy" style="width:100px;">本年贷方累计</div></a></li>';
					var rows = $("#treeDemo").find('li');
					if (rows.length > 0) {
						rows.eq(0).before(li_head)
					} else {
						$("#treeDemo").append(li_head);
						$("#treeDemo")
								.append(
										'<li ><div style="text-align: center;line-height: 30px;" >无符合条件数据</div></li>')
					}
				},
				error : function() {
					alert("服务器异常!");
				}
			});

}
function getFont(treeId, node) {
	return node.font ? node.font : {};
}
//点击回调函数
function onClick(event, treeId, treeNode, clickFlag) {
	//var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	//var nodes = zTree.getSelectedNodes();
	//折叠、展开操作
	//for ( var i = 0, l = nodes.length; i < l; i++) {
	//	zTree.expandNode(nodes[i], null, null, null, true);
	//	}
	//赋值
	$("#id").val(treeNode.id);
	$("#rootId").val(treeNode.rootId);
	$("#fatherId").val(treeNode.id);
	$("#belongLayer").val(treeNode.belongLayer + 1);
	$("#name").val(treeNode.hiddenName);
	$("#fatherName").val(treeNode.fatherName);
	$("#money").val(treeNode.money);
	$("#borrowYearBegingMoney").val(treeNode.borrowYearBegingMoney==null?'':Math.round(treeNode.borrowYearBegingMoney*100)/100);
	$("#lendYearBegingMoney").val(treeNode.lendYearBegingMoney==null?'':Math.round(treeNode.lendYearBegingMoney*100)/100);
	$("#borrowYearSumMoney").val(treeNode.borrowYearSumMoney==null?'':Math.round(treeNode.borrowYearSumMoney*100)/100);
	$("#lendYearSumMoney").val(treeNode.lendYearSumMoney==null?'':Math.round(treeNode.lendYearSumMoney*100)/100);
	$("#borrowQichuMoney").val(treeNode.borrowQichuMoney==null?'':Math.round(treeNode.borrowQichuMoney*100)/100);
	$("#lendQichuMoney").val(treeNode.lendQichuMoney==null?'':Math.round(treeNode.lendQichuMoney*100)/100);
	$("#borrowMoney").val(treeNode.borrowMoney==null?'':Math.round(treeNode.borrowMoney*100)/100);
	$("#lendMoney").val(treeNode.lendMoney==null?'':Math.round(treeNode.lendMoney*100)/100);
	$("#borrowJieyuMoney").val(treeNode.borrowJieyuMoney==null?'':Math.round(treeNode.borrowJieyuMoney*100)/100);
	$("#lendJieyuMoney").val(treeNode.lendJieyuMoney==null?'':Math.round(treeNode.lendJieyuMoney*100)/100);
	$("#showdetail").href = "CwVouchersAction!findDetailBysubId.action?id="
			+ treeNode.id;
	//显示添加选项的页面
	$("#selectDiv").show();
	chageDiv('show');
}

//添加节点
var addzTree = function() {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	var treeNode = zTree.getNodeByParam("id", pId);//查询父类节点
	//添加节点
	treeNode = zTree.addNodes(treeNode, {
		id : id,
		pId : pId,
		name : name + " " + subjectRate + "%",
		hiddenName : name,
		subjectRate : subjectRate,
		rootId : rootId,
		belongLayer : belongLayer,
		open : true,
		click : false
	});
};

//更新节点
var updatezTree = function() {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), nodes = zTree
			.getSelectedNodes(), treeNode = nodes[0];
	if (nodes.length == 0) {
		alert("请先选择一个节点");
		return;
	}
	treeNode.name = jieOrDai + "--" + name + "--" + qichuMoney;
	treeNode.hiddenName = name;
	zTree.updateNode(treeNode, true);

};

//删除节点
var delzTree = function() {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), nodes = zTree
			.getSelectedNodes(), treeNode = nodes[0];
	if (nodes.length == 0) {
		alert("请先选择一个节点");
		return;
	}
	zTree.removeNode(treeNode, true);

};

//添加表单
function submitForm(formId, status) {
	$("#showMessage").html("");
	if (status == "add") {
		$.ajax( {
			type : "POST",
			url : 'SubjectBudgetAction!addSubBudgetRate.action',
			dataType : 'json',
			data : $("#" + formId).serialize(),
			cache : false,//防止数据缓存
			success : function(mes) {
				if (mes.success) {
					$("#showMessage").html("添加科目" + mes.message + "成功!");
					var data = mes.data;
					id = data.id;
					pId = data.fatherId;
					name = data.name;
					subjectRate = data.subjectRate;
					rootId = data.rootId;
					belongLayer = data.belongLayer;
					addzTree();
				} else {
					$("#showMessage").html("添加科目" + mes.message + "失败!");
				}
			}
		});
	} else if (status == "update") {
		$.ajax( {
			type : "POST",
			url : 'SubjectBudgetAction!updateSubBudgetRate.action',
			dataType : 'json',
			data : $("#" + formId).serialize(),
			cache : false,//防止数据缓存
			success : function(mes) {
				if (mes.success) {
					$("#showMessage").html("修改科目成功!");
					var data = mes.data;
					name = data.name;
					subjectRate = data.subjectRate;
					jieOrDai = data.jieOrDai;
					qichuMoney = data.qichuMoney;
					updatezTree();
					$("#fatherName").val(data.name);
				} else {
					$("#showMessage").html("修改科目失败!");
				}
			}
		});
	} else if (status == "del") {
		$.ajax( {
			type : "POST",
			url : 'SubjectBudgetAction!delSubBudgetRate.action',
			dataType : 'json',
			data : $("#" + formId).serialize(),
			cache : false,//防止数据缓存
			success : function(mes) {
				if (mes.success) {
					$("#showMessage").html("删除科目" + mes.message + "成功!");
					var data = mes.data;
					if (data != null) {
						$("#id").val(data.id);
						$("#rootId").val(data.rootId);
						$("#fatherId").val(data.id);
						$("#belongLayer").val(data.belongLayer + 1);
						$("#name").val(data.name);
						$("#fatherName").val(data.name);
						$("#subjectRate").val(data.subjectRate);
					} else {
						$("#id").val("");
						$("#rootId").val("");
						$("#fatherId").val("");
						$("#belongLayer").val("");
						$("#name").val("");
						$("#fatherName").val("");
						$("#subjectRate").val("");
					}
					delzTree();
				} else {
					$("#showMessage").html("删除科目" + mes.message + "失败!");
				}
			}
		});
	}
	id = null;
	pId = null;
	name = null;
	subjectRate = null;
	rootId = null;
	belongLayer = null;
}
//=================================== zTree显示结束
</script>
	</body>
</html>
