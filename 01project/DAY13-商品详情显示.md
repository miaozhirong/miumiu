### 48. 显示商品详情-持久层

**1. 规划SQL语句**

查询商品详情的SQL语句大致是：

	SELECT title,sell_point,image,price FROM t_goods WHERE id=?

**2. 接口与抽象方法**

在接口中声明抽象方法：

	Goods findById(Long id);

**3. 配置映射**

在`GoodsMapper.xml`中配置以上抽象方法的映射：

	<!-- 根据商品id查询商品详情 -->
	<!-- Goods findById(Long id) -->
	<select id="findById"
		resultType="cn.tedu.store.entity.Goods">
		SELECT 
			title,
			price,image,
			sell_point AS sellPoint
		FROM 
			t_goods 
		WHERE 
			id=#{id} 
	</select>

在`GoodsMapperTestCase`中编写并执行单元测试：

	@Test
	public void findById() {
		Long id = 10000017L;
		Goods data = mapper.findById(id);
		System.err.println(data);
	}

### 49. 显示商品详情-业务层

**1. 规划异常**

无

**2. 接口与抽象方法**

在`IGoodsService`接口中，将`GoodsMapper`中的方法复制粘贴过来，并将方法名中的`find`改成`get`：

	/**
	 * 根据商品id查询商品详情
	 * @param id 商品id
	 * @return 匹配的商品详情，如果没有匹配的数据，则返回null
	 */
	Goods getById(Long id);

**3. 实现**

在`GoodsServiceImpl`实现类中，将`GoodsMapper`中的方法复制粘贴过来，添加`private`权限，并调用持久层对象实现该方法：

	/**
	 * 根据商品id查询商品详情
	 * @param id 商品id
	 * @return 匹配的商品详情，如果没有匹配的数据，则返回null
	 */
	private Goods findById(Long id) {
		return goodsMapper.findById(id);
	}

在`GoodsServiceImpl`实现类中，重写`IGoodsService`接口中定义的抽象方法，并调用自身的私有方法实现：

	@Override
	public Goods getById(Long id) {
		return findById(id);
	}

完成后，在`GoodsServiceTestCase`中编写并执行单元测试：

	@Test
	public void findById() {
		Long id = 10000017L;
		Goods data = service.getById(id);
		System.err.println(data);
	}

### 50. 显示商品详情-控制器层

**1. 处理异常**

无

**2. 设计请求**

	请求路径：/goods/{id}/details
	请求参数：无
	请求类型：GET
	响应数据：ResponseResult<Goods>
	是否拦截：否，需要在白名单中检查 /goods/**

**3. 处理请求**

	@GetMapping("{id}/details")
	public ResponseResult<Goods> getById(
		@PathVariable("id") Long id) {
		// 调用业务层对象执行查询
		Goods data = goodsService.getById(id);
		// 响应“成功”和查询结果
		return new ResponseResult<>(SUCCESS, data);
	}

完成后，打开浏览器，不登录，直接通过`http://localhost:8080/goods/10000017/details`进行测试。

### 51. 显示商品详情-前端界面

首先，应该保证`product.html`是可以被不登录的情况下直接访问的，也就是需要将`/web/product.html`添加到拦截器的白名单中！

然后，确保在主页中点击热销排行中的链接时，URL中包含`id=xxx`。则需要先从FTP下载`jquery-getUrlParam.zip`文件，解压得到`jquery-getUrlParam.js`文件，将该文件复制粘贴到项目的`static`下的`js`文件夹中，并打开`product.html`页面，添加引用该文件的代码：

	<script type="text/javascript" src="../js/jqueryQgetUrlParam.js"></script>

当添加了引用js文件后，可以检查该函数是否可以正确的获取到值：

		$(document).ready(function(){
				var id=$.getUrlParam("id");
				console.log("id="+id);
				$("#gid").val(id);
				showGoodsDetails(id)
			});
			
			function showGoodsDetails(id){
				$.ajax({
					"url" : "/goods/"+id+"/details",
	           		"type" : "GET",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							$("#goods-title").html(json.data.title);
							$("#goods-sell-point").html(json.data.sellPoint);
							$("#goods-price").html(json.data.price);
							for (var i = 1; i <= 5; i++) {
								$("#goods-image-" + i + "-big").attr("src", ".." + json.data.image + i + "_big.png");
								$("#goods-image-" + i).attr("src", ".." + json.data.image + i + ".jpg");
							}
						} else {
							alert(json.message);
						}
					}
				});
			}

分开两个script

		$("#btn-add-to-cart").click(function() {
			$.ajax({
				"url" : "/carts/add_to_cart",
				"data" : $("#form-add-to-cart").serialize(),
				"type" : "post",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						alert("添加成功！");
					} else {
						alert(json.message);
					}
				},
				"error":function(){
					alert("您还没有登录或登录信息已过期，请登录后再执行操作！");
					location.href="/web/login.html";
				}
			});
		});


