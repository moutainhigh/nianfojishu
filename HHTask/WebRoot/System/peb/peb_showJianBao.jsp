<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
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
			href="<%=basePath%>/javascript/bootstrap-3.3.7-dist/css/bootstrap.css">
 		<script type="text/javascript"
			src="<%=basePath%>/js/echarts.js"></script>
		<style type="text/css">
			caption{
				text-align: center;
			}
			body{
				margin:5px;
/* 				padding: 5px; */
			}
			.row{
				margin:0;
			}
			.table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th,
			 .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td{
			 	padding: 4px;
			 	line-height: inherit;
			 }
			 caption:hover{
			 	cursor:default;
			 }
		</style>
	</head>
	<body style="background-color: #F5FFFA;">
		<div class="row" id="showTop">
			<div class="cell col-md-12">
				<form action="productEBAction!findPebUsersByCon.action" method="post" autocomplete=off>
					<table class="table table-responsive" id="showTable" style="border: solid 2px #000000;">
						<caption ondblclick="showOrHideInfo()">
							<font style="font-size: 30px;font-weight: bold;">
								<s:if test="banzu!=null&&banzu=='制造部'"></s:if>
								<s:else>${banzu}</s:else>生产效率简报</font>
						</caption>
						<s:iterator value="showList" id="sl" status="ps">
							<s:if test="#ps.index==0">
								<tr id="topHead">
									<s:iterator value="sl" id="detail" status="detailPs">
										<s:if test="#detailPs.index==0">
											
										</s:if>
										<s:else>
											<th class="text-center">${detail}</th>
										</s:else>
									</s:iterator>
								</tr>	
							</s:if>
							<s:else>
								
									<s:iterator value="sl" id="detail" status="detailPs">
										<s:if test="#detailPs.index<=2">
											<s:if test="#detailPs.index==0">
												<tr style="${detail}">
											</s:if>
											<s:else>
												<th class="text-center" ><nobr>${detail}</nobr></th>
											</s:else>
										</s:if>
										<s:elseif test="#sl.size()==#detailPs.index+1">
											<th class="text-left">${detail}</th>
										</s:elseif>
										<s:else>
											<th class="text-right">${detail}</th>
										</s:else>
									</s:iterator>
								</tr>	
							</s:else>
						</s:iterator>
						<tr>
							<th colspan="30">注：数据为日人均产量，单位：标准台/人/天。负值表示效率低于去年水平。</th>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<div id="myChart" ></div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		$(function(){
			$("th b").each(function(){
				$(this).parent().attr("style","background-color:rgb(200,55,55);text-align:center;font-size:17px;");//红色
			});
			$("th strong").each(function(){
				$(this).parent().attr("style","background-color: rgb(52,190,84);;text-align:center;font-size:17px;");//绿色
			});
			$("th span").each(function(){
				$(this).parent().attr("style","text-align:center;font-size:17px;");
			});
			
			var thLength = $("#showTable").children("tbody").children("tr");
			var i = 0;
			$("#topHead th").each(function(){
				var topName = $(this).text();
				if(topName.indexOf("年基数")>0 || topName=="班组人数" || topName=="全体人数" || topName=="占比" || topName=="月占比"){
					for(var tr = 0;tr<thLength.length;tr++){
						$("#showTable tbody tr:eq("+tr+") th:eq("+i+")").hide();
					}
				}
				
				i++;
			});
			
			
			$(".subBanzu").click(function(){
				  var banzu = $(this).text();
				  var json = {banzu:banzu};
				  post("productEBAction!showPebCheJian.action",json);
			});
		});
		
		/**
		 * 采用post方式跳转
		 * @param URL
		 * @param PARAMS json格式数据 {html :prnhtml,cm1:'测试',cm2:'haha'}
		 * @returns {Element}
		 */
		function post(URL, PARAMS) {
		    var temp = document.createElement("form");
		    temp.action = URL;
		    temp.method = "post";
		    temp.style.display = "none";
		    temp.target="_blank";
		    for (var x in PARAMS) {
		        var opt = document.createElement("textarea");
		        opt.name = x;
		        opt.value = PARAMS[x];
		        temp.appendChild(opt);
		    }
		    document.body.appendChild(temp);
		    temp.submit();
		    return temp;
		}
		
		var flag = false;
		function showOrHideInfo(){
			if(flag==false){
				flag = true;
				var thLength = $("#showTable").children("tbody").children("tr");
				var i = 0;
				$("#topHead th").each(function(){
					var topName = $(this).text();
					if(topName.indexOf("年基数")>0 || topName=="班组人数" || topName=="全体人数" || topName=="占比" || topName=="月占比"){
						for(var tr = 0;tr<thLength.length;tr++){
							$("#showTable tbody tr:eq("+tr+") th:eq("+i+")").show();
						}
					}
					
					i++;
				});
			}else{
				flag = false;
				var thLength = $("#showTable").children("tbody").children("tr");
				var i = 0;
				$("#topHead th").each(function(){
					var topName = $(this).text();
					if(topName.indexOf("年基数")>0 || topName=="班组人数" || topName=="全体人数" || topName=="占比" || topName=="月占比"){
						for(var tr = 0;tr<thLength.length;tr++){
							$("#showTable tbody tr:eq("+tr+") th:eq("+i+")").hide();
						}
					}
					
					i++;
				});
			}
			
		}
		
		
		
		
