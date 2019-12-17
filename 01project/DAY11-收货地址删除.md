### 40. 收货地址-删除-业务层

**1. 规划异常**

此次主要执行删除操作，则可能抛出`DeleteException`。

在执行操作之前，还应该检查数据是否存在和数据归属问题，则可能抛出`AddressNotFoundException`和`AccessDeniedException`。

同时，由于可能删除了默认收货地址，就可能需要再把另一条设置为默认，则可能抛出`UpdateException`。

以上4种异常中，需要新创建`DeleteException`。

**2. 抽象方法**

	void delete(Integer aid, Integer uid, String username) throws 4种异常;

**3. 实现功能**

首先，在`AddressServiceImpl`中添加持久层中的2个新方法，将它们私有化实现，其中，删除的方法需要获取返回值，并判断返回值是否是预期值，如果不是，则抛出异常。

然后，重写接口中的抽象方法：

具体实现为：

	@Override
	@Transactional
	public void delete(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedExcption, UpdateException, DeleteException {
		// 根据参数aid查询即将删除的数据：findByAid(aid)
		Address result = findByAid(aid);
		// 判断查询结果是否为null
		if (result == null) {
			// 是：AddressNotFoundException
			throw new AddressNotFoundException(
				"删除收货地址失败！尝试访问的数据不存在！");
		}
		
		// 判断查询结果中的uid和参数uid是否不一致（需要检查持久层的查询功能是否查询了uid）
		if (result.getUid() != uid) {
			// 是：AccessDeniedException
			throw new AccessDeniedExcption(
				"删除收货地址失败！访问被拒绝！");
		}

		// 执行删除：deleteByAid(aid)
		deleteByAid(aid);

		// 判断查询结果的isDefault是否为0：删除的不是默认收货地址，后续不需要补充操作，直接结束
		if (result.getIsDefault() == 0) {
			return;
		}
		
		// 表示删除了默认收货地址，则统计收货地址数量：countByUid(uid)
		Integer count = countByUid(uid);
		// 判断数量是否为0，是：刚才删除的是最后一条数据，后面没有数据了，则不需要补充操作
		if (count == 0) {
			return;
		}
		
		// 找出最后修改的收货地址：findLastModified(uid)
		Address lastModified = findLastModified(uid);
		// 获取该收货地址数据的aid：Integer lastModifiedAid = xx.getAid()
		Integer lastModifiedAid = lastModified.getAid();
		// 创建当前时间对象
		Date now = new Date();
		// 将该收货地址设置为默认：updateDefault(lastModifiedAid, username, now)
		updateDefault(lastModifiedAid, username, now);
	}

测试：

	@Test
	public void delete() {
		try {
			Integer aid = 22;
			Integer uid = 15;
			String username = "哈哈哈";
			service.delete(aid, uid, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

### 41. 收货地址-删除-控制器层

**1. 处理异常**

此次业务层抛了新的异常：`DeleteException`。

**2. 设计请求**

	请求路径：/addresses/{aid}/delete
	请求参数：HttpSession session
	请求类型：POST
	响应数据：ResponseResult<Void>

**3. 处理请求**

	@RequestMapping("/{aid}/delete")
	public ResponseResult<Void> delete(
		@PathVariable("aid") Integer aid,
		HttpSession session) {
		Integer uid=getUidFromSession(session);
		String username=session.getAttribute("username").toString();
		addressService.delete(aid, uid, username);
		return new ResponseResult<>(SUCCESS);
	}

### 41. 收货地址-删除-前端
		function deleteByAid(aid){
				$.ajax({
					"url" : "/addresses/"+aid+"/delete",
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


