### 1. 分析项目

#### 1.1. 项目中涉及的数据

用户、商品、商品类别、收藏、购物车、收货地址、订单。

#### 1.2. 规划各种数据的开发顺序

规划顺序的原则是：**先开发基础数据**，例如：如果没有开发用户数据，就不可能完成订单等数据，则用户数据是必须先完成的！另外，如果某些数据之间没有交叉，那应该**先开发较简单的，再开发较难的**！

所以，以上数据的开发顺序应该是：用户 > 收货地址 > 商品分类 > 商品 > 收藏 > 购物车 > 订单。

#### 1.3. 规划各数据的相关功能

以**用户**数据为例，本项目中可能涉及的功能有：注册、登录、修改密码、修改资料、上传头像。

在开发之前，也应该规划一下这些功能的开发顺序，通常，遵守**增 > 查 > 删 > 改**的顺序。

#### 1.4. 每个功能的开发顺序

在处理每种不同的数据时，首先，应该完成：**创建数据表** > **创建实体类**。

然后，每个功能的开发顺序都应该是：**持久层** > **业务层** > **控制器层** > **前端页面**。

### 2. 用户-注册-创建数据表

由于现在是第1次创建，应该先创建数据库：

	CREATE DATABASE tedu_store;

并使用该数据库：

	USE tedu_store;

然后，创建数据表：

	CREATE TABLE t_user (
		uid INT AUTO_INCREMENT COMMENT '用户的id',
		username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名',
		password CHAR(32) NOT NULL COMMENT '密码',
		salt CHAR(36) COMMENT '盐值',
		avatar VARCHAR(50) COMMENT '头像',
		phone VARCHAR(20)  COMMENT '电话',
		email VARCHAR(50)  COMMENT '邮箱',
		gender INT COMMENT '性别，0-女，1-男',
		is_delete INT COMMENT '是否删除，0-未删除，1-已删除',
		created_user VARCHAR(20) COMMENT '创建者',
		created_time DATETIME COMMENT '创建时间',
		modified_user VARCHAR(20) COMMENT '最后修改者',
		modified_time DATETIME COMMENT '最后修改时间',
		PRIMARY KEY(uid)
	) DEFAULT CHARSET=UTF8;

创建完成后，从FTP服务器下载项目，解压到Workspace中，通过Eclipse的**Import** > **Existing Maven Projects**导入项目，导入完成后，必须先在`application.properties`中添加数据库连接的相关配置（从昨天的项目中复制配置代码，并修改url中的数据库名称），然后运行启动类`StoreApplication`，以确保项目的环境是正确的。

即便项目能运行，也并不代表数据库连接的配置是正确的，应该将昨天项目中的`ConnectionTestCase`复制到当前项目的`src/test/java`的`cn.tedu.store`包中，并执行昨天已经编写好的单元测试方法，如果能成功的在控制台输出连接对象，则表示数据库连接的配置是正确的。

### 3. 用户-注册-创建实体类

创建`cn.tedu.store.entity.BaseEntity`类：

	public class BaseEntity {
		private String createdUser;
		private Date createdTime;
		private String modifiedUser;
		private Date modifiedTime;
		// SET/GET/toString
	}

创建`cn.tedu.store.entity.User`类：

	public class User extends BaseEntity {
		// 与数据表字段匹配的属性，注意使用驼峰命名法，例如isDelete
		// SET/GET/toString
	}

由于`BaseEntity`是实体类的基类，其作用就是用于被继承的，所以，应该将其声明为抽象类。

由于`BaseEntity`只在当前包中使用（所有的实体类都应该在这个包中），所以，应该使用默认的访问权限。

由于`BaseEntity`及其子孙类都是描述数据属性的，所以，应该实现`Serializable`接口。

### 4. 用户-注册-持久层

#### 4.1. 准备工作

