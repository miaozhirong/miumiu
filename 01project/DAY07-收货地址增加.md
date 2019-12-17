### 24. 收货地址-分析

关于收货地址相关的功能有：显示列表，增加收货地址，修改，删除，设为默认。

这些功能的开发顺序应该是：增加 > 显示列表 > 设为默认 > 删除 > 修改。

由于现在是第1次接触这类数据，则还应该在此之前完成创建数据表、创建实体类这2个步骤。

### 25. 收货地址-创建数据表

创建`t_address`数据表：

	CREATE TABLE t_address (
		aid INT AUTO_INCREMENT COMMENT '收货地址id',
		uid INT COMMENT '归属用户的id',
		name VARCHAR(20) COMMENT '收货人',
		province CHAR(6) COMMENT '省',
		city CHAR(6) COMMENT '市',
		area CHAR(6) COMMENT '区',
		district VARCHAR(50) COMMENT '省市区的汉字名称',
		zip CHAR(6) COMMENT '邮编',
		address VARCHAR(50) COMMENT '详细地址',
		tel VARCHAR(20) COMMENT '固话',
		phone VARCHAR(20) COMMENT '手机',
		tag VARCHAR(20) COMMENT '地址类型',
		is_default INT COMMENT '是否默认：0-非默认，1-默认',
		created_user VARCHAR(20) COMMENT '创建者',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(20) COMMENT '最后修改者',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY (aid)
	) DEFAULT CHARSET=UTF8;

### 26. 收货地址-创建实体类

创建`cn.tedu.store.entity.Address`实体类：

### 27. 收货地址-增加-持久层

**1. 规划SQL语句**

向收货地址数据表中增加新数据的SQL语句大致是：

	INSERT INTO t_address (除了aid以外的字段列表) VALUES (除了aid以外的属性值列表)

在执行增加之前，还应该确定`is_default`的值是多少！可以设置规则“增加的第1条是默认的，后续增加的都不是默认的”。则需要“判断当前即将增加的收货地址是不是第1条”，可以通过“根据uid查询数据的数量”来实现这个判断：

	SELECT COUNT(aid) FROM t_address WHERE uid=?

**2. 接口与抽象方法**

创建`cn.tedu.store.mapper.AddressMapper`持久层接口，并添加以上2个功能对应的抽象方法：

	Integer insert(Address address);

	Integer countByUid(Integer uid);

**3. 配置XML映射**

复制得到`AddressMapper.xml`文件，删除原文件中各子级节点的配置，修改根节点的`namespace`属性的值，然后，添加2个子节点以配置以上2个抽象方法的映射：

	<mapper namespace="cn.tedu.store.mapper.AddressMapper">

		<!-- 增加收货地址数据 -->
		<!-- Integer insert(Address address) -->
		<insert id="insert" useGeneratedKeys="true" keyProperty="aid">
			INSERT INTO t_address (
				uid, name,
				province, city,
				area, district,
				zip, address,
				tel, phone,
				tag, is_default,
				created_user, created_time,
				modified_user, modified_time
			) VALUES (
				#{uid}, #{name},
				#{province}, #{city},
				#{area}, #{district},
				#{zip}, #{address},
				#{tel}, #{phone},
				#{tag}, #{isDefault},
				#{createdUser}, #{createdTime},
				#{modifiedUser}, #{modifiedTime}
			)
		</insert>
		
		<!-- 统计某用户的收货地址的数量 -->
		<!-- Integer countByUid(Integer uid) -->
		<select id="countByUid"
			resultType="java.lang.Integer">
			SELECT 
				COUNT(aid) 
			FROM 
				t_address 
			WHERE 
				uid=#{uid}
		</select>
		
	</mapper>

然后，在`src/test/java`下创建`cn.tedu.store.mapper.AddressMapperTestCase`测试类，在类之前添加2个注解，然后编写2个测试方法，以测试持久层的2个功能：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class AddressMapperTestCase {
	
		@Autowired
		AddressMapper mapper;
		
		@Test
		public void insert() {
			Address address = new Address();
			address.setUid(8);
			address.setName("Hello");
			address.setZip("AAAAAA");
			Integer rows = mapper.insert(address);
			System.err.println("rows=" + rows);
		}
		
		@Test
		public void countByUid() {
			Integer uid = 8;
			Integer count = mapper.countByUid(uid);
			System.err.println("count=" + count);
		}
		
	}


### 28. 收货地址-增加-业务层

**1. 规划异常**

此次执行的是`INSERT`操作，则有可能出现`InsertException`。

由于没有指定数据规则，则没有对应的异常。

**2. 接口与抽象方法**

创建`cn.tedu.store.service.IAddressService`接口，并在接口中添加抽象方法：

	void addnew(Address address, String username) throws InsertException;

**3. 实现抽象方法**

创建`cn.tedu.store.service.impl.AddressServiceImpl`类，实现以上接口，在类之前添加`@Service`注解，在类中添加`@Autowired private AddressMapper addressMapper;`持久层对象。

