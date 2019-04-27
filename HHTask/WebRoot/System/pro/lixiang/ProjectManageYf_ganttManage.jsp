<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE"/>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>${projectManageyf.proName}进度图</title>

  <link rel=stylesheet href="${pageContext.request.contextPath}/javascript/gantt/platform.css" type="text/css">
  <link rel=stylesheet href="${pageContext.request.contextPath}/javascript/gantt/libs/dateField/jquery.dateField.css" type="text/css">

  <link rel=stylesheet href="${pageContext.request.contextPath}/javascript/gantt/gantt.css" type="text/css">
<!--  <link rel=stylesheet href="ganttPrint.css" type="text/css" media="print"> -->

 <script src="${pageContext.request.contextPath}/javascript/calendar/jquery/jquery-1.8.3.js"></script>
<script src="${pageContext.request.contextPath}/javascript/fullcalendar-3.7.0/lib/jquery-ui.min.js"></script>

  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.livequery.min.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.timers.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/platform.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/date.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/i18nJs.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/dateField/jquery.dateField.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/libs/JST/jquery.JST.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/DatePicker/WdatePicker.js" type="text/javascript"></script>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/javascript/DatePicker/skin/WdatePicker.css">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.svg.css">
  <script type="text/javascript" src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.svg.min.js"></script>

  <!--In case of jquery 1.8-->
  <script type="text/javascript" src="${pageContext.request.contextPath}/javascript/gantt/libs/jquery.svgdom.1.8.js"></script>


  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttUtilities.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttTask.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttDrawerSVG.js"></script>
  <!--<script src="ganttDrawer.js"></script>-->
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttGridEditor.js"></script>
  <script src="${pageContext.request.contextPath}/javascript/gantt/ganttMaster.js"></script>  
  
  <link rel="stylesheet" type="text/css"
		href="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/css/bootstrap.min.css">
<script src="${pageContext.request.contextPath}/javascript/jquery/jquery-3.2.1.js"></script>
 <script type="text/javascript"
 		src="${pageContext.request.contextPath}/javascript/bootstrap-3.3.7-dist/js/bootstrap.js"></script>
<%--  <script src="${pageContext.request.contextPath}/js/myJquery.js"></script> --%>
<script type="text/javascript">
// 现在window.$和window.jQuery是3.2.1版本:
console.log($().jquery); // => '3.2.1'
var $jq = jQuery.noConflict(true);
// 现在window.$和window.jQuery被恢复成1.8.3版本:
console.log($().jquery); // => 'jquery-1.8.3.js'

</script>
</head>
<body style="background-color: #fff;">
<div class="modal fade" id="showDetail" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title" id="myModalLabel">
					项目其他操作
				</h4>
			</div>
			<div class="modal-body">
				<ul id="myTab" class="nav nav-tabs">
					<li class="active">
						<a href="#projectDetail" data-toggle="tab">
							项目明细
						</a>
					</li>
					<li><a href="#projectMember" data-toggle="tab">项目成员</a></li>
					<li><a href="#assignment" data-toggle="tab">指派人员</a></li>
					<li><a href="#examineAssignment" data-toggle="tab">审批项目人员</a></li>
					<li><a href="#fillSchedule" data-toggle="tab">填报进度</a></li>
				</ul>
				<div id="myTabContent" class="tab-content">
					<div class="tab-pane fade in active" id="projectDetail">
					</div>
					<div class="tab-pane fade" id="projectMember">
						<div id="projectMemberContent">
							
						</div>
						<div  class="text-center">
							<input type="button" value="取消指派" style="width: 100px;height: 40px" onclick="cancelZhipai()">
							<input type="reset" value="重置" style="width: 100px;height: 40px">
						</div>
					</div>
					<div class="tab-pane fade" id="assignment">
						<div id="assignmentContent">
							
						</div>
						<div class="text-center" align="center">
							<input type="button" value="指派" style="width: 100px;height: 40px" onclick="zhipai()">
							<input type="reset" value="重置" style="width: 100px;height: 40px">
						</div>
					</div>
					<div class="tab-pane fade" id="examineAssignment">
						<!-- 审批报选 -->
						<div id="examineAssignmentContent">
							
						</div>
						<div class="text-center" align="center">
							<input type="button" value="同意" style="width: 100px;height: 40px" onclick="examine('同意')">
							<input type="button" value="打回" style="width: 100px;height: 40px" onclick="examine('打回')">
						</div>
					</div>
					<div class="tab-pane fade" id="fillSchedule">
						<!-- 填报进度 -->
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭
				</button>
<!-- 				<button type="button" class="btn btn-primary"> -->
<!-- 					提交更改 -->
<!-- 				</button> -->
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>


<div id="workSpace" style="padding:0px; overflow-y:auto; overflow-x:hidden;border:1px solid #e5e5e5;position:relative;margin:0 5px"></div>

