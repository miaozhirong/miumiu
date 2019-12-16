package com.miu.chuba.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.miu.chuba.controller.ex.FileEmptyException;
import com.miu.chuba.controller.ex.FileIOException;
import com.miu.chuba.controller.ex.FileSizeException;
import com.miu.chuba.controller.ex.FileStateException;
import com.miu.chuba.controller.ex.FileTypeException;
import com.miu.chuba.entity.User;
import com.miu.chuba.service.IUserService;
import com.miu.chuba.util.ResponseResult;


@RestController
@RequestMapping("users")
public class UserController extends BaseController {
	
	@Autowired
	private IUserService userService;
	
	@RequestMapping("reg")
	public ResponseResult<Void> reg(User user) {
		// 执行注册
		userService.reg(user);
		// 返回成功
		return new ResponseResult<>(SUCCESS);
	}

	@RequestMapping("login")
	public ResponseResult<User> login(
			String username, String password, 
			HttpSession session) {
		
		// 检查用户名与密码的格式
		
		// 调用业务层对象的login()方法执行登录
		User result = userService.login(username, password);
		// 将登录结果中的uid封装到session中
		session.setAttribute("uid", result.getUid());
		// 将登录结果中的username封装到session中
		session.setAttribute("username", result.getUsername());
		// 返回结果
		return new ResponseResult<>(SUCCESS, result);
	}
	
	@RequestMapping("change_password")
	public ResponseResult<Void> changePassword(
		@RequestParam("old_password") String oldPassword,
		@RequestParam("new_password") String newPassword, 
		HttpSession session) {
		// 从session中获取uid
		Integer uid = getUidFromSession(session);
		// 从session中获取username
		String username = getUsernameSession(session);
		// 调用业务层对象执行修改密码
		userService.changePassword(uid, oldPassword, newPassword, username);
		// 返回操作成功
		return new ResponseResult<>(SUCCESS);
	}
	
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
	
	/**
	 * 上传头像的文件夹名称
	 */
	public static final String UPLOAD_DIR = "upload";
	/**
	 * 上传头像的最大大小
	 */
	public static final long UPLOAD_AVATAR_MAX_SIZE = 1 * 1024 * 1024;
	/**
	 * 上传头像的文件类型
	 */
	public static final List<String> UPLOAD_AVATAR_TYPES = new ArrayList<>();
	
	static {
		UPLOAD_AVATAR_TYPES.add("image/jpg");
		UPLOAD_AVATAR_TYPES.add("image/png");
	}
	
	@PostMapping("change_avatar")
	public ResponseResult<String> changeAvatar(
		HttpServletRequest request, 
		@RequestParam("avatar") MultipartFile avatar, 
		HttpSession session) {
		// 检查是否选择了有效文件提交的请求
		if (avatar.isEmpty()) {
			// 抛出异常：FileEmptyException
			throw new FileEmptyException(
				"上传头像失败！未选择头像文件，或选择的文件为空！");
		}
		
		// 检查文件大小是否超标
		long size = avatar.getSize();
		if (size > UPLOAD_AVATAR_MAX_SIZE) {
			// 抛出异常：FileSizeException
			throw new FileSizeException(
				"上传头像失败！不允许使用超过" + UPLOAD_AVATAR_MAX_SIZE / 1024 + "KB的文件！");
		}
		
		// 检查文件类型是否在允许的范围之内
		String contentType = avatar.getContentType();
		if (!UPLOAD_AVATAR_TYPES.contains(contentType)) {
			// 抛出异常：FileTypeException
			throw new FileTypeException(
				"上传头像失败！不支持所提交的文件类型！允许的文件类型有：" + UPLOAD_AVATAR_TYPES);
		}
		
		// 确定保存到哪个文件夹
		String parentPath = request
				.getServletContext()
					.getRealPath(UPLOAD_DIR);
		File parent = new File(parentPath);
		if (!parent.exists()) {
			parent.mkdirs();
		}
		
		// 确定保存的文件名
		String originalFilename = avatar.getOriginalFilename();
		String suffix = "";
		int beginIndex = originalFilename.lastIndexOf(".");
		if (beginIndex != -1) {
			suffix = originalFilename.substring(beginIndex);
		}
		String child = UUID.randomUUID().toString() + suffix;
		
		// 确定保存到哪个文件
		File dest = new File(parent, child);
		// 保存头像文件
		try {
			avatar.transferTo(dest);
		} catch (IllegalStateException e) {
			// 抛出异常：FileStateException
			throw new FileStateException(
				"上传头像失败！所选择的文件已经不可用！");
		} catch (IOException e) {
			// 抛出异常：FileIOException
			throw new FileIOException(
				"上传头像失败！读写数据时出现错误！");
		}
		
		// 执行修改头像
		Integer uid = getUidFromSession(session);
		String username = session.getAttribute("username").toString();
		String avatarPath = "/" + UPLOAD_DIR + "/" + child;
		userService.changeAvatar(uid, avatarPath, username);
		
		// 返回
		ResponseResult<String> rr = new ResponseResult<>();
		rr.setState(SUCCESS);
		rr.setData(avatarPath);
		return rr;
	}
}





