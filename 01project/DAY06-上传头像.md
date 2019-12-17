### 20. 用户-上传头像-持久层

**1. 规划SQL语句**

首先，上传头像的功能其实共分为2个部分，一部分是接收上传的文件并存储到服务器的硬盘中，另一部分是将存储下来的文件的路径记录在数据库中！所以，在此前`t_user`表的设计中，关于头像的`avatar`字段是使用`varchar(50)`，就是用来记录每个用户的头像的路径值的，例如`upload/xxxx.jpg`。

所以，对于持久层开发而言，上传头像需要做的事情的本质就是**修改当前用户的avatar字段的值**！则需要执行的SQL语句就是（与**修改密码**极为相似）：

	UPDATE t_user SET avatar=?, modified_user=?, modified_time=? WHERE uid=?

另外，在业务中，也应该检查用户数据是否存在、是否标记为已删除，这些功能已经存在，无需再开发。

**2. 接口与抽象方法**

在`UserMapper.java`接口中添加抽象方法：

	Integer updateAvatar(
		@Param("uid") Integer uid, 
		@Param("avatar") String avatar, 
		@Param("modifiedUser") String modifiedUser, 
		@Param("modifiedTime") Date modifiedTime);

**3. 配置XML映射**

在`UserMapper.xml`中配置以上抽象方法对应的SQL映射：

	<!-- 更新用户的头像 -->
	<!-- Integer updateAvatar(
			@Param("uid") Integer uid, 
			@Param("avatar") String avatar, 
			@Param("modifiedUser") String modifiedUser, 
			@Param("modifiedTime") Date modifiedTime) -->
	<update id="updateAvatar">
		UPDATE
			t_user
		SET
			avatar=#{avatar},
			modified_user=#{modifiedUser},
			modified_time=#{modifiedTime}
		WHERE
			uid=#{uid}
	</update>

在`UserMapperTestCase`中编写并执行单元测试：

	@Test
	public void updateAvatar() {
		Integer uid = 14;
		String avatar = "这里应该是头像的路径";
		String modifiedUser = "超级管理员";
		Date modifiedTime = new Date();
		Integer rows = mapper.updateAvatar(uid, avatar, modifiedUser, modifiedTime);
		System.err.println("rows=" + rows);
	}

### 21. 用户-上传头像-业务层

**1. 规划异常**

无

**2. 接口与抽象方法**

在`IUserService`接口中添加“上传头像”的抽象方法：

	void changeAvatar(Integer uid, String avatar, String username) throws UserNotFoundException, UpdateException;

**3. 实现抽象方法**

在`UserServiceImpl`实现类中实现以上抽象方法：

	@Override
	public void changeAvatar(Integer uid, String avatar, String username)
			throws UserNotFoundException, UpdateException {
		// 根据参数uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改头像错误！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改头像错误！尝试访问的用户数据不存在！");
		}
		
		// 创建当前时间对象
		Date now = new Date();
		// 调用持久层的方法执行更新头像，并获取返回值
		Integer rows = userMapper.updateAvatar(uid, avatar, username, now);
		// 判断返回值是否不为1，是，抛出异常
		if (rows != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改头像错误！更新数据时发生未知错误！");
		}
	}

