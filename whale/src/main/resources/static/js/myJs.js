$(function(){
	$("#saveUser").on("click",function () {
		layer.open({
			type : 2,
			title :['注册用户','font-size:18px;font-weight:bold;'],
			 area : [ '40%', '90%' ],
			shade : 0.3,
			maxmin : false,//最大化和最小化按钮
			resize : false,//拉伸
			scrollbar : false,//滚动条
			moveOut : false,//拖动到屏幕外面
			fixed : false, //不固定
			content : '/user/toSaveUser',
			end : function() {
				location.reload();
			},
			btn : [ '提交', '取消' ], //按钮上传按钮怎么提交的是 模态框的？
			yes : function(index, layero) {
				var iframes = $(layero).find("iframe")[0].contentWindow;//找页面html
				var formdata = new FormData(iframes.document
						.getElementById("file"));//序列化表单
				console.log(formdata);
				//iframeWin.contentWindow.uploadFiles();//doSubmit 是你在弹出层的那个jsp里写的表单提交方法
				if ("formdata" != typeof (formdata) && formdata != null
						&& formdata != "") {
					$.ajax({
						url : '/user/saveUser',
						type : 'POST',
						data : formdata,
						contentType : false, //不设置内容类型
						processData : false, //不处理数据
						success : function(data) {
							console.log(data);
							if(data==200){
								layer.msg('操作成功!',{time:1000},function(){
									layer.close(index);
								});
							}else{
								layer.msg(data, {icon: 2, time:2000,anim:6});
							}
						},
						error : function() {
							layer.alert('图片上传失败!', {icon: 2});
						}
					});
				} else {
					layer.alert("选择的文件无效！请重新选择",{icon:1});
				}
			}
		});
	})
	/*登陆弹框*/
	$("#login").on("click",function () {
		layer.open({
			type : 2,
			title :['Whale Login','font-size:18px;font-weight:bold;'],
			area : [ '50%', '50%' ],
			shade : 0.3,
			maxmin : false,//最大化和最小化按钮
			resize : false,//拉伸
			scrollbar : false,//滚动条
			moveOut : false,//拖动到屏幕外面
			fixed : true, //不固定
			content : '/login',
			end : function() {
				location.reload();
			},
			btn : [ 'Login','Sign Up'], //按钮上传按钮怎么提交的是 模态框的？
			yes : function(index, layero) {
				layer.getChildFrame('body', index).find('#loginFrom').submit();
				layer.msg('登陆成功!');
				layer.close(index);
				
			}
		});
	})
	
	
	
	
	function cz(ko) {
		$.ajax({
			url : '/user/toUserUpdate',
			type : 'POST',
			data : "id=" + ko,
			dataType : "json",
			success : function(data) {
				console.log(data);
				$("#id").val(data.id);
				$("#userName").val(data.userName);
				$("#userPassword").val(data.userPassword);
				$("#email").val(data.email);
			},
			error : function() {
				alert("获取失败");
			}
		})
	}
});