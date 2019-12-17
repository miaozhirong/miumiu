### 12. 用户-修改密码-持久层

**1. 规划SQL语句**

执行修改密码的SQL语句大致是：

	UPDATE t_user SET password=?, modified_user=?, modified_time=? WHERE uid=?

在执行修改之前，还应该验证原密码是否正确，则还会涉及：

	SELECT password,salt FROM t_user WHERE uid=?

**2. 接口与抽象方法**

在`UserMapper.java`接口中添加抽象方法：

	Integer updatePassword(
		@Param("uid") Integer uid, 
		@Param("password") String password, 
		@Param("modifiedUser") String modifiedUser, 
		@Param("modifiedTime") Date modifiedTime);

	User findByUid(Integer uid);

**3. 配置XML映射**

在`UserMapper.xml`中配置以上2个抽象方法的映射：

	<!-- 更新用户的密码 -->
	<!-- Integer updatePassword(
			@Param("uid") Integer uid, 
			@Param("password") String password, 
			@Param("modifiedUser") String modifiedUser, 
			@Param("modifiedTime") Date modifiedTime) -->
	<update id="updatePassword">
		UPDATE
			t_user
		SET
			password=#{password},
			modified_user=#{modifiedUser},
			modified_time=#{modifiedTime}
		WHERE
			uid=#{uid}
	</update>
	
	<!-- 根据用户id查询用户数据 -->
	<!-- User findByUid(Integer uid) -->
	<select id="findByUid"
		resultType="cn.tedu.store.entity.User">
		SELECT
			password,salt
		FROM
			t_user
		WHERE
			uid=#{uid}
	</select>

在`UserMapperTestCase`中编写并执行单元测试，测试时，不要使用`root`用户进行测试：

	@Test
	public void updatePassword() {
		Integer uid = 12;
		String password = "1234";
		String modifiedUser = "超级管理员";
		Date modifiedTime = new Date();
		Integer rows = mapper.updatePassword(uid, password, modifiedUser, modifiedTime);
		System.err.println("rows=" + rows);
	}
	
	@Test
	public void findByUid() {
		Integer uid = 12;
		User user = mapper.findByUid(uid);
		System.err.println(user);
	}

### 13. 用户-修改密码-业务层

**1. 规划异常**

当用户尝试修改密码时，应该先验证原密码是否正确，当原密码正确的情况下才允许执行修改密码。

为了保证能验证原密码，需要先查询用户数据，查询时，则可能出现用户数据不存在的问题（例如用户先登录，然后管理员删除了该用户的数据，然后用户尝试修改密码，就会出现这种情况），则将抛出`UserNotFoundException`，同理，还应该检查用户数据的`isDelete`是否正常，否则，还是应该抛出`UserNotFoundException`。

> 则对应的查询功能需要补充查询`is_delete`字段的值。

当用户数据是正确的情况下，则应该将原密码和盐结合起来加密，以验证原密码是否正确，如果不正确，则抛出`PasswordNotMatchException`。

最终，用户将执行`UPDATE`操作，则可能出现更新数据异常，抛出`UpdateException`。

以上可能抛出的3种异常中，`UpdateException`是需要新创建的，也是继承自`ServiceException`。

**2. 接口与抽象方法**

在`IUserService`接口中添加抽象方法：

	void changePassword(Integer uid, String oldPassword, String newPassword, String username) throws UserNotFoundException, PasswordNotMatchException, UpdateException;

**3. 实现抽象方法**

在`UserServiceImpl`类中重写该抽象方法：

	@Override
	public void changePassword(Integer uid, String oldPassword, String newPassword, String username)
			throws UserNotFoundException, PasswordNotMatchException, UpdateException {
		// 根据参数uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改密码错误！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改密码错误！尝试访问的用户数据不存在！");
		}

		// 取出查询结果中的盐值
		String salt = result.getSalt();
		// 基于参数oldPassword和盐值执行加密
		String oldMd5Password = getMd5Password(oldPassword, salt);
		// 判断加密结果与查询结果中的密码是否不匹配
		if (!result.getPassword().equals(oldMd5Password)) {
			// 是：抛出PasswordNotMatchException
			throw new PasswordNotMatchException(
				"修改密码错误！原密码错误！");
		}

		// 基于参数newPassword和盐值执行加密
		String newMd5Password = getMd5Password(newPassword, salt);
		// 创建当前时间对象，作为最后修改时间
		Date now = new Date();
		// 调用持久层执行更新密码（新密码是以上加密的结果），并获取返回值
		Integer rows = userMapper.updatePassword(uid, newMd5Password, username, now);
		// 判断返回值是否不为1
		if (rows != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改密码错误！更新数据时发生未知错误！");
		}
	}

然后，在`UserServiceTestCase`中编写并执行单元测试：

	@Test
	public void changePassword() {
		try {
			Integer uid = 13;
			String username = "Admin";
			String oldPassword = "8888";
			String newPassword = "1234";
			service.changePassword(uid, oldPassword, newPassword, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}

### 14. 用户-修改密码-控制器层

**1. 处理异常**

此次抛出了新的异常`UpdateException`，需要在`BaseController`中添加1个新的`else if`对其进行处理。

**2. 设计请求**

	请求路径：/users/change_password
	请求参数：@RequestParam("old_password") String oldPassword, @RequestParam("new_password") String newPassword, HttpSession session
	请求类型：POST
	响应数据：ResponseResult<Void>

**3. 处理请求**

在`UserController`中添加处理请求的方法：

	@RequestMapping("change_password")
	public ResponseResult<Void> changePassword(
		@RequestParam("old_password") String oldPassword,
		@RequestParam("new_password") String newPassword, 
		HttpSession session) {
		//调用业务层对象的login()方法执行登录
		User result=userService.login(username, password);
		//将登录结果中的uid封装到session中
		session.setAttribute("uid", result.getUid());
		//将登录结果中的username封装到session中
		session.setAttribute("username", result.getUsername());
		//返回结果
		return new ResponseResult<>(SUCCESS,result);
	}

完成后，先登录，通过`http://localhost:8080/users/change_password?old_password=1234&new_password=8888`进行测试，测试完成后，将`@RequestMapping`修改为`@PostMapping`。

### 15. 用户-修改密码-前端页面
	$("#btn-password").click(function() {
			$.ajax({
				"url" : "/users/change_password",
				"data" : $("#form-password").serialize(),
				"type" : "post",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						alert("修改密码成功！");
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
### 16. 用户-修改资料-持久层

### 17. 用户-修改资料-业务层

### 18. 用户-修改资料-控制器层

### 19. 用户-修改资料-前端页面

### 20. 用户-上传头像-持久层

### 21. 用户-上传头像-业务层

### 22. 用户-上传头像-控制器层

### 23. 用户-上传头像-前端页面









