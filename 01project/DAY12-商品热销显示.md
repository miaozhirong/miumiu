### 42. 商品-数据表

从FTP下载`t_goods.zip`解压后导入**商品分类表**和**商品表**，及表中数据。

注：如果导入`t_goods.zip`中的脚本出现乱码，则下载`t_goods2.zip`再次导入。

### 43. 商品-实体类

创建`cn.tedu.store.entity.Goods`实体类，表示商品数据。

	/**
	 * 商品数据的实体类
	 */
	public class Goods extends BaseEntity {
	
		private static final long serialVersionUID = 5960164494648879998L;
	
		private Long id;
		private Long categoryId;
		private String itemType;
		private String title;
		private String sellPoint;
		private Long price;
		private Integer num;
		private String barcode;
		private String image;
		private Integer status;
		private Integer priority;

		// SET/GET
	}

注：暂不需要创建**商品分类**的实体类。

### 44. 主页热销排行-持久层

**1. SQL**

	SELECT 
		id,title,price,image 
	FROM 
		t_goods 
	WHERE 
		status=1 
	ORDER BY 
		priority DESC 
	LIMIT 0,4

**2. 接口与抽象方法**

创建`cn.tedu.store.mapper.GoodsMapper`接口，并添加抽象方法：

	List<Goods> findHotList();

**3. 配置映射**

复制得到`src/main/resources/mapper/GoodsMapper.xml`文件，删除原有子级节点，修改根节点的`namespace`属性，并配置以上抽象方法的映射：

	<mapper namespace="cn.tedu.store.mapper.GoodsMapper">

		<!-- 获取热销的前4项商品的数据列表 -->
		<!-- List<Goods> findHotList() -->
		<select id="findHotList"
			resultType="cn.tedu.store.entity.Goods">
			SELECT 
				id,title,
				price,image 
			FROM 
				t_goods 
			WHERE 
				status=1 
			ORDER BY 
				priority DESC 
			LIMIT 
				0,4
		</select>
		
	</mapper>

编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class GoodsMapperTestCase {
	
		@Autowired
		GoodsMapper mapper;
		
		@Test
		public void findByUid() {
			List<Goods> list = mapper.findHotList();
			System.err.println("BEGIN:");
			for (Goods item : list) {
				System.err.println(item);
			}
			System.err.println("END.");
		}
		
	}

### 45. 主页热销排行-业务层

**1. 异常**

无

**2. 接口与抽象方法**

创建`cn.tedu.store.service.IGoodsService`接口，并添加抽象方法：

	/**
	 * 获取热销的前4项商品的数据列表
	 * @return 热销的前4项商品的数据列表
	 */
	List<Goods> getHotList();

**3. 实现类**

创建`cn.tedu.store.service.impl.GoodsServiceImpl`实现类，在类之前添加`@Service`注解，在类中声明`@Autowired private GoodsMapper goodsMapper;`持久层对象。

复制持久层接口中的抽象方法到实现类，使用私有权限，并直接调用持久层对象实现方法。

重写接口中的抽象方法，通过调用自身的私有方法来实现。

### 46. 主页热销排行-控制器层

**1. 异常**

无

**2. 设计请求**

	请求路径：/goods/hot
	请求参数：无
	请求类型：GET
	响应数据：ResponseResult<List<Goods>>
	是否拦截：否，需要在拦截器的配置中添加白名单

**3. 处理请求**

首先，打开拦截器的配置类，在其中添加`/goods/**`到白名单中。

然后，创建`cn.tedu.store.controller.GoodsController`控制器类，继承自`BaseController`，在类之前添加`@RestController`和`@RequestMapping("goods")`注解，并在类中声明`@Autowired private IGoodsService goodsService`业务层对象。

然后，添加处理请求的方法：

	@GetMapping("hot")
	public ResponseResult<List<Goods>> getHostList() {
		List<Goods> data=goodsService.getHotList();
		return new ResponseResult<>(SUCCESS,data);
	}

完成后，通过`http://localhost:8080/goods/hot`进行测试。

### 47. 主页热销排行-前端界面

首先，打开拦截器的配置类，在其中添加`/web/index.html`到白名单中。

		$(document).ready(function() {
				showHotList();
			});
			function showHotList(){
				$("#hot-list").empty();
				$.ajax({
					"url" : "/goods/hot",
					"type" : "GET",
					"dataType" : "json",
					"success" : function(json) {
						if (json.state == 200) {
							var list=json.data;
							for(var i=0;i<list.length;i++){
								console.log(list[i].title);
								var html='<div class="col-md-12">'
								+'<div class="col-md-7 text-row-2"><a href="product.html?id=#{id}">#{title}</a></div>'
								+'<div class="col-md-2">¥#{price}</div>'
								+'<div class="col-md-3"><img src="..#{image}collect.png" class="img-responsive" />'
								+'</div>';
								html=html.replace(/#{id}/g,list[i].id);
								html=html.replace(/#{title}/g,list[i].title);
								html=html.replace(/#{price}/g,list[i].price);
								html=html.replace(/#{image}/g,list[i].image);
								$("#hot-list").append(html);
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