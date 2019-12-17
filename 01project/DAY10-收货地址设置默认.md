### 35. 收货地址-设为默认-持久层

**1. 规划SQL语句**

将指定的收货地址设置为默认的SQL语句应该是：

	UPDATE 
		t_address 
	SET 
		is_default=1, modified_user=?, modified_time=? 
	WHERE 
		aid=?

同时，还应该将原本默认的收货地址设置为非默认，但是，却不明确到底哪条数据是原本默认的收货地址，则可以在设置默认之前，**将该用户的所有收货地址全部设置为非默认**：

	UPDATE t_address SET is_default=0 WHERE uid=?

除此以外，在操作数据之前，还应该检查数据是否存在：

	SELECT name FROM t_address WHERE aid=?

此次并不关心查询的结果中包含哪些字段的数据，只需要知道是否查询到数据即可，所以，查询的字段可以写任何字段名。

**2. 接口与抽象方法**

在`AddressMapper.java`接口中添加抽象方法：

	Integer updateDefault(
		@Param("aid") Integer aid, 
		@Param("modifiedUser") String modifiedUser, 
		@Param("modifiedTime") Date modifiedTime);

	Integer updateNonDefault(Integer uid);

	Address findByAid(Integer aid);

**3. 配置映射**

	<!-- 将指定的收货地址设置为默认 -->
	<!-- Integer updateDefault(
			@Param("aid") Integer aid, 
			@Param("modifiedUser") String modifiedUser, 
			@Param("modifiedTime") Date modifiedTime) -->
	<update id="updateDefault">
		UPDATE
			t_address
		SET
			is_default=1,
			modified_user=#{modifiedUser},
			modified_time=#{modifiedTime}
		WHERE
			aid=#{aid}
	</update>
	
	<!-- 将某用户的所有收货地址全部设置为非默认 -->
	<!-- Integer updateNonDefault(Integer uid) -->
	<update id="updateNonDefault">
		UPDATE
			t_address
		SET
			is_default=0
		WHERE
			uid=#{uid}
	</update>

	<!-- 根据收货地址id查询收货地址数据 -->
	<!-- Address findByAid(Integer aid) -->
	<select id="findByAid"
		resultType="cn.tedu.store.entity.Address">
		SELECT 
			name 
		FROM 
			t_address 
		WHERE 
			aid=#{aid}
	</select>

测试：

	@Test
	public void updateDefault() {
		Integer aid = 18;
		String modifiedUser = "系统管理员";
		Date modifiedTime = new Date();
		Integer rows = mapper.updateDefault(aid, modifiedUser, modifiedTime);
		System.err.println("rows=" + rows);
	}

	@Test
	public void updateNonDefault() {
		Integer uid = 11;
		Integer rows = mapper.updateNonDefault(uid);
		System.err.println("rows=" + rows);
	}

	@Test
	public void findByAid() {
		Integer aid = 1800;
		Address address = mapper.findByAid(aid);
		System.err.println(address);
	}

### 36. 收货地址-设为默认-业务层

**1. 规划异常**

由于在更新操作之前，需要检查数据是否存在，则可能抛出`AddressNotFoundException`。

执行的2次更新操作，都有可能抛出`UpdateException`。

由于被设置为默认的收货地址数据的aid是客户端提交的，则可能是不可靠的！例如该aid对应的收货地址数据不是当前登录的用户的数据！这种情况是拒绝执行相关数据操作的！则抛出`AccessDeniedExcption`。

则需要创建`AddressNotFoundException`和`AccessDeniedExcption`异常类。

**2. 接口与抽象方法**

在`IAddressService`中添加抽象方法：

	void setDefault(Integer aid, Integer uid, String username) throws AddressNotFoundException, UpdateException;

**3. 实现抽象方法**

首先，应该将持久层的3个方法复制粘贴到`AddressServiceImpl`中，并全部修饰为私有方法，对于查询方法，直接调用持久层对象来实现功能，对于2个更新的方法，将返回值修改为`void`，并调用持久层对象来实现功能，同时获取返回值，如果返回值不是预期值，则抛出`UpdateException`。

然后，实现抽象方法：

	public void setDefault(Integer aid, Integer uid, String username) throws AddressNotFoundException, UpdateException {
		// 根据参数aid查询数据
		Address result=findByAid(aid);
	    // 判断查询结果是否为null
		if(result==null) {
	    // 是：AddressNotFoundException
			throw new AddressNotFoundException("收货地址没找到");
		}
		//判断
		if(result.getUid()!=uid) {
			throw new AccessDeniedException("访问被拒绝");
		}
	    // 根据参数uid将全部地址设置为非默认
		updateNonDefault(uid);

	    // 创建当前时间对象
	    // 根据aid将指定地址设置为默认
		updateDefault(aid, username, new Date());
	}

### 37. 收货地址-设为默认-控制器层

**1. 处理异常**

此次抛出了2种新的异常：`AddressNotFoundException`和`AccessDeniedException`，需要在`BaseController`中进行处理！

**2. 设计请求**

	请求路径：/addresses/1/set_default	资源名/id/命令
	请求参数：HttpSession session
	请求类型：POST
	响应数据：ResponseResult<Void>

**3. 处理请求**

具体实现为：

	@RequestMapping("/{aid}/set_default")
	public ResponseResult<Void> setDefault(
		@PathVariable("aid") Integer aid,
		HttpSession session) {
		// 获取uid和username
		Integer uid = getUidFromSession(session);
		String username = session.getAttribute("username").toString();
		// 执行
		addressService.setDefault(aid, uid, username);
		// 返回
		return new ResponseResult<>(SUCCESS);
	}

### 38. 收货地址-设为默认-前端界面

在设置默认标签添加`onclick="setDefault(#{aid})"`

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



### --------------------------------------

### 附1：基于SpringJDBC的事务

事务：如果某项业务涉及多次增、删、改操作，使用事务可以保证这多次操作要么全部成功，要么全部失败，从而保障数据的完整性和数据安全。

基于SpringJDBC使用事务时（可以是基于SSM框架的项目，也可以直接使用SpringBoot框架，或其它框架并添加了spring-jdbc依赖均可），只需要在相关业务方法之前添加`@Transactional`注解即可，则该业务方法就是以事务的方式来执行的。

其实现过程大致是：

	try {
		开启事务
		执行若干条数据操作（增/删/改）
		提交commit
	} catch (RuntimeException e) {
		回滚rollback
	}

也就是说，在整个业务方法执行过程中，只要出现了`RuntimeException`，就会导致事务回滚，如果自始至终没有出现`RuntimeException`，则会提交事务。

所以，在实际开发时：

1. 任何增删改操作都应该及时获取其返回的受影响的行数，并判断是否是预期值，如果不是，则抛出异常，且抛出的异常必须是`RuntimeException`的子孙类异常；

2. 如果某个业务方法涉及2次或更多次的增/删/改操作（例如2次Update，或1次Delete和1次Update），则需要事务保障，在需要以事务的方式来执行业务方法之前添加`@Transactional`注解。

> 其实，`@Transactional`注解也可以添加在类之前，这样的话，就会使得每个方法都是有事务保障的，但是，没有这个必要！

> 如果使用的不是SpringBoot，则需要在Spring的配置文件中配置`TranactionManager`并开启`<tx:annotation-driven transaction-manager="transactionManager" />`。

**注意：SpringJDBC的事务处理是基于接口的代理的，在使用时，需要使用接口来声明业务层对象！**