首先，检查`application.properties`中是否存在配置：

	# mybatis
	mybatis.mapper-locations=classpath:mappers/*.xml

并检查在`src/main/resources`下是否存在名为`mappers`的文件夹，如果没有，则创建：

![](01.png)

然后，在启动类`StoreApplication.java`的声明语句之前添加：

	@MapperScan("cn.tedu.store.mapper")

即：

	@SpringBootApplication
	@MapperScan("cn.tedu.store.mapper")
	public class StoreApplication {
	
		public static void main(String[] args) {
			SpringApplication.run(StoreApplication.class, args);
		}
	
	}

并且，后续在当前项目中创建的持久层接口文件都应该在`cn.tedu.store.mapper`这个包中。

#### 4.2. 规划SQL语句

此次执行的**注册**功能的数据操作应该是向数据表中插入新的数据，则SQL语句应该是：

	INSERT INTO t_user (除了uid以外的所有字段) VALUES (匹配的值);

除此以外，还应该在插入数据之前，检查用户名是否被占用，可以通过**根据用户名查询用户数据**，并判断查询结果是否为`null`来实现判断，该查询对应的SQL语句应该是：

	SELECT uid FROM t_user WHERE username=?

#### 4.3. 接口与抽象方法

当前是第一次处理用户相关数据，则需要先创建`cn.tedu.store.mapper.UserMapper`接口，并添加抽象方法：

	/**
	 * 处理用户数据的持久层接口
	 */
	public interface UserMapper {
	
		/**
		 * 插入用户数据
		 * @param user 用户数据
		 * @return 受影响的行数
		 */
		Integer insert(User user);
	
		/**
		 * 根据用户名查询用户数据
		 * @param username 用户名
		 * @return 匹配的用户数据，如果没有匹配的数据，则返回null
		 */
		User findByUsername(String username);
		
	}

#### 4.4. 配置XML映射

当前是第一次处理用户相关数据，则需要复制并得到`UserMapper.xml`，将其放在`mappers`文件夹下，并配置根节点`<mapper>`的`namespace`属性，其值是以上接口。

然后，配置以上2个方法对应的映射：

	<?xml version="1.0" encoding="UTF-8" ?>  
	<!DOCTYPE mapper PUBLIC "-//ibatis.apache.org//DTD Mapper 3.0//EN"      
		"http://ibatis.apache.org/dtd/ibatis-3-mapper.dtd">
	
	<mapper namespace="cn.tedu.store.mapper.UserMapper">
	
		<!-- 插入用户数据 -->
		<!-- Integer insert(User user) -->
		<insert id="insert" 
			useGeneratedKeys="true" 
			keyProperty="uid">
			INSERT INTO t_user (
				username, password,
				salt, avatar,
				phone, email,
				gender, is_delete,
				created_user, created_time,
				modified_user, modified_time
			) VALUES (
				#{username}, #{password},
				#{salt}, #{avatar},
				#{phone}, #{email},
				#{gender}, #{isDelete},
				#{createdUser}, #{createdTime},
				#{modifiedUser}, #{modifiedTime}
			)
		</insert>
		
		<!-- 根据用户名查询用户数据 -->
		<!-- User findByUsername(String username) -->
		<select id="findByUsername"
			resultType="cn.tedu.store.entity.User">
			SELECT
				uid
			FROM
				t_user
			WHERE
				username=#{username}
		</select>
		
	</mapper>

完成后，应该执行单元测试。

应该在`src/test/java`下创建`cn.tedu.store.mapper.UserMapperTestCase`测试类，并编写测试方法以执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class UserMapperTestCase {
	
		@Autowired
		public UserMapper mapper;
		
		@Test
		public void insert() {
			User user = new User();
			user.setUsername("root");
			user.setPassword("1234");
			Integer rows = mapper.insert(user);
			System.err.println("rows=" + rows);
		}
		
		@Test
		public void findByUsername() {
			String username = "spring";
			User user = mapper.findByUsername(username);
			System.err.println(user);
		}
	
	}

### 5. 用户-注册-业务层

#### 5.1. 什么是业务

业务，在普通用户眼里来看就是某个特定的功能，例如注册、登录等，但是，对于开发过程而言，可能是由多次增删改查的数据访问有机的结合起来的，在这个过程中，可能存在数据访问操作的先后顺序，及相关逻辑判断。所以，业务可以理解为：开发某个功能时，需要将多个增删改查操作设定一些业务流程和业务逻辑，以保障数据的安全。

#### 5.2. 规划异常

在注册过程中，可能出现的操作错误有：用户名已经被占用，插入数据过程出错。

则应该自定义匹配的异常类：

	cn.tedu.store.service.ex.UsernameDuplicateException
	cn.tedu.store.service.ex.InsertException

另外，通常会创建自定义的基类：

	cn.tedu.store.service.ex.ServiceException

> 自定义的异常都应该是``的子孙类异常。

所以，先创建`ServiceException`继承自`RuntimeException`，然后分别创建`UsernameDuplicateException`和`InsertException`，都继承自`ServiceException`。

#### 5.3. 接口与抽象方法