重写接口中的抽象方法：

	public void addnew(Address address, String username) throws InsertException {
		// 根据参数address中的uid执行查询数量
		// 判断收货地址数量是否为0
		// 是：is_default > 1
		// 否：is_default > 0
		// 将is_default的值封装到参数address中

		// 4项日志数据

		// 执行增加
	}

在业务层中，应该：

- 持久层中的方法，在业务层中，都应该有一个与之对应的方法
而在业务层中的这类方法，应该是私有的

- 如果对应的是增/删/改方法，持久层方法的返回值是Integer，而业务层的应该是void，并且，会判断调用持久层方法的返回值，如果不是期望值，则抛出异常

- 如果对应的是查询方法，则直接调用持久层对象来完成查询

- 在业务层中的业务方法（公有的方法）中，只会调用自身的私有方法，决不直接调用持久层对象的方法

所以，具体的实现为：

	/**
	 * 处理收货地址数据的业务层实现类
	 */
	@Service
	public class AddressServiceImpl implements IAddressService {
		
		@Autowired
		private AddressMapper addressMapper;
	
		@Override
		public void addnew(Address address, String username) throws InsertException {
			// 根据参数address中的uid执行查询数量
			Integer uid = address.getUid();
			Integer count = countByUid(uid);
			// 判断收货地址数量是否为0
			// 是：is_default > 1
			// 否：is_default > 0
			Integer isDefault = count == 0 ? 1 : 0;
			// 将is_default的值封装到参数address中
			address.setIsDefault(isDefault);
	
			// 4项日志数据
			Date now = new Date();
			address.setCreatedUser(username);
			address.setCreatedTime(now);
			address.setModifiedUser(username);
			address.setModifiedTime(now);
	
			// 执行增加
			insert(address);
		}
		
		// 持久层中的方法，在业务层中，都应该有一个与之对应的方法
		// 而在业务层中的这类方法，应该是私有的
		// 如果对应的是增/删/改方法，持久层方法的返回值是Integer，而业务层的应该是void，并且，会判断调用持久层方法的返回值，如果不是期望值，则抛出异常
		// 如果对应的是查询方法，则直接调用持久层对象来完成查询
		// 在业务层中的业务方法（公有的方法）中，只会调用自身的私有方法，决不直接调用持久层对象的方法
		
		/**
		 * 增加收货地址数据
		 * @param address 收货地址数据
		 */
		private void insert(Address address) {
			Integer rows = addressMapper.insert(address);
			if (rows != 1) {
				throw new InsertException(
					"增加收货地址失败！插入数据时出现未知错误！");
			}
		}
		
		/**
		 * 统计某用户的收货地址的数量
		 * @param uid 用户的id
		 * @return 收货地址的数量
		 */
		private Integer countByUid(Integer uid) {
			return addressMapper.countByUid(uid);
		}
	
	}

然后，在`src/test/java`中创建`cn.tedu.store.service.AddressServiceTestCase`测试类，编写并执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class AddressServiceTestCase {
	
		@Autowired
		IAddressService service;
		
		@Test
		public void addnew() {
			try {
				Address address = new Address();
				address.setUid(9);
				address.setName("Controller");
				String username = "系统管理员";
				service.addnew(address, username);
				System.err.println("OK.");
			} catch (ServiceException e) {
				System.err.println(e.getClass().getName());
				System.err.println(e.getMessage());
			}
		}
		
	}

应该至少使用相同的uid增加2条数据，并直接查询数据表，可以看到2次增加的数据的`is_default`值是与业务的设计是相符的。

### 29. 收货地址-增加-控制器层

**1. 处理异常**

无

**2. 设计请求**

	请求路径：/addresses/addnew
	请求参数：Address address, HttpSession session
	请求类型：POST
	响应数据：ResponseResult<Void>
	是否拦截：无需修改配置

**3. 处理请求**

创建`cn.tedu.store.controller.AddressController`控制器类，继承自`BaseController`，在类之前添加`@RestController`和`@RequestMapping("addresses")`注解，在类中声明`@Autowired private IAddressService addressService;`业务层对象。

然后，在类中添加处理请求的方法：

	@RequestMapping("addnew")
	public ResponseResult<Void> addnew(Address address, HttpSession session) {
		//获取uid
		Integer uid=getUidFromSession(session);
		//将uid封装到参数address中
		address.setUid(uid);
		//获取用户名
		String username=session.getAttribute("username").toString();
		//执行增加
		addressService.addnew(address, username);
		//返回成功
		return new ResponseResult<>(SUCCESS);
	}

完成后，打开浏览器，先登录（如果没有登录就访问会被拦截器重定向到登录页），然后通过`http://localhost:8080/addresses/addnew?name=Jack`进行测试。

### 30. 收货地址-增加-前端界面


在addAddress.html设置ajax

	$("#btn-addnew").click(function() {
			$.ajax({
				"url" : "/addresses/addnew",
				"data" : $("#form-addnew").serialize(),
				"type" : "post",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						alert("增加成功！");
					} else {
						alert(json.message);
					}
				},
				"error":function(){
					alert("您的登录信息已过期，请重新登录！");
					location.href="/web/login.html";
				}
			});
		});
设置id和name














