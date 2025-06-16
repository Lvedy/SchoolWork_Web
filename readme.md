# 全网最猛贩猪人网站 - 用户注册登录系统

## 项目概述

这是一个完整的前后端分离的用户注册登录系统，包含前端网站和后端微服务架构。

## 系统架构

### 后端系统 (serviceCode)
- **网关服务** (gateway): 端口 9027，负责路由转发和统一入口
- **用户服务** (service-user): 处理用户注册、登录等功能
- **其他服务**: 产品服务、订单服务、推荐服务

### 前端系统 (frontend-website)
- 静态HTML页面
- CSS样式文件
- JavaScript功能模块

## 功能特性

### 注册功能
- 用户名和密码验证
- 密码确认验证
- MD5密码加密
- 实时错误提示
- 成功后自动跳转到登录页面

### 登录功能
- 用户名和密码验证
- MD5密码加密
- Token存储到localStorage
- 成功后跳转到首页

## API接口

### 注册接口
- **URL**: `http://localhost:9027/api-service-user/api/app/user/register`
- **方法**: POST
- **请求头**: 
  - `Content-Type: application/json`
  - `lang: zh`
- **请求体**:
  ```json
  {
    "username": "用户名",
    "password": "MD5加密后的密码"
  }
  ```
- **返回格式**:
  ```json
  {
    "code": 1,
    "data": 用户ID,
    "msg": "成功信息",
    "success": true
  }
  ```

### 登录接口
- **URL**: `http://localhost:9027/api-service-user/api/app/user/login`
- **方法**: POST
- **请求头**: 
  - `Content-Type: application/json`
  - `lang: zh`
- **请求体**:
  ```json
  {
    "username": "用户名",
    "password": "MD5加密后的密码"
  }
  ```
- **返回格式**:
  ```json
  {
    "code": 1,
    "data": {
      "token": "JWT令牌",
      "userInfo": "用户信息"
    },
    "msg": "成功信息",
    "success": true
  }
  ```

## 文件结构

```
├── frontend-website/
│   └── src/com/lvedy/
│       ├── html/
│       │   ├── register.html    # 注册页面
│       │   ├── login.html       # 登录页面
│       │   └── first.html       # 首页
│       ├── CSS/
│       │   ├── forms.css        # 表单样式
│       │   └── first_h.css      # 首页样式
│       └── JavaScript/
│           ├── register.js      # 注册功能
│           └── login.js         # 登录功能
└── serviceCode/
    ├── gateway/                 # 网关服务
    └── services/
        └── service-user/        # 用户服务
```

## 使用说明

### 启动后端服务
1. 确保Nacos服务注册中心运行在 `localhost:8848`
2. 启动网关服务 (端口: 9027)
3. 启动用户服务

### 访问前端页面
1. 打开浏览器访问注册页面: `register.html`
2. 填写用户名和密码进行注册
3. 注册成功后会自动跳转到登录页面
4. 使用注册的账号进行登录

## 安全特性

- 密码使用MD5加密传输
- 网关层面的路由保护
- JWT Token认证机制
- 前端表单验证
- 后端参数验证

## 错误处理

系统提供完善的错误处理机制：
- 网络连接错误提示
- 表单验证错误提示
- 服务器错误提示
- 用户友好的错误消息显示

## 响应式设计

前端页面支持响应式设计，适配不同屏幕尺寸的设备。

## 注意事项

1. 确保后端服务正常运行
2. 网关端口默认为9027，如有修改请同步更新前端JavaScript文件中的API地址
3. 密码长度至少6位
4. 用户名不能为空