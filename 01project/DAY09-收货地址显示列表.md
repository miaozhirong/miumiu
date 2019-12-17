### 31. 收货地址-显示列表-持久层

**1. 规划SQL语句**

需要查询当前登录的用户的所有收货地址数据

	SELECT 
		tag, name, district, address, phone, is_default, aid
	FROM 
		t_address
	WHERE 
		uid=? 
	ORDER BY 
		is_default DESC, modified_time DESC

**2. 接口与抽象方法**

在`AddressMapper.java`中添加抽象方法：

	List<Address> findByUid(Integer uid);

**3. 配置XML映射**

	<select id="findByUid" resultType="cn.tedu.store.entity.Address">
 		SELECT 
 			tag,name,
 			district,address,
 			phone,is_default AS isDefault,
 			aid 
 		FROM 
 			t_address 
 		WHERE 
 			uid=#{uid}
 		ORDER BY
 			is_default DESC,modified_time DESC	
 	</select>

### 32. 收货地址-显示列表-业务层

**1. 规划异常**

无

**2. 接口与抽象方法**

在`IAddressService`中粘贴持久层的查询数据的方法及其注释，并将方法名中的`find`改为`get`：

	List<Address> getByUid(Integer uid);

**3. 实现抽象方法**

在`AddressServiceImpl`中粘贴持久层的查询数据的方法及其注释，使用私有权限进行修饰，并调用持久层方法实现。

在`AddressServiceImpl`中重写`IAddressService`中新添加的抽象方法，调用以上私有方法实现。

### 33. 收货地址-显示列表-控制器层

**1. 处理异常**

无

**2. 设计请求**

	请求路径：/addresses/
	请求参数：HttpSession session
	请求类型：GET
	响应数据：ResponseResult<List<Address>>

**3. 处理请求**

在`AddressController`中添加处理请求的方法：

	@GetMapping("/")
	public ResponseResult<List<Address>> getByUid(
		HttpSession session) {
		// 获取uid
		Integer uid=getUidFromSession(session);
	    // 调用业务层对象执行查询，并获取结果
		List<Address> result=addressService.getByUid(uid);
	    // 返回成功+结果
		return new ResponseResult<>(SUCCESS,result);
	}

完成后，打开浏览器，先登录，通过`http://localhost:8080/addresses/`执行测试，正常的结果可以看到当前登录的用户的收货地址数据。

### 34. 收货地址-显示列表-前端界面

		$(document).ready(function() {
				showAddressList();
				
			});
			function showAddressList(){
				$("#address-list").empty();
				$.ajax({
					"url" : "/addresses/",
					"type" : "GET",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							var list=json.data;
							for(var i=0;i<list.length;i++){
								console.log(list[i].name);
								var html='<tr>'
								+'<td>#{tag}</td>'
								+'<td>#{name}</td>'
								+'<td>#{address}</td>'
								+'<td>#{phone}</td>'
								+'<td><a href="addAddress.html" class="btn btn-xs btn-info"><span class="fa fa-edit"></span> 修改</a></td>'
								+'<td><a onclick="deleteByAid(#{aid})" class="btn btn-xs add-del btn-info"><span class="fa fa-trash-o"></span> 删除</a></td>'
								+'<td><a onclick="setDefault(#{aid})" class="btn btn-xs add-def btn-default">设为默认</a></td>'
								+'</tr>';
								html=html.replace(/#{aid}/g,list[i].aid);
								html=html.replace(/#{tag}/g,list[i].tag);
								html=html.replace(/#{name}/g,list[i].name);
								html=html.replace(/#{address}/g,list[i].district+list[i].address);
								html=html.replace(/#{phone}/g,list[i].phone);
								
								$("#address-list").append(html);
							}
							$(".add-def:eq(0)").hide();
						} else {
							alert(json.message);
						}
					},
					"error":function(){
						alert("您的登录信息已过期，请重新登录！");
						location.href="/web/login.html";
					}
				});
			}
			function deleteByAid(aid){
				$.ajax({
					"url" : "/addresses/"+aid+"/delete",
					"type" : "post",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							showAddressList();
						} else {
							alert(json.message);
						}
					},
					"error":function(){
						alert("您的登录信息已过期，请重新登录！");
						location.href="/web/login.html";
					}
				});
			}
			function setDefault(aid){
				$.ajax({
					"url" : "/addresses/"+aid+"/set_default",
					"type" : "post",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							showAddressList();
						} else {
							alert(json.message);
						}
					},
					"error":function(){
						alert("您的登录信息已过期，请重新登录！");
						location.href="/web/login.html";
					}
				});
			}


### 附1：关于检查数据

在执行绝大部分业务时，数据的基本格式都是有要求的，例如用户的组成字符、字符的数量等，则在项目中，需要对数据的基本格式进行检查！

在View中进行检查是非常有必要的！例如在HTML页面中，可以通过Javascript技术实现数据格式的检查！

但是，在Controller中也必须再次检查！因为现今的客户端种类很多，控制器端的检查可以防止各客户端检查不统一，甚至某些专门人士可以使用某些方式避开客户端的检查。

> 可以认为：凡是从客户端提交的第一手数据，都是不可靠的，都应该检查！

尽管Controller会再次检查，在客户端（View）中的检查也是不可缺少的，因为绝大部分的普通用户并不掌握避开检查的技术，则客户端的检查可以帮助服务器端拦截绝大部门数据格式有误的请求，从而减轻服务器的压力！

另外，在Service中，也应该对数据的格式进行检查！这样的目的，是为了防止某些业务调用并不是来自Controller的！