//折线图显示----------------------------+

		var captionValue= $("caption").text();//显示标题title
		
		//获得所有班组
    	var trLength = $(".table tr").length;
    	
    	
		//获取所有班组
    	function getFirstTdArray(){
    		var firstTdArray=[];
    		$(".subBanzu").each(function(){
    			var subBanzu = $(this).text()
	    		firstTdArray.push(subBanzu);
    		});
    		return firstTdArray;
    	}
		
    	
    	var bodyHeight = $("body").height();
		var topHeight=$("#showTop").height();
		var height = bodyHeight-topHeight;
		if(height<200){
			height = 200;
		}
		$("#myChart").height(height);
		// 基于准备好的dom，初始化echarts实例
		var myChart = echarts.init(document.getElementById('myChart'));
		// 指定图表的配置项和数据
		

		
	    var option = {
	   	    title: {
	   	        text: captionValue+"折线图",
	   	    },
	   	 	backgroundColor:'#ffffff',
	   	    tooltip: {
	   	        trigger: 'axis'
	   	    },
// 	   	    legend: {
// 	   	    	data:getFirstTdArray()
// 	   	    },
			legend:getFirstTdAndSelectArray(),
		    calculable : true,
	   	    grid: {
	   	        left: '2%',
	   	        right: '2%',
	   	        bottom: '2%',
	   	        containLabel: true
	   	    },
		   	  toolbox: {
		          show : true,
		          feature : {
		              mark : {show: true},
		              dataView : {show: true, readOnly: false},
		              magicType : {show: true, type: ['line','bar','stack']},
		              restore : {show: true},
		              saveAsImage : {show: true}
		          }
		      },
	   	    xAxis: [{
	   	        type: 'category',
	   	        boundaryGap: true,
	   	        data: (function(){
	   	        	var firstTdArray = getFirstTdArray();
			    	var dataAvgNumber=[];
			    	$.ajax({
			    		type:"post",
			    		url:"productEBAction!getAllMonth.action",
			    		data:{
							"banzu":firstTdArray[0]
						},
			    		dataType:"json",
	 		    		async : false,
			    		success:function(data){
			    			if(data!=null){
			    				for(var j=0;j<data.length;j++){
			    					var str = data[j];
// 			    					var str1 = str.replace("<br>","\n");
			    					dataAvgNumber.push(str);
			    				}
			    			}
			    		}
			    	});
			        return dataAvgNumber;
	   	        })(),
	   	     	axisLabel: {//x轴文字显示不全并将文字倾斜
					interval:0//,
					//rotate:40
				}
	
	   	    }],
	   	    yAxis: [
	   	    	{
		            splitLine : {//去掉网格线
						show : true
					},
					name : '月效率增长',
					type: 'value',  
		            axisLabel: {  
		                  show: true,  
		                  interval: 'auto',  
		                  formatter: '{value}%'  
		                },  
		            show: true  
		        }
	   	    ],
	   	    series: (function(){
	   	    	var firstTdArray = getFirstTdArray();
	   	    	var serie = [];
		    	for(var i=0;i<firstTdArray.length;i++){
			    	var dataAvgNumber=[];
			    	$.ajax({
			    		type:"post",
			    		url:"productEBAction!getMonthZengZhang.action",
			    		data:{
							"banzu":firstTdArray[i],
							"pageStatus":"ajaxPost"
						},
			    		dataType:"json",
	 		    		async : false,
			    		success:function(data){
			    			if(data!=null){
			    				for(var j=0;j<data.length;j++){
			    					dataAvgNumber.push(data[j]);
			    				}
			    			}
			    		}
			    	});
			    	var arrStr = {name:firstTdArray[i],type:"bar",data:dataAvgNumber};
		            serie.push(arrStr);
		    	}
		        return serie;
	   	    })()
	   	};

	    // 使用刚指定的配置项和数据显示图表
	    myChart.setOption(option);
	    
	    function getValues(object){
	    	 var contrTemp=[];
	    	 var tr = $(object);
	    	 
	         for(var i=2;i<tr[0].cells.length;i++){
	        	 var value = tr[0].cells[i].innerText;
	             contrTemp.push(value);
	         }
	         return contrTemp;
	    }
	    
	    
	 // 动态添加默认不显示的数据
// 	    var ecConfig = require('echarts/config');
// 	    myChart.on(ecConfig.EVENT.LEGEND_SELECTED, function (param){
// 	        var selected = param.selected;
// 	        var len;
// 	        var added;
// 	        if (selected['最低气温']) {
// 	            len = option.series.length;
// 	            added = false;
// 	            while (len--) {
// 	                if (option.series[len].name == '最低气温') {
// 	                    // 已经添加
// 	                    added = true;
// 	                    break;
// 	                }
// 	            }
// 	            if (!added) {
// 	                myChart.showLoading({
// 	                    text : '数据获取中',
// 	                    effect: 'whirling'
// 	                });
// 	                setTimeout(function (){
// 	                    option.series.push({
// 	                        name:'最低气温',
// 	                        type:'line',
// 	                        yAxisIndex: 1,
// 	                        data:[-2, 1, 2, 5, 3, 2, 0]
// 	                    });
// 	                    myChart.hideLoading();
// 	                    myChart.setOption(option);
// 	                }, 2000)
// 	            } 
// 	        }
// 	    });

	function getFirstTdAndSelectArray(){
		var items = [];
		var firstTdArray = getFirstTdArray();
		
		var selectTd=[];
// 		for(var i=0;i<firstTdArray.length;i++){
// 			var str = "var str='"+firstTdArray[i]+"'=false";
// 			var first = ;
// 			var object = {eval(str):false};
// 			object.eval(str)=false;
// 			selectTd.push(eval(str));
// 		}
		var result = {data:firstTdArray,selected:selectTd};
		return result;
	}
	
	</script>	
</html>