创建业务层接口`cn.tedu.store.service.IUserService`，并在接口中声明抽象方法：

	void reg(User user) throws UsernameDuplicateException, InsertException;

关于抽象方法的设计原则：

- 返回值类型：仅以**成功**为前提来设计返回值

- 参数：是由客户端可以提交的

- 异常：任何视为操作失败，都抛出某种异常，例如登录时，如果用户名不存在，则抛出`用户名不存在Exception`，如果密码错误，则抛出`密码错误Exception`，后续，方法的调用者可以通过`try...catch...catch`来区分操作成功，或哪种失败！

#### 5.4. 实现抽象方法

先创建业务层实现类`cn.tedu.store.service.impl.UserServiceImpl`，实现`IUserService`接口，为该类添加`@Service`注解，声明`@Autowired private UserMapper userMapper;`属性，然后实现以上抽象方法：

	@Service
	public class UserServiceImpl 
		implements IUserService {
	
		@Autowired 
		private UserMapper userMapper;
		
		@Override
		public void reg(User user) throws UsernameDuplicateException, InsertException {
			// 根据尝试注册的用户名查询数据
			User result = 
					userMapper.findByUsername(
						user.getUsername());
			// 判断查询结果是否为null
			if (result == null) {
				// 是：允许注册
				// 执行注册
				Integer rows = userMapper.insert(user);
				if (rows != 1) {
					throw new InsertException();
				}
			} else {
				// 否：不允许注册，抛出异常
				throw new UsernameDuplicateException();
			}
		}
	
	}

然后，在`src/test/java`下创建单元测试类`cn.tedu.store.service.UserServiceTestCase`，并编写、执行单元测试：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class UserServiceTestCase {
		
		@Autowired
		public IUserService service;
	
		@Test
		public void reg() {
			try {
				User user = new User();
				user.setUsername("spring");
				user.setPassword("8888");
				service.reg(user);
				System.err.println("OK.");
			} catch (ServiceException e) {
				System.err.println(e.getClass());
			}
		}
	
	}

#### 5.5. 对异常进行文字描述

通常，在业务层中抛出异常时，应该对抛出的异常进行文字的描述，后续，将会把这些描述文字响应给客户端。

所以，应该先打开`ServiceException`，通过Eclipse的**Source**菜单中的**Generate Constructors from superclass**添加与父类相同的5个构造方法，然后，再分别打开`InsertException`和`UsernameDuplicateException`，使用相同的操作方式也添加5个构造方法。

然后，在业务方法中，抛出异常时，可以通过异常类的构造方法封装描述文字：

	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 根据尝试注册的用户名查询数据
		User result = 
				userMapper.findByUsername(
					user.getUsername());
		// 判断查询结果是否为null
		if (result == null) {
			// 是：允许注册
			// TODO 密码加密
			// 执行注册
			Integer rows = userMapper.insert(user);
			if (rows != 1) {
				throw new InsertException(
					"注册时发生未知错误，请联系系统管理员！");
			}
		} else {
			// 否：不允许注册，抛出异常
			throw new UsernameDuplicateException(
				"尝试注册的用户名(" + user.getUsername() + ")已经被占用！");
		}
	}
业务层除了要保证数据的逻辑安全（即：数据应该根据我们设定的规则而产生或变化，例如：用户名对应的数据不存在时才可以注册该用户名），还应该保证数据在产生或发生变化时是完整的！

以此次“注册”为例，注册时的参数`User user`应该是客户端提交的，则用户可以通过客户端界面输入例如用户名、密码等信息，这些信息都会封装在这个参数中，但是，这个参数中不会包含其它不由客户端提交的数据，例如`is_delete`及4个日志数据，则在执行注册之前，需要补全这些数据！

	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 根据尝试注册的用户名查询数据
		User result = 
				userMapper.findByUsername(
					user.getUsername());
		// 判断查询结果是否为null
		if (result == null) {
			// 是：允许注册
			// 封装is_delete
			user.setIsDelete(0);
			// 封装日志
			Date now = new Date();
			user.setCreatedUser(user.getUsername());
			user.setCreatedTime(now);
			user.setModifiedUser(user.getUsername());
			user.setModifiedTime(now);
			// TODO 密码加密
			// 执行注册
			Integer rows = userMapper.insert(user);
			if (rows != 1) {
				throw new InsertException(
					"注册时发生未知错误，请联系系统管理员！");
			}
		} else {
			// 否：不允许注册，抛出异常
			throw new UsernameDuplicateException(
				"尝试注册的用户名(" + user.getUsername() + ")已经被占用！");
		}
	}

