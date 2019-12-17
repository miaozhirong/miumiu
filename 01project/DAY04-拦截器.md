

### 补：拦截器

创建`cn.tedu.store.interceptor.LoginInterceptor`登录拦截器类，实现`HandlerInterceptor`接口，并重写`preHandle()`方法，根据Session中有没有uid来决定拦截或放行：

	public class LoginInterceptor implements HandlerInterceptor {

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			// 获取session对象
			HttpSession session = request.getSession();
			// 尝试从session中获取用户的uid（因为登录后session中肯定有uid）
			Object uid = session.getAttribute("uid");
			// 判断是否正确的获取到了uid
			if (uid == null) {
				// 尝试获取uid失败，意味着用户没有登录，或登录已超
				response.sendRedirect("/web/login.html");
				return false;
			}
			// 放行
			return true;
		}
	}

然后，在`cn.tedu.store.conf`包下创建`LoginInterceptorConfigurer`类，作为配置拦截器的类，需要添加`@Configuration`注解，并实现`WebMvcConfigurer`接口（使用早期版本的话是继承自`WebMvcConfigurerAdapter`），并重写`addInterceptors()`方法以完成拦截器的配置：

	@Configuration
	public class LoginInterceptorConfigurer 
		implements WebMvcConfigurer {
	
		@Override
		public void addInterceptors(
				InterceptorRegistry registry) {
			// 创建拦截器对象
			HandlerInterceptor interceptor
				= new LoginInterceptor();
			
			// 白名单，即：不要求登录即可访问的路径
			List<String> patterns = new ArrayList<>();
			patterns.add("/js/**");
			patterns.add("/css/**");
			patterns.add("/images/**");
			patterns.add("/bootstrap3/**");
			patterns.add("/web/register.html");
			patterns.add("/web/login.html");
			patterns.add("/users/reg");
			patterns.add("/users/login");
			
			// 通过注册工具添加拦截器对象
			registry.addInterceptor(interceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(patterns );
		}
	
	}

当添加了拦截器后，如果在浏览器中，打开了“修改密码”页，登录超时后再点击“修改”按钮，会没有反应！因为是通过AJAX发出的异步请求，由于登录超时，服务器端的拦截器会要求重定向，浏览器是通过AJAX对应的子线程去完成重定向的，而在浏览器窗体中没有任何体现！所以，需要在调用的`$.ajax()`函数的参数中，补充`error`属性，该属性会在服务器响应非2xx响应码时被触发执行其回调函数：

	<script type="text/javascript">
	$("#btn-change-password").click(function(){
		$.ajax({
			"url":"/users/change_passwordAAAAAA",
			"data":$("#form-change-password").serialize(),
			"type":"post",
			"dataType":"json",
			"success":function(json) {
				if (json.state == 200) {
					alert("修改成功！");
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
	</script>

> 其实，服务器端响应400、404、405、500等响应码，也会导致error的函数被回调，但是，完整的项目中，AJAX请求应该不会出现4xx的请求错误，服务器端也不应该会出现5xx的内部错误，所以，可以认为：完整的项目中，会导致error的函数被回调的，只有3xx的响应码。
