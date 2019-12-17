### 显示确认订单页-持久层

**1. 规划SQL语句**

在“显示确认订单页”，需要显示当前登录的用户的收货地址列表，还需要显示所勾选的购物车数据列表。

在`AddressMapper`中已经完成显示当前登录的用户的收货地址列表的功能。

目前，尚未开发“显示所勾选的购物车数据列表”功能，则需要添加，该功能对应的SQL语句大致是：

	select cid,gid,title,image,price,t_cart.num 
	from t_cart 
	inner join t_goods 
	on t_cart.gid=t_goods.id 
	where cid in (?,?,?)
	order by t_cart.created_time desc

即：相对于此前已经完成的“显示当前用户的购物车数据列表”，此次只是查询时的WHERE子句不同，改为`where cid in (?,?,?)`。

**2. 接口与抽象方法**

由于以上功能应该是处理购物车数据的相关功能，所以，在`CartMapper.java`接口中添加抽象方法：

	List<CartVO> findByCids(Integer[] cids);

**3. 配置映射**

在`CartMapper.xml`中添加节点配置映射：

	<select id="findByCids" resultType="xx.xx.xx.xx.CartVO">
		select 
			cid,gid,title,image,price,t_cart.num 
		from 
			t_cart 
		inner join 
			t_goods 
		on 
			t_cart.gid=t_goods.id 
		where 
			cid in (
			<foreach collection="cids"
				item="cid" separator=",">
				#{cid}
			</foreach>
			)
		order by 
			t_cart.created_time desc
	</select>

### 显示确认订单页-业务层

与其它查询数据的业务层开发流程和套路都相同。

### 显示确认订单页-控制器层

此次“获取用户勾选的购物车数据列表”的URL应该是`http://localhost:8080/carts/get_by_cids?cids=3&cids=4&cids=5`。

处理请求的方法：

	@GetMapping("get_by_cids")
	public ResponseResult<List<CartVO>> getByCids(
		Integer[] cids) {
		// 直接调用业务层对象执行查询，并获取结果
		// 返回成功与查询结果
	}


### 显示确认订单页-控制器层

		$(document).ready(function() {
				showAddressList();
				showCartList();
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
								var html='<option value="#{aid}">#{tag} | #{name} | #{phone} | #{district}#{address}</option>';
								html=html.replace(/#{aid}/g,list[i].aid);
								html=html.replace(/#{tag}/g,list[i].tag);
								html=html.replace(/#{name}/g,list[i].name);
								html=html.replace(/#{district}/g,list[i].district);
								html=html.replace(/#{address}/g,list[i].address);
								html=html.replace(/#{phone}/g,list[i].phone);
								
								$("#address-list").append(html);
							}
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

			function showCartList(){
				$("#cart-list").empty();
				$.ajax({
					"url" : "/carts/get_by_cids"+location.search,
					"type" : "GET",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							var list=json.data;
							var allCount=0;
							var allPrice=0;
							for(var i=0;i<list.length;i++){
								console.log(list[i].title);
								allCount+=list[i].num;
								 allPrice+=list[i].num*list[i].price;
								var html='<tr>'
									+'<td><img src="..#{goods-image}collect.png" class="img-responsive" /></td>'
									+'<td>#{goods-title}</td>'
									+'<td>¥<span>#{goods-price}</span></td>'
									+'<td>#{goods-num}</td>'
									+'<td><span>#{total-price}</span></td>'
									+'</tr>';
									
								html=html.replace(/#{cid}/g,list[i].cid);	
								html=html.replace(/#{goods-image}/g,list[i].image);
								html=html.replace(/#{goods-title}/g,list[i].title);
								html=html.replace(/#{goods-price}/g,list[i].price);
								html=html.replace(/#{goods-num}/g,list[i].num);
								html=html.replace(/#{total-price}/g,list[i].num*list[i].price);
								
								$("#cart-list").append(html);
							}
							$("#all-count").html(allCount);
							$("#all-price").html(allPrice);
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