然后，编写密码加密的方法，该方法将应用于注册和后续所有需要验证密码或加密的业务中：

	/**
	 * 将密码执行加密
	 * @param password 原密码
	 * @param salt 盐值
	 * @return 加密后的结果
	 */
	private String getMd5Password(String password, String salt) {
		// 拼接原密码与盐值
		String str = salt + password + salt;
		// 循环加密5次
		for (int i = 0; i < 5; i++) {
			str = DigestUtils.md5DigestAsHex(
					str.getBytes()).toUpperCase();
		}
		// 返回结果
		return str;
	}

接下来，在业务方法中生成随机盐值，并加密：

	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 根据尝试注册的用户名查询数据
		User result = 
				userMapper.findByUsername(
					user.getUsername());
		// 判断查询结果是否为null
		if (result == null) {
			// 是：允许注册
			// 封装is_delete
			user.setIsDelete(0);
			// 封装日志
			Date now = new Date();
			user.setCreatedUser(user.getUsername());
			user.setCreatedTime(now);
			user.setModifiedUser(user.getUsername());
			user.setModifiedTime(now);
			// 密码加密
			// - 生成随机盐
			String salt = UUID.randomUUID().toString().toUpperCase();
			// - 基于原密码和盐值执行加密
			String md5Password = getMd5Password(
					user.getPassword(), salt);
			// - 将盐和加密结果封装到user对象中
			user.setSalt(salt);
			user.setPassword(md5Password);
			// 执行注册
			Integer rows = userMapper.insert(user);
			if (rows != 1) {
				throw new InsertException(
					"注册时发生未知错误，请联系系统管理员！");
			}
		} else {
			// 否：不允许注册，抛出异常
			throw new UsernameDuplicateException(
				"尝试注册的用户名(" + user.getUsername() + ")已经被占用！");
		}
	}

### 6. 用户-注册-控制器层

#### 6.1. 处理异常


创建`cn.tedu.store.util.ResponseResult`类，作为控制器类的响应结果类型，后续，当控制器方法返回这种类型的数据时，Jackson框架会把响应结果对象转换为JSON数据格式：

	/**
	 * 控制器向客户端响应结果的数据类型
	 * 
	 * @param <T> 如果控制器会向客户端响应某些数据，则表示响应的数据的类型
	 */
	public class ResponseResult<T> implements Serializable {
	
		private static final long serialVersionUID = 1568501256910141001L;
	
		private Integer state;
		private String message;
		private T data;
		
		// SET/GET
	}

因为控制器类将调用业务层来完成各个业务，大部分都会抛出异常，则控制器类就需要处理异常，推荐使用SpringMVC框架提供的统一处理的方式来处理异常。

为了使得每个控制器都能直接使用统一处理异常的方式，应该先创建控制器类的基类`cn.tedu.store.controller.BaseController`，并在这个类中添加处理异常的方法：

	/**
	 * 控制器类的基类
	 */
	public abstract class BaseController {
		
		/**
		 * 响应结果时用于表示操作成功
		 */
		protected static final int SUCCESS = 200;
	
		@ExceptionHandler(ServiceException.class)
		public ResponseResult<Void> handleException(ServiceException e) {
			ResponseResult<Void> rr
				= new ResponseResult<>();
			rr.setMessage(e.getMessage());
			
			if (e instanceof UsernameDuplicateException) {
				// 400-用户名冲突异常
				rr.setState(400);
			} else if (e instanceof InsertException) {
				// 500-插入数据异常
				rr.setState(500);
			}
			
			return rr;
		}
	
	}

由于该类不需要被单独创建对象，所以，类本身是不需要添加注解的！

由于该类的作用就是被其它各控制器类继承，所以，应该声明为抽象类！

无论是成功，还是某种操作失败，都应该向客户端响应操作结果的代号，推荐自行决定编号规则，例如使用200表示成功，使用4xx表示可详细描述原因的错误，使用5xx表示不便于描述原因的错误。

#### 6.x. 设计请求

在编写处理请求的方法之前，应该先规划如何处理请求：

	请求路径：/users/reg
	请求参数：User user
	请求方式：POST
	响应结果：ResponseResult<Void>

#### 6.x. 处理请求

创建`cn.tedu.store.controller.UserController`控制器类，继承自`BaseController`，添加`@RestController`和`@RequestMapping("users")`注解，并在类中声明`@Autowired private IUserService userService;`业务层对象。

