### 66. 购物车-增加商品数量-持久层

**1. 规划SQL语句**

增加商品数量的SQL语句应该是：

	UPDATE xx SET num=?,modified_user=?,modified_time=? WHERE cid=?

该功能已经完成！无需再次开发。

在增加数量之前，还应该检查该数据是否存在！

	SELECT xx FROM t_cart WHERE cid=?

并检查数据归属是否正确，则查询时需要获取`uid`字段的值。

后续更新商品数量时，还需要知道原数量是多少，则查询时需要获取`num`字段的值。

**2. 接口与抽象方法**

	Cart findByCid(Integer cid);

**3. 配置映射**

	<!-- 根据购物车数据的id查询购物车数据 -->
	<!-- Cart findByCid(Integer cid) -->
	<select id="findByCid"
		resultType="cn.tedu.store.entity.Cart">
		SELECT 
			uid,num
		FROM 
			t_cart
		WHERE 
			cid=#{cid}
	</select>

### 67. 购物车-增加商品数量-业务层

**1. 规划异常**

当更新商品数量时，可能出现`UpdateException`。

当检查购物车数据是否存在时，可能出现`CartNotFoundException`。

当检查购物车数据归属时，可能出现`AccessDeniedException`。

以上3种异常中，需要创建的是`CartNotFoundException`。

**2. 接口与抽象方法**

	Integer addNum(Integer cid, Integer uid, String username) throws CartNotFoundException, AccessDeniedException, UpdateException;

**3. 实现**

将持久层的`Cart findByCid(Integer cid);`方法复制到业务层实现类中，并私有化实现。

在业务层实现类中重写抽象方法：


	@Override
	public Integer addNum(Integer cid, Integer uid, String username)
			throws CartNotFoundException, AccessDeniedException, UpdateException {
		// 根据参数cid查询数据
		Cart result = findByCid(cid);
		// 检查查询结果是否为null
		if (result == null) {
			// 是：CartNotFoundException
			throw new CartNotFoundException(
				"增加商品数量失败！尝试访问的购物车数据不存在！");
		}

		// 检查参数uid与查询结果中的uid是否不同
		if (uid != result.getUid()) {
			// 是：AccessDeniedException
			throw new AccessDeniedExcption(
				"增加商品数量失败！尝试访问的购物车数据归属错误！");
		}

		// 取出查询结果中的商品数量，增加1，得到新的数量
		Integer newNum = result.getNum() + 1;
		// 更新商品数量：updateNum(Integer cid, Integer num, String modifiedUser, Date modifiedTime)
		updateNum(cid, newNum, username, new Date());
		// 返回新的数量
		return newNum;
	}

单元测试：

	@Test
	public void addNum() {
		try {
			Integer cid = 3;
			Integer uid = 11;
			String username = "小刘同学";
			Integer newNum = service.addNum(cid, uid, username);
			System.err.println("OK. new num=" + newNum);
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 68. 购物车-增加商品数量-控制器层

**1. 处理异常**

处理`CartNotFoundException`。

**2. 设计请求**

	请求路径：/carts/{cid}/add_num
	请求参数：@PathVariable("cid") Integer cid, HttpSession session
	请求类型：POST
	响应数据：ResponseResult<Integer>

**3. 处理请求**

	@RequestMapping("{cid}/add_num")
	public ResponseResult<Integer> addNum(@PathVariable("cid") Integer cid, HttpSession session) {
		// 获取uid和username
		// 调用业务层对象执行增加并获取结果：addNum(Integer cid, Integer uid, String username)
		// 返回成功和结果(新的数量)
	}

完成后，通过`http://localhost:8080/carts/5/add_num`执行测试。

### 68. 购物车-增加商品数量-前端页面

			function addNum(cid){
				$.ajax({
					"url" : "/carts/"+cid+"/add_num",
					"type" : "post",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							//showCartList();
							$("#goods-num-"+cid).val(json.data);
							var price=parseInt($("#goods-price-"+cid).html());
							var totalPrice=price*json.data;
							$("#goods-total-price-"+cid).html(totalPrice);
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