### 31. 省市区-显示列表-导入district数据表
   `source d:/t_dict_district.sql;`

### 32. 省市区-显示列表-创建实体类
创建`cn.tedu.store.entity.District`实体类：

	private Integer id;
	private String parent;
	private String code;
	private String name;

###33. 省市区-显示列表-持久层
**1. 规划SQL语句**
根据父级代号获取省市区列表

	SELECT id,parent,code,name FROM t_dict_district WHERE parent=? ORDER BY code ASC

根据父级代号获取省市区详情

	SELECT name FROM t_dict_district WHERE code=#{code}

**2. 接口与抽象方法**
创建`cn.tedu.store.mapper.DistrictMapper`持久层接口，并添加以上2个功能对应的抽象方法：

	//根据父级代号获取省市区列表
	List<District> findByParent(String parent);
	//根据父级代号获取省市区详情	
	District findByCode(String code);

**3. 配置XML映射**
复制得到`DistrictMapper.xml`文件，删除原文件中各子级节点的配置，修改根节点的namespace属性的值，然后，添加2个子节点以配置以上2个抽象方法的映射：

然后，在src/test/java下创建`cn.tedu.store.mapper.DistrictMapperTestCase`测试类，在类之前添加2个注解，然后编写2个测试方法，以测试持久层的2个功能：

###33. 省市区-显示列表-业务层
1. 规划异常

无

2. 接口与抽象方法
创建接口`cn.tedu.store.service.IDistrictService`,
添加抽象方法

	List<District> getByParent(String parent);
	
	District getByCode(String code);

3. 实现抽象方法

	public List<District> getByParent(String parent) {}

	public District getByCode(String code) {}

###33. 省市区-显示列表-控制层
	@RestController
	@RequestMapping("/districts")
	public class DistrictController extends BaseController{
		@Autowired
		private IDistrictService districtService;
		@GetMapping("/")
		public ResponseResult<List<District>>getByParent(@RequestParam("parent") String parent){
			List<District>data=districtService.getByParent(parent);
			return new ResponseResult<>(SUCCESS,data);
		}
	}


**实现显示全国所有省的下拉列表**

1.当页面加载时，就应该向服务器端发出请求，获取全国所有省的数据，即`$(document).ready()`函数触发时，执行`$.ajax()`;

2.获取回来的数据需要填充到下拉列表中，下拉列表的HTML代码结构大致是：

	<select id="" name="xx">
		<option value="值1">第1个选项</option>
		<option value="值2">第2个选项</option>
		<option value="值3">第3个选项</option>
	</select>

则需要将获取回来的数据进行遍历，形成若干个`<option..`选项，通过`$("#menu").append(html)`可以将选项的HTML代码填充到某个标签中。

	$(document).ready(function() {
			showProvinceList();
			$("#province").change(function(){
				$("#city").empty();
				$("#area").empty();
				$("#area").append("<option value=0>------请选择------</option>");
				showCityList();
			});
			$("#city").change(function(){
				$("#area").empty();
				showAreaList();
				
			});
			$("#city").append("<option value=0>------请选择------</option>");
			$("#area").append("<option value=0>------请选择------</option>");
		});
		function showProvinceList(){
			$("#province").append("<option value=0>------请选择------</option>");
			$.ajax({
				"url" : "/districts/",
				"data" : "parent=86",
				"type" : "GET",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						var list=json.data;
						for(var i=0;i<list.length;i++){
							console.log(list[i].name);
							var op='<option value="'+list[i].code+'">'+list[i].name+'</option>';
							$("#province").append(op);
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
		function showCityList(){
			var provinceCode=$("#province").val();
			$("#city").append("<option value=0>------请选择------</option>");
			if(provinceCode==0){
				return;
			}
			$.ajax({
				"url" : "/districts/",
				"data" : "parent="+provinceCode,
				"type" : "GET",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						var list=json.data;
						for(var i=0;i<list.length;i++){
							console.log(list[i].name);
							var op='<option value="'+list[i].code+'">'+list[i].name+'</option>';
							$("#city").append(op);
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
		function showAreaList(){
			var cityCode=$("#city").val();
			$("#area").append("<option value=0>------请选择------</option>");
			if(cityCode==0){
				return;
			}
			$.ajax({
				"url" : "/districts/",
				"data" : "parent="+cityCode,
				"type" : "GET",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						var list=json.data;
						for(var i=0;i<list.length;i++){
							console.log(list[i].name);
							var op='<option value="'+list[i].code+'">'+list[i].name+'</option>';
							$("#area").append(op);
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