然后，在控制器类中添加处理请求的方法：

	@RequestMapping("reg")
	public ResponseResult<Void> reg(User user) {
		// 执行注册
		userService.reg(user);
		// 返回成功
		return new ResponseResult<>(SUCCESS);
	}

完成后，打开浏览器，通过`http://localhost:8080/users/reg?username=handler&password=123456`进行测试，以检验控制器是否可以正常运行，且，完成后，应该将`@RequestMapping`替换为`@PostMapping`。

### 7. 用户-注册-前端页面

解压静态资源文件压缩包，将5个文件夹全部复制到项目的`src/main/resources/static`中。

注册页面是`register.html`，然后添加AJAX相关代码：

	<script type="text/javascript">
	$("#btn-reg").click(function(){
		$.ajax({
			"url":"/users/reg",
			"data":$("#form-reg").serialize(),
			"type":"post",
			"dataType":"json",
			"success":function(json) {
				if (json.state == 200) {
					alert("注册成功！");
				} else {
					alert(json.message);
				}
			}
		});
	});
	</script>

并且，在HTML代码部分，添加表单和按钮的id，为输入框设置name。

### -------------------------------------------

### 附1：密码加密

用户的密码应该执行加密后，再进行存储，以避免数据库中的数据被泄密，导致用户数据被窃取。

常见的加密算法有：AES、3DES等，并不适用于密码加密，因为这些加密算法都是可以被逆向运算的，即：如果能够得到加密过程中的所有参数，就可以根据密文（加密后的结果）逆向运算得到原文（加密前的原始数据）。

应用于密码加密的应该使用摘要算法。

摘要算法的特点：

- 使用相同的摘要算法，相同的原文，得到的摘要数据必然是相同的；

- 无论原文是什么样的数据，使用固定的摘要算法，得到的摘要数据的长度是固定的； 

- 使用固定的摘要算法，不同的原文，几乎不会得到相同的摘要数据。

所有的摘要算法都是不可被逆运算的！

在应用于密码加密时，由于密码的原文的长度是有限制的，例如限制为6~16位长度，则，在有限的原文中，找到2个不同的原文却对应相同的摘要数据，几乎就是不可能的！所以，将摘要算法应用于密码加密领域是安全的！

常见的摘要算法有SHA系列和MD系列。应用于密码加密的比较常见的就是MD5，应用于消息或下载文件的校验的可能是SHA-256或其它长度更长的消息摘要算法。

另外，也可以找到许多与“MD5破解”相关的内容！

首先，MD5破解是真实存在的！此“破解”指的是通过找到2个不同的原文，运算得到相同的摘要，从而验证该算法是不安全的，并且，运算次数并不需要2的128次方这么多。如果认为“逆运算才算破解”，本身就是对摘要算法的误解！

另外，网上也有许多网站可以实现在线解密，其实，这些网站就是记录了原文和对应的摘要，当尝试通过这些网站“解密”时，本质上是一种“反查”操作。

因为有“反查”的存在，所以，为了进一步保障用户密码安全，应该：

1. 强制要求使用强度更高的原始密码；

2. 加盐；

3. 反复加密；

4. 综合以上应用方式。

### 附2：阶段小结

1. 学会拆分项目，先分出数据，再分出每种数据需要开发的功能，再分持久层、业务层等层次，在开发时，一次只解决一个问题！

2. 再次理解MVC。

3. 在开发持久层时，应该：设计SQL语句 > 接口与抽象方法 > 配置映射。

4. 在开发业务层时，应该：设计异常 > 接口与抽象方法 > 实现抽象方法。

5. 在开发控制器层时，应该：处理异常 > 设计所处理的请求 > 处理请求。

### 7. 用户-注册-前端页面

### 8. 用户-登录-持久层

### 9. 用户-登录-业务层

### 10. 用户-登录-控制器层

### 11. 用户-登录-前端页面

### 12. 用户-修改密码-持久层

### 13. 用户-修改密码-业务层

### 14. 用户-修改密码-控制器层

### 15. 用户-修改密码-前端页面

### 16. 用户-修改资料-持久层

### 17. 用户-修改资料-业务层

### 18. 用户-修改资料-控制器层

### 19. 用户-修改资料-前端页面

### 20. 用户-上传头像-持久层

### 21. 用户-上传头像-业务层

### 22. 用户-上传头像-控制器层

### 23. 用户-上传头像-前端页面
























