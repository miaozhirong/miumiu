### 58. 购物车-显示列表-持久层

**1. 规划SQL语句**

要显示某用户的购物车数据列表，需要执行的SQL语句大致是：

	select xx from t_cart where uid=?

但是，在`t_cart`表中存储的都是关联数据的id，例如商品数据的`gid`，即便查询到了`gid`也不可能直接用于显示界面，还应该根据`gid`查询出商品的标题、图片、单价等，为了保证查询出的结果能被直接显示到界面中，查询语句应该是：

	select cid,gid,title,image,price,t_cart.num 
	from t_cart 
	inner join t_goods 
	on t_cart.gid=t_goods.id 
	where uid=?
	order by t_cart.created_time desc
	
**2. 接口与抽象方法**

在设计抽象方法之前，需要新创建`cn.tedu.store.vo.CartVO`VO类，以用于封装查询结果：

	public class CartVO implements Serializable {
		private Integer cid;
		private Long gid;
		private String title;
		private String image;
		private Long price;
		private Integer num;
		// GET/SET
	}

然后，在`CartMapper.java`接口中添加抽象方法：

	List<CartVO> findByUid(Integer uid);

**3. 配置映射**

在`CartMapper.xml`中配置：

	<!-- 获取某用户的购物车数据列表 -->
	<!-- List<CartVO> findByUid(Integer uid) -->
	<select id="findByUid"
		resultType="cn.tedu.store.vo.CartVO">
		SELECT
			cid, gid,
			title, image,
			price, t_cart.num
		FROM
			t_cart
		INNER JOIN
			t_goods
		ON
			t_cart.gid=t_goods.id
		WHERE
			uid=#{uid}
		ORDER BY
			t_cart.created_time DESC
	</select>

然后编写并执行单元测试：

	@Test
	public void findByUid() {
		Integer uid = 11;
		List<CartVO> list = mapper.findByUid(uid);
		System.err.println("BEGIN:");
		for (CartVO item : list) {
			System.err.println(item);
		}
		System.err.println("END.");
	}

### 59. 购物车-显示列表-业务层

**1. 规划异常**

无

**2. 接口与抽象方法**

在`ICartService`中添加抽象方法：

	/**
	 * 获取某用户的购物车数据列表
	 * @param uid 用户的id
	 * @return 匹配的购物车数据列表
	 */
	List<CartVO> getByUid(Integer uid);

**3. 实现**

首先，在`CartServiceImpl`实现类中再次粘贴持久层中的方法，并将其私有化实现：

然后，重写接口中的抽象方法，调用自身的私有方法实现：

最后，在`CartServiceTestCase`中编写并执行单元测试：

### 60. 购物车-显示列表-控制器层

**1. 处理异常**

无

**2. 设计请求**

	请求路径：/carts/
	请求参数：HttpSession session
	请求类型：GET
	响应结果：ResponseResult<List<CartVO>>
	是否拦截：是，无需修改配置

**3. 处理请求**

在`CartController`中添加处理请求的方法：

	@GetMapping("/")
	public ResponseResult<List<CartVO>> getByUid(HttpSession session) {
		// 获取uid
		Integer uid=getUidFromSession(session);
		// 执行查询
		List<CartVO>data=cartService.getByUid(uid);
		// 响应成功与查询结果
		return new ResponseResult<>(SUCCESS,data);
	}

完成后，打开浏览器，通过`http://localhost:8080/carts/`执行测试，测试之前应该先登录。

### 60. 购物车-显示列表-前端页面

			$(document).ready(function() {
				showCartList();
				
			});
			function showCartList(){
				$("#cart-list").empty();
				$.ajax({
					"url" : "/carts/",
					"type" : "GET",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							var list=json.data;
							for(var i=0;i<list.length;i++){
								console.log(list[i].title);
								var html='<tr><td><input name="cids" value="#{cid}" type="checkbox" class="ckitem" /></td>'
									+'<td><img src="..#{goods-image}collect.png" class="img-responsive" /></td>'
									+'<td>#{goods-title}</td>'
									+'<td>¥<span id="goods-price-#{cid}">#{goods-price}</span>.00</td>'
									+'<td>'
									+'<input type="button" value="-" class="num-btn" onclick="reduceNum(1)" />'
									+'<input id="goods-num-#{cid}" type="text" size="2" readonly="readonly" class="num-text" value="#{goods-num}">'
									+'<input class="num-btn" type="button" value="+" onclick="addNum(#{cid})" />'
									+'</td>'
									+'<td>¥<span id="goods-total-price-#{cid}">#{total-price}</span>.00</td>'
									+'<td>'
									+'<input type="button" onclick="delCartItem(this)" class="cart-del btn btn-default btn-xs" value="删除" />'
									+'</td></tr>';
									
								html=html.replace(/#{cid}/g,list[i].cid);	
								html=html.replace(/#{goods-image}/g,list[i].image);
								html=html.replace(/#{goods-title}/g,list[i].title);
								html=html.replace(/#{goods-price}/g,list[i].price);
								html=html.replace(/#{goods-num}/g,list[i].num);
								html=html.replace(/#{total-price}/g,list[i].num*list[i].price);
								
								$("#cart-list").append(html);
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

