function iFrameHeight(frmId) 
{
		var ifm= document.getElementById(frmId);
		var subWeb = document.frames ? document.frames[frmId].document : ifm.contentDocument;
		if(ifm != null && subWeb != null) 
		{
			ifm.height = subWeb.body.scrollHeight;
		}
}

/**
 * 日期转换 调用： var time1 = new Date().Format("yyyy-MM-dd"); var time2 = new
 * Date().Format("yyyy-MM-dd HH:mm:ss"); var time1 = new
 * Date(日期对象).Format("yyyy-MM-dd");
 */
Date.prototype.Format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	}

	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
		}

	}

	return fmt;
};

jQuery.extend({
	/**
	 * @see 将javascript数据类型转换为json字符串
	 * @param 待转换对象,支持object,array,string,function,number,boolean,regexp
	 * @return 返回json字符串
	 */
	toJSON : function(object) {
		var type = $.type(object);
		switch (type) {
		case 'undefined':
		case 'unknown':
			return;
			break;
		case 'function':
		case 'boolean':
		case 'regexp':
			return object.toString();
			break;
		case 'number':
			return isFinite(object) ? object.toString() : 'null';
			break;
		case 'string':
			return '"'
					+ object.replace(/(\\|\")/g, "\\$1").replace(
							/\n|\r|\t/g,
							function() {
								var a = arguments[0];
								return (a == '\n') ? '\\n'
										: (a == '\r') ? '\\r'
												: (a == '\t') ? '\\t' : "";
							}) + '"';
			break;
		case 'object':
			if (object === null)
				return 'null';
			var results = [];
			for ( var property in object) {
				var value = jQuery.toJSON(object[property]);
				if (value !== undefined)
					results.push(jQuery.toJSON(property) + ':' + value);
			}
			return '{' + results.join(',') + '}';
			break;
		case 'array':
			var results = [];
			for ( var i = 0; i < object.length; i++) {
				var value = jQuery.toJSON(object[i]);
				if (value !== undefined)
					results.push(value);
			}
			return '[' + results.join(',') + ']';
			break;
		}
	}
});
var Tip={
	error:function(errorCode,complete)
	{
		window.top.$("#italkTip").html(errorCode).slideDown(500).delay(2000).slideUp(1000,complete);
	},
	info:function(infoCode,complete)
	{
		window.top.$("#italkTip").html(infoCode).slideDown(500).delay(2000).slideUp(1000,complete);
	}
}

$(function(){

			$(document).ajaxSend(function(event, jqXHR, ajaxOptions){
				var mask=ajaxOptions["_mask"];
				
				if(mask)
				{
					$(mask).block({"message":"正在处理中..."});
				}
				
			});
			$(document).ajaxComplete(function(event, XMLHttpRequest, ajaxOptions){
				var mask=ajaxOptions["_mask"];
				if(mask)
				{
					$(mask).unblock();
				}
			});
			
			$.ajaxPrefilter(function(options, originalOptions, jqXHR){
				
				if(options['success']&&(options.dataType||"").toLowerCase()=="json")
				{
					var fnSuc=options['success'];
					options['success']=function(data)
					{
						if(data['_code'])
						{
							if(data["_code"]=="0101008")
							{
								Tip.error(data['_message'],function(){
									window.top.location.href=_basepath+"/index.html";
								});
								return;
							}
							
							if(data['_message'])
							{
								Tip.error(data['_message']);
							}
							return;
						}
						
						fnSuc.call(this,data);
					}
				}
			});
});