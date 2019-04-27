<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.task.entity.Users"%>
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
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/css/mobiscroll.core-2.5.2.css">
		<script src="${pageContext.request.contextPath}/javascript/jquery/jquery-3.2.1.js"> </script> 
		<script type="text/javascript">
			// 现在window.$和window.jQuery是3.2.1版本:
			console.log($().jquery); // => '3.2.1'
			var $jq = jQuery.noConflict(true);
			// 现在window.$和window.jQuery被恢复成1.5版本:
			console.log($().jquery); // => '1.5.0'
		
		</script>
		<style type="text/css">
			.ztree{background-color: #ffffff;position: absolute;  z-index: 100; top: 40px;width: 500px;display: none;}
		</style>
	</head>
	<body bgcolor="#ffffff">
		<div class="container">
			<form action="productEBAction!applyBorrowPerson.action" method="post"  id="myform" autocomplete=off onsubmit="return check()">
				<div class="row clearfix">
					<h2 align="center">
						人员借入申请
					</h2>
	            </div>
	            <div class="row">
	            	<div class="form-group col-lg-6">
	                    <div class="input-group">
	                        <span class="input-group-addon">申请班组</span>
	                        <input type="hidden" name="log.borrowJiegouId" id="borrowJiegouId" >
	                        <input type="text"   name="log.sqBanzu" ondblclick="onDoubleClick(2)"
		                        class="form-control" onclick="$('#treeDemo2').show()"
		                        id="sqBanzu" placeholder="选择借入班组" readonly="readonly" style="background-color: #ffffff"/>
                        	<ul id="treeDemo2" class="ztree"  >
                        	</ul>
	 	                </div>
	                </div>
	             	<div class="form-group col-lg-6">
	                    <div class="input-group">
	                        <span class="input-group-addon">选择借出班组</span>
	                        <input type="hidden" name="log.lendJiegouId" id="lendJiegouId" >
	                        <input type="text"   name="log.borrowBanzu" ondblclick="onDoubleClick(1)"
		                        class="form-control" onclick="$('#treeDemo1').show()"
		                        id="borrowBanzu" placeholder="选择借出班组" readonly="readonly" style="background-color: #ffffff"/>
                        	<ul id="treeDemo1" class="ztree"  >
                        	</ul>
	                    </div>
 	                </div>
	            </div>
	            <div class="row">
	                <div class="form-group col-lg-6">
	                    <div class="input-group">
	                        <span class="input-group-addon">借工时数（小时）</span>
	                        <input class="form-control" type="text" name="log.gzHour" id="gzHour">
	                    </div>
	                </div>
	                <div class="form-group col-lg-6">
	                    <div class="input-group">
	                        <span class="input-group-addon">申请人数</span>
	                        <input class="form-control" type="text" name="log.borrowNum" id="borrowNum">
	                    </div>
	                </div>
				</div>
				<div class="row">
					<div class="form-group col-lg-12" id="appendDate">
	                </div>
				</div>
	            <div class="form-group">
					<label for="">
						备注
					</label>
					<input type="text" data-role="datebox" class="form-control" name="log.remarks">
				</div>
				</br>
				<button type="submit" class="btn btn-default text-center" >
					提交
				</button>
			</form>
			<textarea rows="" cols="" id="borrowDateText" style="display: none;">
				<div class="row" id="appendRow_0">
	            	<div class="form-group col-lg-6">
	                    <div class="input-group">
	                        <span class="input-group-addon">借入日期</span>
	                        <input class="form-control" type="text" name="logList[0].sqTime" id="borrowDate_0">
	                    </div>
	                </div>
	                <div class="form-group col-lg-3">
	                	<div class="input-group">
	                		<input type="button" value="增加" class="btn btn-default text-center" onclick="addAList()">
	                		&nbsp;&nbsp;&nbsp;
		                	<input type="button" value="删除" class="btn btn-default text-center" onclick="delAList('_0')">
	                	</div>
	                </div>
	                <div class="form-group col-lg-3" >
	                	<div class="input-group">
	                		<p id="showMessageDiv_0" style="color: red;"></p>
	                	</div>
	                </div>
				</div>
			</textarea>
		</div>
	</body>	
	<script type="text/javascript">
	var mfzTree;
	var addzTree;
	var delzTree;
	var updatezTree;

	var id;
	var pId;
	var name;
	//========================================zTree显示
	//自动组装树形结构
	var setting = {
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			onClick : onClick
		}
	};
	//读取树形数据
	$(document).ready(function() {
		parent.mfzTree();
	});

	parent.mfzTree = function() {
		$.ajax( {
			url : 'productEBAction!getBanZuJieGou.action',
			type : 'post',
			dataType : 'json',
			cache : true,
			success : function(doc) {
				var zNodes = [];
				$(doc).each(
						function() {
							zNodes.push( {
								id : $(this).attr('id'),
								pId : $(this).attr('fatherId'),
								name : $(this).attr('name'),
								click : false
							});

						});
				$.fn.zTree.init($("#treeDemo1"), setting, zNodes);
				var zTree = $.fn.zTree.getZTreeObj("treeDemo1");
				zTree.expandAll(true);
				
				
				$.fn.zTree.init($("#treeDemo2"), setting, zNodes);
				var zTree2 = $.fn.zTree.getZTreeObj("treeDemo2");
				zTree2.expandAll(true);
			},
			error : function() {
				alert("服务器异常!");
			}
		});
	};
	
	function onClick(event, treeId, treeNode, clickFlag) {
		var node = $("#treeDemo1");
		if(node.is(':hidden')){　　//如果node是隐藏的则显示node元素，否则隐藏
		　　    var zTree = $.fn.zTree.getZTreeObj("treeDemo2");
			var nodes = zTree.getSelectedNodes();
			//折叠、展开操作
			for ( var i = 0, l = nodes.length; i < l; i++) {
				zTree.expandNode(nodes[i], null, null, null, true);
			}
			
			$("#sqBanzu").val(treeNode.name);
			$("#borrowJiegouId").val(treeNode.id);//这里是借入班组审批
			$("#treeDemo2").hide();
		}else{
		　　    var zTree = $.fn.zTree.getZTreeObj("treeDemo1");
			var nodes = zTree.getSelectedNodes();
			//折叠、展开操作
			for ( var i = 0, l = nodes.length; i < l; i++) {
				zTree.expandNode(nodes[i], null, null, null, true);
			}
			
			$("#borrowBanzu").val(treeNode.name);
			$("#lendJiegouId").val(treeNode.id);//这里是借出班组审批
			$("#treeDemo1").hide();
		}
		
		
	}
	
	function onDoubleClick(num){
		$("#treeDemo"+num).hide();
		
	}
	
	
	//--------tree结束
	
	function submitFile(){
		var form = new FormData(document.getElementById("fromFile"));
	    var file = $jq("#file").val();
	    if(file!=null){
	    	$jq.ajax({
	              url:"${pageContext.request.contextPath}/productEBAction!importPdata.action",
	              type:"post",
	              data:form,
	              processData:false,
	              contentType:false,
	              async : false,
	              dataType:"json",
	              success:function(data){
	            	  if(data!=""){
			              	alert(data);
	            	  }else{
	            		  alert("导入成功");
	            	  }
	              },
	              error:function(e){
	                  alert("错误！！");
	              }
	          });
	    }
	}
	
	//提交验证
	function check(){
		var sqBanzu = $("#sqBanzu").val();
		var borrowBanzu = $("#borrowBanzu").val();
		var borrowNum = $("#borrowNum").val();
		if(sqBanzu==null || sqBanzu==""){
			alert("请选择申请班组");
			return false;
		}
		if(borrowBanzu==null || borrowBanzu==""){
			alert("请选择借出班组");
			return false;
		}
		if(borrowBanzu==sqBanzu){
			alert("借入和借出班组不能相同");
			return false;
		}
		if(borrowNum==null || borrowNum==""){
			alert("请选择借入班组人数");
			return false;
		}else{
			borrowNum = borrowNum.replace(/^\s+|\s+$/g,"");//去除空格
			$("#borrowNum").val(borrowNum);
		}
		
		//借工时数去空格
		var gzHour = $("#gzHour").val();
		if(gzHour!=null){
			gzHour = gzHour.replace(/^\s+|\s+$/g,"");//去除空格
			$("#gzHour").val(gzHour);
		}
		
		for(var i=1;i<=tab_option;i++){
			var borrowDateObj = $("#borrowDate_"+i);
			var borrowDate = borrowDateObj.val();
			if(borrowDate!=null){
				if(borrowDate=="" || borrowDate.length!=10){
					$("#showMessageDiv_"+i).text("日期格式不正确！");
					return false;
				}else{
					
					//循环查看是否有日期重复
					for(var j=1;j<=tab_option;j++){
						var borrowDate2 = $("#borrowDate_"+j).val();
						if(borrowDate2!=null){
							if(borrowDate==borrowDate2 && i!=j){
								$("#showMessageDiv_"+i).text("日期重复！！");
								$("#showMessageDiv_"+j).text("日期重复！！");
								return false;
							}else{
								$("#showMessageDiv_"+i).text("");
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	
	//增加多个日期的方法
    function addzero(v) {
		if (v < 10){
			return '0' + v;
		}else{
			return v.toString();
		}
	}
	$(function(){
		addAList();
		
		//设置默认时间
		var d = new Date();
	 	var day = d.getDate();        //获取当前日(1-31)
	 	if((day-10)<0){
	 		day="0"+day;
	 	}
	    var s = d.getFullYear().toString() + '-'+addzero(d.getMonth() + 1)+'-'+day;
	    $("#borrowDate_1").val(s);
	});
	var tab_option=0;
	function addAList(){
		var str = $("#borrowDateText").val();
		tab_option++;
		
		while (str.indexOf('_0') >= 0){
	       str = str.replace('_0', '_'+tab_option);
	       str = str.replace('[0]', '['+tab_option+']');
	    }
		var lastBorrowDate = $("#borrowDate_"+(tab_option-1));
		var currentDate="";
		if(lastBorrowDate!=null && lastBorrowDate.val()!=null && lastBorrowDate.val()!=""){
			var oldDate = new Date(lastBorrowDate.val());
			var date=new Date(oldDate.setDate(oldDate.getDate()+1));
			
			currentDate = date.getFullYear().toString() + '-'+addzero(date.getMonth() + 1)+'-'+addzero(date.getDate());
		}
		$("#appendDate").append(str);
		$("#borrowDate_"+tab_option).val(currentDate);
	}
	
	function delAList(opt){
		$("#appendRow"+opt).remove();
	}
	</script>
</html>