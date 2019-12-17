### 16. 用户-修改资料-持久层

**1. 规划SQL语句**

修改用户资料将使用的SQL语句大致是：

	UPDATE 
		t_user 
	SET 
		phone=?,email=?,
		gender=?,modified_user=?,
		modified_time=? 
	WHERE 
		uid=?

执行修改之前还是应该检查用户数据是否存在、是否被标记为删除，相关功能已经完成，无需再次开发。

在界面的处理方面，在用户点击“修改资料”之前，页面上应该已经显示当前的用户数据，即：需要查询出当前用户的相关数据：

	SELECT username,phone,email,gender FROM t_user WHERE uid=?

此前已经开发了`findByUid()`方法，则只需要补充查询的字段列表即可！

**2. 接口与抽象方法**

在`UserMapper.java`接口中添加抽象方法：

	Integer updateInfo(User user);

**3. 配置XML映射**

先配置映射：

	<!-- 更新用户个人资料 -->
	<!-- Integer updateInfo(User user) -->
	<update id="updateInfo">
		UPDATE
			t_user
		SET
			phone=#{phone},
			email=#{email},
			gender=#{gender},
			modified_user=#{modifiedUser},
			modified_time=#{modifiedTime}
		WHERE
			uid=#{uid}
	</update>

单元测试：

	@Test
	public void updateInfo() {
		User user = new User();
		user.setUid(14);
		user.setPhone("13800138014");
		user.setEmail("spring@tedu.cn");
		user.setGender(1);
		user.setModifiedUser("ROOT");
		user.setModifiedTime(new Date());
		Integer rows = mapper.updateInfo(user);
		System.err.println("rows=" + rows);
	}

### 17. 用户-修改资料-业务层

**1. 规划异常**

此次是对用户数据进行操作，应该先检查用户数据，则可能抛出`UserNotFoundException`。

此次执行是更新操作，则可能抛出`UpdateException`。

此次没有新的异常。

**2. 接口与抽象方法**

在`IUserService`接口中添加“修改资料”的抽象方法：

	void changeInfo(User user) throws UserNotFoundException, UpdateException;

**3. 实现抽象方法**

在`UserServiceImpl.java`中实现以上抽象方法：

	@Override
	public void changeInfo(User user) throws UserNotFoundException, UpdateException {
		// 从参数user中获取uid
		Integer uid = user.getUid();
		// 调用持久层对象的方法，根据uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改个人资料错误！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改个人资料错误！尝试访问的用户数据不存在！");
		}

		// 创建当前时间对象，封装到user中
		user.setModifiedTime(new Date());
		// TODO 确保modifiedUser属性是有值的

		// 调用持久层对象执行修改，并获取返回值（即受影响的行数）
		Integer rows = userMapper.updateInfo(user);
		// 判断返回值是否不为1
		if (rows != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改个人资料错误！更新数据时发生未知错误！");
		}
	}

完成后，编写并执行单元测试：

	@Test
	public void changeInfo() {
		try {
			User user = new User();
			user.setUid(11);
			user.setModifiedUser("超级管理员");
			user.setPhone("13800138011");
			user.setEmail("root@tedu.cn");
			user.setGender(1);
			service.changeInfo(user);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}

### 18. 用户-修改资料-控制器层

**1. 处理异常**

此次业务层没有抛出新的异常，抛出的都是已经在`BaseController`中处理过的类型的异常，所以，无需处理。

**2. 设计请求**

	请求路径：/users/change_info
	请求参数：User user, HttpSession session
	请求类型：POST
	响应数据：ResponseResult<Void>

**3. 处理请求**

在`UserController`中处理请求：

	@RequestMapping("change_info")
	public ResponseResult<Void> changeInfo(
		User user, HttpSession session) {
		// 获取uid
		Integer uid = getUidFromSession(session);
		// 获取用户名
		String username = session.getAttribute("username").toString();
		// 将uid和用户名封装到参数user对象中
		user.setUid(uid);
		user.setModifiedUser(username);
		// 调用业务层对象执行修改个人资料
		userService.changeInfo(user);
		// 返回操作成功
		return new ResponseResult<>(SUCCESS);
	}

完成后，在浏览器中通过`http://localhost:8080/users/change_info?phone=13900139011&email=root@qq.com&gender=0`执行测试。

### 19. 用户-修改资料-前端页面

与此前的页面不同，此次前端页面需要完成的功能有2项：

1. 当页面打开时，就显示当前登录的用户的信息；

2. 当点击“修改”按钮时，提交修改。

首先，完成第1项任务，应该是：当页面打开时，客户端（浏览器）就直接向服务器发出请求，请求获取当前登录的用户的信息，当服务器返回用户信息后，将这些数据显示在网页中。

目前，服务器端并不能响应客户端“请求获取当前登录的用户的信息”这样的需求，所以，需要在控制器中添加对这种请求的处理，但是，控制器中的功能都是通过业务层对象来实现的，而业务层中也没有这样功能，同理，业务层的功能是基于持久层实现的，而持久层已经有`findByUid()`方法可以实现，则持久层无需再开发。

所以，现在的任务应该是：

**开发“获取用户数据”的业务层**

应该先在业务层接口`IUserService`中添加新的抽象方法：

	User getByUid(Integer uid) throws UserNotFoundException;

然后在业务层实现类`UserServiceImpl`中实现以上方法：

	@Override
	public User getByUid(Integer uid) throws UserNotFoundException {
		// 根据参数uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"获取用户信息失败！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"获取用户信息失败！尝试访问的用户数据不存在！");
		}
		
		// 将查询结果中的password/salt/isDelete设置为null
		result.setPassword(null);
		result.setSalt(null);
		result.setIsDelete(null);
		
		// 返回查询结果
		return result;
	}

完成后，编写并执行单元测试：

	@Test
	public void getByUid() {
		try {
			Integer uid = 11;
			User user = service.getByUid(uid);
			System.err.println(user);
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}

**开发“获取用户数据”的控制器层**

设计请求：

	请求路径：/users/details
	请求参数：HttpSession session
	请求类型：GET
	响应数据：ResponseResult<User>

处理请求：

	@GetMapping("details")
	public ResponseResult<User> getByUid(
			HttpSession session) {
		// 获取uid
		Integer uid = getUidFromSession(session);
		// 调用业务层对象执行获取数据
		User data = userService.getByUid(uid);
		// 返回操作成功及数据
		return new ResponseResult<>(SUCCESS, data);
	}

打开浏览器，先登录，然后通过`http://localhost:8080/users/details`测试。

**在前端页面中，打开时即请求当前登录的用户数据，并显示**

		$(document).ready(function() {
			$.ajax({
				"url" : "/users/details",
				"type" : "get",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						console.log("username="+json.data.username);
						console.log("phone="+json.data.phone);
						console.log("email="+json.data.email);
						console.log("gender="+json.data.gender);
						
						$("#input-username").val(json.data.username);
						$("#input-phone").val(json.data.phone);
						$("#input-email").val(json.data.email);
						if(json.data.gender==1){
							$("#gender-male").attr("checked","checked");
						}else{
							$("#gender-female").attr("checked","checked");
						}
					} else {
						alert(json.message);
					}
				}
			});
		});

**在前端页面中，当点击“修改”按钮时执行修改个人资料**

		$("#btn-change-info").click(function() {
			$.ajax({
				"url" : "/users/change_info",
				"data" : $("#form-change-info").serialize(),
				"type" : "post",
				"dataType" : "json",
				"success" : function(json) {
					if (json.state == 200) {
						alert("个人资料修改成功！");
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