<div id="taZone" style="display:none;" class="noprint">
   <textarea rows="8" cols="150" id="ta">
     {"tasks":[
     {"id":-1,"name":"Gantt editor","code":"1","level":0,"status":"STATUS_ACTIVE","canWrite":true,"start":1396994400000,"duration":21,"end":1399672799999,"startIsMilestone":true,"endIsMilestone":false,"collapsed":false,"assigs":[],"hasChild":true}
     ,{"id":-2,"name":"coding","code":"1.1","level":1,"status":"STATUS_ACTIVE","canWrite":true,"start":1396994400000,"duration":10,"end":1398203999999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"description":"","progress":0,"hasChild":true}
     ,{"id":-3,"name":"gantt part","code":"","level":2,"status":"STATUS_ACTIVE","canWrite":true,"start":1396994400000,"duration":2,"end":1397167199999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"","hasChild":false}
     ,{"id":-4,"name":"editor part","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1397167200000,"duration":4,"end":1397685599999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"3","hasChild":false}
     ,{"id":-5,"name":"testing","code":"","level":1,"status":"STATUS_SUSPENDED","canWrite":true,"start":1398981600000,"duration":6,"end":1399672799999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"2:5","description":"","progress":0,"hasChild":true}
     ,{"id":-6,"name":"test on safari","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1398981600000,"duration":2,"end":1399327199999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"","hasChild":false}
     ,{"id":-7,"name":"test on ie","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1399327200000,"duration":3,"end":1399586399999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"6","hasChild":false}
     ,{"id":-8,"name":"test on chrome","code":"","level":2,"status":"STATUS_SUSPENDED","canWrite":true,"start":1399327200000,"duration":2,"end":1399499999999,"startIsMilestone":false,"endIsMilestone":false,"collapsed":false,"assigs":[],"depends":"6","hasChild":false}
     ],"selectedRow":0,"canWrite":true,"canWriteOnParent":true}
   </textarea>

  <button onclick="loadGanttFromServer();">load</button>
</div>
<style>
   
    
    </style>
<style>
  .resEdit {
    padding: 15px;
  }

  .resLine {
    width: 95%;
    padding: 3px;
    margin: 5px;
    border: 1px solid #d0d0d0;
  }

  body {
    overflow: hidden;
  }

  .ganttButtonBar h1{
    color: #000000;
    font-weight: bold;
    font-size: 28px;
    margin-left: 10px;
  }

</style>



<script type="text/javascript">

var ge;  //this is the hugly but very friendly global var for the gantt editor
$(function() {

  //load templates
  //$("#ganttemplates").loadTemplates();

  // here starts gantt initialization
  ge = new GanttMaster();
  var workSpace = $("#workSpace");
  workSpace.css({width:$(window).width() - 20,height:$(window).height() - 100});
  ge.init(workSpace);

  //inject some buttons (for this demo only)工具栏
  //$(".ganttButtonBar div").append("<button onclick='clearGantt();' class='button'>清空</button>")
  //        .append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
  //        .append("<button onclick='getFile();' class='button'>出口</button>");
  
  $(".ganttButtonBar div").addClass('buttons');
  //overwrite with localized ones
  loadI18n();

  //simulate a data load from a server.
  loadGanttFromServer();


  //fill default Teamwork roles if any
  if (!ge.roles || ge.roles.length == 0) {
    setRoles();
  }

  //fill default Resources roles if any
  if (!ge.resources || ge.resources.length == 0) {
    setResource();
  }


  /*/debug time scale
  $(".splitBox2").mousemove(function(e){
    var x=e.clientX-$(this).offset().left;
    var mill=Math.round(x/(ge.gantt.fx) + ge.gantt.startMillis)
    $("#ndo").html(x+" "+new Date(mill))
  });*/

  $(window).resize(function(){
    workSpace.css({width:$(window).width() - 1,height:$(window).height() - workSpace.position().top});
    workSpace.trigger("resize.gantt");
  }).oneTime(150,"resize",function(){$(this).trigger("resize")});

});


function loadGanttFromServer(taskId, callback) {

  loadFromLocalStorage();

  //this is the real implementation
  /*
  //var taskId = $("#taskSelector").val();
  var prof = new Profiler("loadServerSide");
  prof.reset();

  $.getJSON("ganttAjaxController.jsp", {CM:"LOADPROJECT",taskId:taskId}, function(response) {
    //console.debug(response);
    if (response.ok) {
      prof.stop();

      ge.loadProject(response.project);
      ge.checkpoint(); //empty the undo stack

      if (typeof(callback)=="function") {
        callback(response);
      }
    } else {
      jsonErrorHandling(response);
    }
  });
  */
}


function saveGanttOnServer() {
  if(!ge.canWrite)
    return;


  //this is a simulation: save data to the local storage or to the textarea
  saveInLocalStorage();


}


//-------------------------------------------  Create some demo data ------------------------------------------------------
function setRoles() {
  ge.roles = [
    {
      id:"tmp_1",
      name:"Project Manager"
    },
    {
      id:"tmp_2",
      name:"Worker"
    },
    {
      id:"tmp_3",
      name:"Stakeholder/Customer"
    }
  ];
}

function setResource() {
  var res = [];
  for (var i = 1; i <= 10; i++) {
    res.push({id:"tmp_" + i,name:"Resource " + i});
  }
  ge.resources = res;
}


function editResources(){

}

function clearGantt() {
  ge.reset();
}

function loadI18n() {
  GanttMaster.messages = {
    "CANNOT_WRITE":"不能编辑",
    "CHANGE_OUT_OF_SCOPE":"变化的范围",
    "START_IS_MILESTONE":"开始是里程碑",
    "END_IS_MILESTONE":"结束是里程碑",
    "TASK_HAS_CONSTRAINTS":"项目有时间限制",
    "GANTT_ERROR_DEPENDS_ON_OPEN_TASK":"甘特图打开错误",
    "GANTT_ERROR_DESCENDANT_OF_CLOSED_TASK":"甘特图关闭错误",
    "TASK_HAS_EXTERNAL_DEPS":"外部DEPS任务",
    "GANTT_ERROR_LOADING_DATA_TASK_REMOVED":"甘特图加载数据被删除",
    "ERROR_SETTING_DATES":"设置日期错误",
    "CIRCULAR_REFERENCE":"参考循环",
    "CANNOT_DEPENDS_ON_ANCESTORS":"CANNOT_DEPENDS_ON_ANCESTORS",
    "CANNOT_DEPENDS_ON_DESCENDANTS":"CANNOT_DEPENDS_ON_DESCENDANTS",
    "INVALID_DATE_FORMAT":"INVALID_DATE_FORMAT",
    "TASK_MOVE_INCONSISTENT_LEVEL":"TASK_MOVE_INCONSISTENT_LEVEL",

    "GANTT_QUARTER_SHORT":"trim.",
    "GANTT_SEMESTER_SHORT":"sem."
  };
}


//-------------------------------------------  LOCAL STORAGE MANAGEMENT (for this demo only) ------------------------------------------------------
Storage.prototype.setObject = function(key, value) {
  this.setItem(key, JSON.stringify(value));
};


Storage.prototype.getObject = function(key) {
  return this.getItem(key) && JSON.parse(this.getItem(key));
};

//加载数据
function loadFromLocalStorage() {
  var ret;
  
  if (!ret || !ret.tasks || ret.tasks.length == 0){
    //ret = JSON.parse($("#ta").val());
	$.ajax({
		url : 'projectPoolAction_findProjectManageyfByRootId.action',
		type : 'post',
		data : {
			rootIdStr : '${id}'
		},
		dataType : 'json',
		async : false,
		success : function(data) {
			var append = '';
			if(data!=null ){
				for(var i=0;i<data.length;i++){
					if(i!=0){
						append+=',';
					}
					var status = "";//项目状态
					var proStatus = data[i].status;
					var canWrite = true;
					if(proStatus=="完成"){
						status="STATUS_ACTIVE";
						canWrite = false;
					}else if(proStatus=="待完善" || proStatus=="待填报" ){
						status="STATUS_UNDEFINED";
						//STATUS_ACTIVE完成
						//STATUS_DONE执行中
						//STATUS_FAILED"重新执行
						//STATUS_SUSPENDED待执行
						//STATUS_UNDEFINED待填报
					}else if(proStatus=="执行中" || proStatus=="待确认"){
						status="STATUS_DONE";
					}else if(proStatus=="重新执行"){
						status="STATUS_FAILED";
					}else if(proStatus=="待执行"){
						status="STATUS_SUSPENDED";
					}else if(proStatus=="待填报"){
						status="STATUS_UNDEFINED";
					}else{
						status="";
						//STATUS_ACTIVE积极的
						//STATUS_DONE空
						//STATUS_FAILED失败的
						//STATUS_SUSPENDED暂停的
						//STATUS_UNDEFINED不明确的，未定义的
					}
					var starttime = data[i].startTime.replace(new RegExp("-","gm"),"/");
    				var start= computeStart((new Date(starttime)).getTime()); //得到毫秒数
    				var endtime = "";
					var end =null;
    				if(data[i].reTime!=null && data[i].reTime!=""){
    					endtime = data[i].reTime.replace(new RegExp("-","gm"),"/");
						end=computeEnd((new Date(endtime)).getTime()); //得到毫秒数
    				}
    				
					
					var duration=recomputeDuration(start,end)+1;//持续时间/工期
					var model = data[i].model;//关联模型
					if(model=="null" || model==null){
						model="";
					}
					var depends = data[i].depends;//前置任务
					var gradeStore = data[i].gradeStore;//分数
					if(gradeStore==null){
						gradeStore = "";
					}
					var startIsMilestone = data[i].startIsMilestone;
					if(startIsMilestone==null || startIsMilestone=="null" || startIsMilestone=="false"){
						startIsMilestone="";
					}
					var endIsMilestone = data[i].endIsMilestone;
					if(endIsMilestone==null || endIsMilestone=="null" || endIsMilestone=="false"){
						endIsMilestone="";
					}
					var description = data[i].remark;
					var inPeople = data[i].principals;//负责人
					var fatherId = data[i].fatherId;
					//"assigs":[],"hasChild":true,"canWrite":true,,"milestone":"'+milestone+'"
					append+='{"id":'+data[i].id+',"name":"'+data[i].proName+'","code":"'+data[i].proNum+'","canWrite":'+canWrite+',' +
					'"level":'+data[i].belongLayer+',"status":"'+status+'","start":'+start+
					',"duration":'+duration+',"startIsMilestone":"'+startIsMilestone+'","endIsMilestone":"'+endIsMilestone+'",' +
					'"collapsed":false,"depends":"'+depends+'","model":"'+model+'","title":"'+proStatus+'",' +
					'"gradeStore":"'+gradeStore+'","description":"'+description+'","inPeople":"'+inPeople+'","fatherId":"'+fatherId+'"}';
				}
			}
			var str = '{"tasks":['+append+'],"selectedRow":0,"canWrite":true,"canWriteOnParent":true}';
			try {
				ret = JSON.parse(str);
			} catch (e) {
				alert("数据解析异常\n"+str+"\n"+e.name + ": " + e.message);
			}
			
		},
		error : function() {
			alert("服务器异常!");
		}
	});
	//-----------------------------

  }
  ge.loadProject(ret);
  ge.checkpoint(); //empty the undo stack
}


function saveInLocalStorage() {
	if(!confirm("确定要保存提交吗？")){
		return ;
	}
 
  var prj = ge.saveProject();
  var status = "";
  var ids="";
  for(var i=0;i<prj.tasks.length;i++){
	   ge.beginTransaction();
	  //alert(prj.tasks[i].name);
	  var project = prj.tasks[i];
	  var id = project.id;
	  if(id.toString().indexOf('tmp') >= 0){
		  id=null;
	  }else{
		  id = parseInt(id);
	  }
	  ids+=id+",";
	  var code = project.code;
	  var name = project.name;
	  var startTime = new Date(project.start).toLocaleString();
	  var endTime = new Date(project.end).toLocaleString();
	  var duration = project.duration;//工期
	  
	  var depends = project.depends;//前置任务
	  if(depends=="" ){
		  depends=null;
	  }else{
		  depends = parseInt(depends);
	  }
	  var model = project.model;//关联模型
	  var inPeople = project.inPeople;
	  var gradeStore = project.gradeStore;
	  if(gradeStore=="null"){
		  gradeStore = null;
	  }
	  
	  var remark = project.description;//备注
	  var belongLayer = project.level;
	  var fatherId = project.fatherId;
	  if(fatherId=="null"){
		  fatherId = null;
	  }
	  var startIsMilestone = project.startIsMilestone;
	  var endIsMilestone =project.endIsMilestone;
	  $.ajax({
		  url:"projectPoolAction_saveOrUpdateYf.action",
		  type:"post",
		  data:{
			"projectManageyf.id":id,
			"projectManageyf.rootId":"${param.id}",
			"projectManageyf.proNum":code,
			"projectManageyf.proName":name,
			"projectManageyf.startTime":startTime,
			"projectManageyf.reTime":endTime,
			"projectManageyf.duration":duration,
			"projectManageyf.depends":depends,
			"projectManageyf.model":model,
			"projectManageyf.takeChangeMan":inPeople,
			"projectManageyf.gradeStore":gradeStore,
			"projectManageyf.remark":remark,
			"projectManageyf.belongLayer":belongLayer,
			"projectManageyf.fatherId":fatherId,
			"projectManageyf.poolId":"${param.poolId}",
			"projectManageyf.startIsMilestone":startIsMilestone,
			"projectManageyf.endIsMilestone":endIsMilestone
		  },
		  dataType:"json",
		  async : false,
		  success:function(obj){
			  if(obj!=null && obj.message!=null && obj.message!=""){
				  status = obj.message;
			  }
			  for(var j=0;j<prj.tasks.length;j++){
				  if(project.id == prj.tasks[j].fatherId&&project.level==prj.tasks[j].level-1){
					  prj.tasks[j].fatherId = obj.data
				  }
			  }
			  project.id = obj.data;
		  },error:function(){
			  status = "functionError";
		  }
	  });
	  ge.endTransaction();
	  if("functionError"==status){
		  alert("保存出错");
		  return;
	  }else if(status!="添加成功" && status!="修改成功"){
		  alert(status);
		  return;
	  }
   }
   if(status=="添加成功" || status=="修改成功"){
	  
	   
	   //修改和保存后执行删除操作。
	   var projectIds = ids.substring(0,ids.length-1);
	   $.ajax({
		   type:"post",
		   url:"projectPoolAction_delProjectByIds.action",
		   data:{
		   	"ids":projectIds
		   },
		   success:function(data){
			   if(data=="true" || data.indexOf("true")>0){
				    alert("保存成功");
				    location.reload(true); 
			   }else{
				   alert("删除项目失败");
			   }
		   },error:function(){
			   alert("系统异常");
		   }
	   });
   }else{
	   alert(status);
   }
	
}

//-------------------------------------------  Open a black popup for managing resources. This is only an axample of implementation (usually resources come from server) ------------------------------------------------------
Date.prototype.toLocaleString = function() {
	var year = this.getFullYear();
	var month = (this.getMonth() + 1);
	var days = this.getDate();
	if(month<10){
		month = "0"+month;
	}
	if(days<10){
		days="0"+days;
	}
      return  year+ "-" + month + "-" +days ;
};

function editResources(){

  //make resource editor
  var resourceEditor = $.JST.createFromTemplate({}, "RESOURCE_EDITOR");
  var resTbl=resourceEditor.find("#resourcesTable");

  for (var i=0;i<ge.resources.length;i++){
    var res=ge.resources[i];
    resTbl.append($.JST.createFromTemplate(res, "RESOURCE_ROW"))
  }


  //bind add resource
  resourceEditor.find("#addResource").click(function(){
    resTbl.append($.JST.createFromTemplate({id:"new",name:"resource"}, "RESOURCE_ROW"))
  });

  //bind save event
  resourceEditor.find("#resSaveButton").click(function(){
    var newRes=[];
    //find for deleted res
    for (var i=0;i<ge.resources.length;i++){
      var res=ge.resources[i];
      var row = resourceEditor.find("[resId="+res.id+"]");
      if (row.size()>0){
        //if still there save it
        var name = row.find("input[name]").val();
        if (name && name!="")
          res.name=name;
        newRes.push(res);
      } else {
        //remove assignments
        for (var j=0;j<ge.tasks.length;j++){
          var task=ge.tasks[j];
          var newAss=[];
          for (var k=0;k<task.assigs.length;k++){
            var ass=task.assigs[k];
            if (ass.resourceId!=res.id)
              newAss.push(ass);
          }
          task.assigs=newAss;
        }
      }
    }

    //loop on new rows
    resourceEditor.find("[resId=new]").each(function(){
      var row = $(this);
      var name = row.find("input[name]").val();
      if (name && name!="")
        newRes.push (new Resource("tmp_"+new Date().getTime(),name));
    });

    ge.resources=newRes;

    closeBlackPopup();
    ge.redraw();
  });


  //var ndo = createBlackPage(400, 500).append(resourceEditor);
}


</script>


<div id="gantEditorTemplates" style="display:none;">
  <div class="__template__" type="GANTBUTTONS"><!--
  <div class="ganttButtonBar noprint">
    
    <div class="buttons">
    <button onclick="$('#workSpace').trigger('undo.gantt');" class="button textual" title="撤销"><span class="teamworkIcon">&#39;</span></button>
    <button onclick="$('#workSpace').trigger('redo.gantt');" class="button textual" title="恢复"><span class="teamworkIcon">&middot;</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('addAboveCurrentTask.gantt');" class="button textual" title="上方插入"><span class="teamworkIcon">l</span></button>
    <button onclick="$('#workSpace').trigger('addBelowCurrentTask.gantt');" class="button textual" title="下方插入"><span class="teamworkIcon">X</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('indentCurrentTask.gantt');" class="button textual" title="降级"><span class="teamworkIcon">.</span></button>
    <button onclick="$('#workSpace').trigger('outdentCurrentTask.gantt');" class="button textual" title="升级"><span class="teamworkIcon">:</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('moveUpCurrentTask.gantt');" class="button textual" title="上移"><span class="teamworkIcon">k</span></button>
    <button onclick="$('#workSpace').trigger('moveDownCurrentTask.gantt');" class="button textual" title="下移"><span class="teamworkIcon">j</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('zoomMinus.gantt');" class="button textual" title="缩小"><span class="teamworkIcon">)</span></button>
    <button onclick="$('#workSpace').trigger('zoomPlus.gantt');" class="button textual" title="放大"><span class="teamworkIcon">(</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="$('#workSpace').trigger('deleteCurrentTask.gantt');" class="button textual" title="删除"><span class="teamworkIcon">&cent;</span></button>
    <span class="ganttButtonSeparator"></span>
    <button onclick="print();" class="button textual" title="打印"><span class="teamworkIcon">p</span></button>
    <span class="ganttButtonSeparator"></span>
<%--    <button onclick="print();" class="button textual" title="导出"><span class="teamworkIcon">f</span></button>--%>
<%--    <span class="ganttButtonSeparator"></span>--%>
<%--    <button onclick="ge.gantt.showCriticalPath=!ge.gantt.showCriticalPath; ge.redraw();" class="button textual" title="关键路径"><span class="teamworkIcon">&pound;</span></button>--%>
<%--    <span class="ganttButtonSeparator"></span>--%>
<%--    <button onclick="editResources();" class="button textual" title="编辑资源"><span class="teamworkIcon">M</span></button>--%>
<%--      &nbsp; &nbsp; &nbsp; &nbsp;--%>
      <button onclick="saveGanttOnServer();" class="button first big" title="保存">保存</button>
    </div></div>
  --></div><p align="right">说明：</p>

  <div class="__template__" type="TASKSEDITHEAD">
  <!--
  <table class="gdfTable" cellspacing="0" cellpadding="0">
    <thead>
    <tr style="height:40px">
      <th class="gdfColHeader" style="width:35px;"></th>
      <th class="gdfColHeader" style="width:25px;">状态</th>
      <th class="gdfColHeader gdfResizable" style="width:2px;">任务编码</th>
      <th class="gdfColHeader gdfResizable" style="width:200px;">任务名称</th>
      <th class="gdfColHeader gdfResizable" style="width:80px;">开始时间</th>
      <th class="gdfColHeader gdfResizable" style="width:80px;">预完成时间</th>
      <th class="gdfColHeader gdfResizable" style="width:50px;">工期</th>
      <th class="gdfColHeader gdfResizable" style="width:60px;">前置任务</th>
	  <th class="gdfColHeader gdfResizable" style="width:30px;">开始是里程碑</th>
	  <th class="gdfColHeader gdfResizable" style="width:30px;">结束是里程碑</th>
	  <th class="gdfColHeader gdfResizable" style="width:100px;">关联模型</th>
	  <th class="gdfColHeader gdfResizable" style="width:100px;">负责人</th>
	  <th class="gdfColHeader gdfResizable" style="width:50px;">分数</th>
    </tr>
    </thead>
  </table>
  --></div>

  <div class="__template__" type="TASKROW"><!--
  <tr taskId="(#=obj.id#)" class="taskEditRow" level="(#=level#)">
    <th class="gdfCell text-center" style="cursor:pointer;" data-toggle="modal" onclick="getProId(this)">
    	<span class="taskRowIndex" >(#=obj.getRow()+1#)</span> </th>
    <td class="gdfCell noClip" align="center"><div class="taskStatus cvcColorSquare" status="(#=obj.status#)" title="(#=obj.title#)"></div></td>
    <td class="gdfCell"><input type="text" name="code" placeholder="不填自动生成"></td>
    <td class="gdfCell indentCell" >
      <div class="(#=obj.isParent()?'exp-controller expcoll exp':'exp-controller'#)" align="center"></div>
      <input type="text" name="name" value="(#=obj.name#)">
    </td>
<%--onClick="WdatePicker({dateFmt:'yyyy-MM-dd',skin:'whyGreen'})"--%>
    <td class="gdfCell"><input type="text" name="start" value="(#=obj.start#)"  class="date"></td>
    <td class="gdfCell"><input type="text" name="end" value="(#=obj.end#)"  class="date"></td>
    <td class="gdfCell"><input type="text" name="duration" value="(#=obj.duration#)" style="text-align:right" ></td>
    <td class="gdfCell"><input type="text" name="depends" value="(#=obj.depends#)" (#=obj.hasExternalDep?"readonly":""#)></td>
    <td class="gdfCell"><input type="text" name="startIsMilestone" value="(#=obj.startIsMilestone#)"></td>
    <td class="gdfCell"><input type="text" name="endIsMilestone" value="(#=obj.endIsMilestone#)"></td>
	<td class="gdfCell"><input type="text" name="model" value="(#=obj.model#)"></td>
	<td class="gdfCell"><input type="text" name="inPeople" value="(#=obj.inPeople#)" readonly="readonly"></td>
	<td class="gdfCell"><input type="text" name="gradeStore" value="(#=obj.gradeStore#)" style="text-align:right"></td>
  </tr>
  --></div>

  <div class="__template__" type="TASKEMPTYROW"><!--
  <tr class="taskEditRow emptyRow" >
    <th class="gdfCell" align="right"></th>
    <td class="gdfCell noClip" align="center"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
    <td class="gdfCell"></td>
	<td class="gdfCell"></td>
	<td class="gdfCell"></td>
	<td class="gdfCell"></td>
  </tr>
  --></div>

  <div class="__template__" type="TASKBAR"><!--
  <div class="taskBox taskBoxDiv" taskId="(#=obj.id#)" >
    <div class="layout (#=obj.hasExternalDep?'extDep':''#)">
      <div class="taskStatus" status="(#=obj.status#)"></div>
      <div class="taskProgress" style="width:(#=obj.progress>100?100:obj.progress#)%; background-color:(#=obj.progress>100?'red':'rgb(153,255,51);'#);"></div>
      <div class="milestone (#=obj.startIsMilestone?'active':''#)" ></div>

      <div class="taskLabel"></div>
      <div class="milestone end (#=obj.endIsMilestone?'active':''#)" ></div>
    </div>
  </div>
  --></div>

  <div class="__template__" type="CHANGE_STATUS"><!--
    <div class="taskStatusBox">
      <div class="taskStatus cvcColorSquare" status="STATUS_ACTIVE" title="完成"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_DONE" title="执行中"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_FAILED" title="重新执行"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_SUSPENDED" title="待执行"></div>
      <div class="taskStatus cvcColorSquare" status="STATUS_UNDEFINED" title="待填报"></div>
    </div>
  --></div>


  <div class="__template__" type="TASK_EDITOR"><!--
  <div class="ganttTaskEditor">
  <table width="100%">
    <tr>
      <td>
        <table cellpadding="5">
          <tr>
            <td><label for="code">任务编码</label><br><input type="text" name="code" id="code" value="" class="formElements"></td>
          </tr>
          <tr>
            <td><label for="name">任务名称</label><br><input type="text" name="name" id="name" value=""  size="35" class="formElements"></td>
          </tr>
          <tr>
          </tr>
            <td>
              <label for="description">description</label><br>
              <textarea rows="5" cols="30" id="description" name="description" class="formElements"></textarea>
            </td>
          </tr>
        </table>
      </td>
      <td valign="top">
        <table cellpadding="5">
          <tr>
          <td colspan="2"><label for="status">status</label><br><div id="status" class="taskStatus" status=""></div></td>
          <tr>
          <td colspan="2"><label for="progress">progress</label><br><input type="text" name="progress" id="progress" value="" size="3" class="formElements"></td>
          </tr>
          <tr>
          <td><label for="start">start</label><br><input type="text" name="start" id="start"  value="" class="date" size="10" class="formElements"><input type="checkbox" id="startIsMilestone"> </td>
          <td rowspan="2" class="graph" style="padding-left:50px"><label for="duration">dur.</label><br><input type="text" name="duration" id="duration" value=""  size="5" class="formElements"></td>
        </tr><tr>
          <td><label for="end">end</label><br><input type="text" name="end" id="end" value="" class="date"  size="10" class="formElements"><input type="checkbox" id="endIsMilestone"></td>
        </table>
      </td>
    </tr>
    </table>

  <h2>assignments</h2>
  <table  cellspacing="1" cellpadding="0" width="100%" id="assigsTable">
    <tr>
      <th style="width:100px;">name</th>
      <th style="width:70px;">role</th>
      <th style="width:30px;">est.wklg.</th>
      <th style="width:30px;" id="addAssig"><span class="teamworkIcon" style="cursor: pointer">+</span></th>
    </tr>
  </table>

  <div style="text-align: right; padding-top: 20px"><button id="saveButton" class="button big">save</button></div>
  </div>
  --></div>


  <div class="__template__" type="ASSIGNMENT_ROW"><!--
  <tr taskId="(#=obj.task.id#)" assigId="(#=obj.assig.id#)" class="assigEditRow" >
    <td ><select name="resourceId"  class="formElements" (#=obj.assig.id.indexOf("tmp_")==0?"":"disabled"#) ></select></td>
    <td ><select type="select" name="roleId"  class="formElements"></select></td>
    <td ><input type="text" name="effort" value="(#=getMillisInHoursMinutes(obj.assig.effort)#)" size="5" class="formElements"></td>
    <td align="center"><span class="teamworkIcon delAssig" style="cursor: pointer">d</span></td>
  </tr>
  --></div>


  <div class="__template__" type="RESOURCE_EDITOR"><!--
  <div class="resourceEditor" style="padding: 5px;">

    <h2>Project team</h2>
    <table  cellspacing="1" cellpadding="0" width="100%" id="resourcesTable">
      <tr>
        <th style="width:100px;">name</th>
        <th style="width:30px;" id="addResource"><span class="teamworkIcon" style="cursor: pointer">+</span></th>
      </tr>
    </table>

    <div style="text-align: right; padding-top: 20px"><button id="resSaveButton" class="button big">save</button></div>
  </div>
  --></div>


  <div class="__template__" type="RESOURCE_ROW"><!--
  <tr resId="(#=obj.id#)" class="resRow" >
    <td ><input type="text" name="name" value="(#=obj.name#)" style="width:100%;" class="formElements"></td>
    <td align="center"><span class="teamworkIcon delRes" style="cursor: pointer">d</span></td>
  </tr>
  --></div>


</div>
<script type="text/javascript">
$.JST.loadDecorator("ASSIGNMENT_ROW", function(assigTr, taskAssig) {

    var resEl = assigTr.find("[name=resourceId]");
    for (var i in taskAssig.task.master.resources) {
      var res = taskAssig.task.master.resources[i];
      var opt = $("<option>");
      opt.val(res.id).html(res.name);
      if (taskAssig.assig.resourceId == res.id)
        opt.attr("selected", "true");
      resEl.append(opt);
    }


    var roleEl = assigTr.find("[name=roleId]");
    for (var i in taskAssig.task.master.roles) {
      var role = taskAssig.task.master.roles[i];
      var optr = $("<option>");
      optr.val(role.id).html(role.name);
      if (taskAssig.assig.roleId == role.id)
        optr.attr("selected", "true");
      roleEl.append(optr);
    }

    if(taskAssig.task.master.canWrite && taskAssig.task.canWrite){
      assigTr.find(".delAssig").click(function() {
        var tr = $(this).closest("[assigId]").fadeOut(200, function() {
          $(this).remove();
        });
      });
    }
});
  
  
  /**
  	* 下面都是模态框的操作
  */
var currentProId;//弹出框后的项目Id
function getProId(obj){
	var currentIndex = $jq(obj).find("span").text();
	currentProId = ge.tasks[currentIndex-1].id;
	$jq("#showDetail").modal('show');
	
	showTabs('projectDetail','projectPoolAction_toAddSubProject.action?pageStatus=detail&id='+currentProId);
//     e.preventDefault();
}
function showTabs(tabsId,url) {
    $jq("a[href='#"+tabsId+"']").tab('show');
    var $tabContent = $jq('#'+tabsId);
    if($tabContent.length < 100) {
    	if(url==null || url==""){
    		$tabContent.html("请稍后，正在加载中......");
    	}
    	$tabContent.load(url);
        //console.info(tabsId + ' load done!');
    }
}



//项目明细
$('a[href="#projectDetail"]').click(function(e) {
    showTabs('projectDetail','projectPoolAction_toAddSubProject.action?pageStatus=detail&id='+currentProId);
    e.preventDefault();
});

//项目成员
$('a[href="#projectMember"]').click(function(e) {
    showTabs('projectMemberContent','projectPoolAction_findBindPlayers.action?pageStatus=get&id='+currentProId);
    e.preventDefault();
});

//指派人员
$('a[href="#assignment"]').click(function(e) {
    showTabs('assignmentContent','projectPoolAction_findUnErList.action?pageStatus=get&id='+currentProId);
    e.preventDefault();
});
//审批报选人员
$('a[href="#examineAssignment"]').click(function(e) {
    showTabs('examineAssignmentContent','projectPoolAction_findUnErList.action?pageStatus=shenpi&id='+currentProId);
    e.preventDefault();
});
  //填报进度
$('a[href="#fillSchedule"]').click(function(e) {
    showTabs('fillSchedule','projectPoolAction_gotoFillSchedule.action?pageStatus=applychoose&id='+currentProId);
    e.preventDefault();
});

  
//取消指派
function cancelZhipai(){
	var checkout ="";
	$("input[name='canyuren']:checked").each(function(){ 
		var projectyfUserId = $(this).val();
		var obj = this;
		$.ajax({
			type:"post",
			url:"projectPoolAction_cancelBindPlayers.action",
			dataType:'json',
		    cache:false, 
      		async:false, 
			data:{
				"yfuserMiddle.yfUserId":projectyfUserId,
				"yfuserMiddle.projectManagerYfId":currentProId
			},
			success:function(data){
				checkout = data;
				if(data=='取消成功'){
					$(obj).parent().remove();
				}
			},
			error:function(){
				alert("取消异常");
				return;
			}
		});
	}); 
	if(checkout!=null && checkout!=""){
		alert(checkout);
	}
}

//直接指派项目成员
function zhipai(){
	var checkout ="";
	$("input[name='uner']:checked").each(function(){ 
		var userId = $(this).val();
		var weight = $(this).next().val();
		
		$.ajax({
			type:"post",
			url:"projectPoolAction_zhipaiPlayers.action",
			dataType:'json',
		    cache:false, 
      		async:false, 
			data:{
				"userId":userId,
				"id":currentProId,
				"weight":weight
			},
			
			success:function(data){
				checkout = data;
			},
			error:function(){
				checkout = "不好意思，出错了哦，刷新重试一下吧";
			}
		});
	}); 
	if(checkout!=null && checkout!=""){
		alert(checkout);
	}
}

//审批报选项目人员
function examine(param){
	var checkout ="";
	$("input[name='uner']:checked").each(function(){ 
		debugger;
		var userId = $(this).val();
		var weight = $(this).next().val();
		
		$.ajax({
			type:"post",
			url:"projectPoolAction_examineProject.action",
			dataType:'json',
		    cache:false, 
      		async:false, 
			data:{
				"er.addUserId":userId,
				"er.status":param,
				"er.projectId":currentProId,
				"weight":weight,
				"id":"${param.id}"
			},
			success:function(data){
				checkout = data;
			},
			error:function(){
				alert("不好意思，出错了哦，刷新重试一下吧");
				return;
			}
		});
	}); 
	if(checkout!=null && checkout!=""){
		alert(checkout);
	}
}
</script>
</body>
</html>