完成后，在`UserServiceTestCase`中编写并执行单元测试：

	@Test
	public void changeAvatar() {
		try {
			Integer uid = 14;
			String username = "Admin";
			String avatar = "新头像地址";
			service.changeAvatar(uid, avatar, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}

### 22. 用户-上传头像-控制器层

**1. 处理异常**

暂无

**2. 设计请求**

	请求路径：/users/change_avatar
	请求参数：HttpServletRequest request, @RequestParam("avatar") MultipartFile avatar, HttpSession session
	请求类型：POST
	响应数据：ResponseResult<String>

**3. 处理请求**
	
	/**
	 * 上传头像的文件夹名称
	 */
	public static final String UPLOAD_DIR="upload";
	public static final long UPLOAD_AVATAR_MAX_SIZE=1*1024*1024;
	
	public static final List<String> UPLOAD_AVATAR_TYPES=new ArrayList<String>();
	static {
		UPLOAD_AVATAR_TYPES.add("image/jpg");
		UPLOAD_AVATAR_TYPES.add("image/png");
	}

	@PostMapping("change_avatar")
	public ResponseResult<String>changeAvatar(
			HttpServletRequest request,
			@RequestParam("avatar") MultipartFile avatar,
			HttpSession session) {
		// TODO 判断isEmpty
		if(avatar.isEmpty()) {
			//抛异常
			throw new FileEmptyException("上传头像失败！未选择文件或文件为空");
		}
		// TODO 判断文件大小
		if(avatar.getSize()>UPLOAD_AVATAR_MAX_SIZE) {
			//抛异常
			throw new FileSizeException("上传头像失败！不允许使用超过"+UPLOAD_AVATAR_MAX_SIZE/1024+"KB的文件！");
		}
		// TODO 判断文件类型
		if(!UPLOAD_AVATAR_TYPES.contains(avatar.getContentType())) {
			//不允许上传
			throw new FileTypeException("上传头像失败！不支持文件类型");
		}
		// 确定保存文件的文件夹的File对象：参考上传的demo
		String parentPath=request.getServletContext().getRealPath(UPLOAD_DIR);
		File parent=new File(parentPath);
		if (!parent.exists()) {
	        parent.mkdirs();
	    }
	    // 确定保存的文件的文件名：参考上传的demo
		String originalFileName=avatar.getOriginalFilename();
		String suffix="";
		int benginIndex=originalFileName.lastIndexOf(".");
		if(benginIndex!=1) {
			suffix=originalFileName.substring(benginIndex);
		}
		String child=UUID.randomUUID().toString()+suffix;
	    // 创建保存文件的File对象：dest = new File(parent, child);
		File dest=new File(parent,child);
	    // 执行上传并保存头像文件：
		try {
			avatar.transferTo(dest);
		} catch (IllegalStateException e) {		
			throw new FileStateException("上传头像失败！所选文件已经不可使用");
		} catch (IOException e) {
			throw new FileIOException("上传头像失败！读写数据错误");
		}
		
	    // 从Session中获取uid和username
		Integer uid=getUidFromSession(session);
	    // 从session中获取username
		String username=session.getAttribute("username").toString();
		String avatarPath="/"+UPLOAD_DIR+"/"+child;
	    // 执行修改数据：service.changeAvatar(uid, avatarPath, username)
		userService.changeAvatar(uid, avatarPath, username);
	    // 返回成功及avatarPath
		ResponseResult<String>rr=new ResponseResult<>();
		rr.setState(SUCCESS);
		rr.setData(avatarPath);
		return rr;
	}

### 23. 用户-上传头像-前端页面


当完成控制器端的基本上传功能后，就可以直接开始编写前端页面，以尽早的测试较少的代码，验证功能是否正常，当功能已经正常后，再补全控制器中剩余的相关检查等代码。

此次使用的前端页面是`upload.html`，该页面需要实现的功能与此前其它页面相同，也都是需要提交AJAX请求到服务器端的控制器，所以，可以从前序的其它页面中复制相关代码再进行调整。

相对于普通的表单数据提交，此次涉及文件上传时，区别在于：

1. 不再使用`表单对象.serialize()`，而是需要通过`new FormData(表单对象)`的方式获取需要提交的数据；

2. 调用`$.ajax()`函数时，需要多配置2个属性：`"contentType":false`和`"processData":false`

具体的代码实现为：
	加上`script type="text/javascript" src="../bootstrap3/js/jquery.cookie.js" charset="utf-8"></script`
	
	$(document).ready(function(){
			if($.cookie("avatar")!=null){
				$("img-avatar").attr("src",$.cookie("avatar"));
			}		
	});
	$("#btn-change-avatar").click(function(){
		$.ajax({
			"url":"/users/change_avatar",
			"data":new FormData($("#form-change-avatar")[0]),
			"contentType":false,
			"processData":false,
			"type":"post",
			"dataType":"json",
			"success":function(json) {
				if (json.state == 200) {
					alert("上传头像成功！");
					$("#img-avatar").attr("src",json.data);
					$.cookie("avatar", json.data, {expire: 7});
				} else {
					alert(json.message);
				}
			},
			"error":function() {
				alert("您的登录信息已过期，请重新登录！");
				location.href="/web/login.html";
			}
		});
	});


剩下的，就是修改标签的id和表单控件的name值。

完成后，可以测试功能，与传统的SpringMVC项目不同，由于SpringBoot内置Tomcat，如果上传成功，在Eclipse中是可以看到上传的文件的。

接下来，应该在上传成功后显示所上传的头像图片，需要先为显示头像的`<img>`标签分配id值，然后，当上传成功后：

	$("#img-avatar").attr("src", json.data);

目前，上传成功后，可以显示所上传的图片，但是，刷新或重新打开页面时，仍显示默认的头像图片，并不是所上传的图片！要解决这个问题：

1. 当登录成功后，服务器端就将头像路径响应给客户端，并且，客户端应该将头像路径存储到Cookie中；

2. 当打开“修改头像”页面时，检查Cookie中是否存在头像路径，如果存在，则显示；

3. 当修改头像成功后，需要更新Cookie中存储的头像路径。

在代码中的表现为：

1. 服务器端用于执行“登录”的查询中，是否查询了`avatar`字段，如果没有，需要补充查询该字段；

2. 在`login.html`中，客户端登录成功时，需要将头像保存到Cookie中：

	$.cookie("username", json.data.username, {expire: 7});
	$.cookie("avatar", json.data.avatar, {expire: 7});

3. 在`upload.html`中，页面加载完成时就检查头像，如果有，则显示：

	$(document).ready(function() {
		if ($.cookie("avatar") != null) {
			$("#img-avatar").attr("src", $.cookie("avatar"));
		}
	});

4. 在`upload.html`中，上传成功后需要更新Cookie中存储的头像路径：

	$.cookie("avatar", json.data, {expire: 7});


### 附2：配置整个项目上传文件的最大大小
在主main配置

	@SpringBootApplication
	@MapperScan("cn.tedu.store.mapper")
	@Configuration
	public class StoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}
	@Bean
	public MultipartConfigElement getMultipartConfig() {
		MultipartConfigFactory factory=new MultipartConfigFactory();
		DataSize maxSize=DataSize.ofMegabytes(100L);//整个项目允许上传的文件大小最大为100MB
		factory.setMaxFileSize(maxSize);
		factory.setMaxRequestSize(maxSize);
		return factory.createMultipartConfig();
	}

}

### 附1：在HTML页面中使用Cookie

在jQuery中的`$.cookie()`函数是用于操作Cookie的函数。

当需要向Cookie中存入数据时：

	$.cookie(数据名, 数据值, {expire: 有效期为多少天});

同样是`$.cookie()`函数，当只有1个参数时，表示从Cookie中获取数据：

	$.cookie(数据名);

**注意：使用以上函数需要引用jquery.cookie.js文件！**



