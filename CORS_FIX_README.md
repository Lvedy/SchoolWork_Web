# CORS跨域问题解决方案

## 问题描述
前端访问后端API时出现CORS跨域错误：
```
Access to fetch at 'http://localhost:9027/api-service-user/api/app/user/login' from origin 'http://localhost:63342' has been blocked by CORS policy
```

### 最新问题：重复的CORS头
```
The 'Access-Control-Allow-Origin' header contains multiple values '*, *', but only one is allowed.
```
**原因：** 多个CORS配置同时生效，导致重复设置响应头。
**解决：** 删除重复的CORS配置，只保留一个配置源。

## 编译错误修复

### 问题：找不到符号 addAllowedOriginPattern
**错误信息：**
```
java: 找不到符号
符号: 方法 addAllowedOriginPattern(java.lang.String)
位置: 类型为org.springframework.web.cors.CorsConfiguration的变量 config
```

**原因：** Spring Boot 2.3.12版本中，`addAllowedOriginPattern`方法可能不可用或命名不同。

**解决方案：** 使用`addAllowedOrigin`方法，并调整相关配置以确保兼容性。

## 解决方案

已为网关添加了完整的CORS跨域支持，包括以下三个层面的配置：

### 1. Java配置类 (CorsConfig.java)
- 位置：`serviceCode/gateway/src/main/java/org/example/gateway/config/CorsConfig.java`
- 功能：创建CorsWebFilter Bean，配置CORS策略
- **重要修改：**
  - 使用`addAllowedOrigin("*")`替代`addAllowedOriginPattern`
  - 设置`allowCredentials(false)`（使用通配符*时的要求）
  - 明确指定允许的HTTP方法

### 2. ~~全局过滤器 (CorsGlobalFilter.java)~~ **已删除**
- ~~位置：`serviceCode/gateway/src/main/java/org/example/gateway/filter/CorsGlobalFilter.java`~~
- **删除原因：** 与CorsConfig配置冲突，导致重复的CORS响应头
- **解决方案：** 统一使用CorsConfig进行CORS配置

### 3. 配置文件 (application.yml)
- 位置：`serviceCode/gateway/src/main/resources/application.yml`
- 功能：Spring Cloud Gateway的全局CORS配置
- **重要修改：**
  - 使用`allowedOrigins`替代`allowedOriginPatterns`
  - 明确列出允许的HTTP方法
  - 设置`allowCredentials: false`

## 版本兼容性说明

### 当前环境
- **Spring Boot版本：** 2.3.12.RELEASE
- **Spring Cloud版本：** Hoxton.SR12
- **Java版本：** 8

### CORS配置要点
1. **通配符限制：** 当使用`allowedOrigins: "*"`时，必须设置`allowCredentials: false`
2. **方法兼容性：** 使用`addAllowedOrigin`而非`addAllowedOriginPattern`
3. **明确方法列表：** 避免使用`"*"`，明确列出需要的HTTP方法

## 重启步骤

### 方法一：IDE重启
1. 在IDE中停止网关服务
2. 重新运行 `GatewayApplication.java`

### 方法二：命令行重启
1. 进入网关目录：
   ```bash
   cd serviceCode/gateway
   ```

2. 停止现有进程（如果有）

3. 重新编译并启动：
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

### 方法三：打包运行
1. 编译打包：
   ```bash
   cd serviceCode
   mvn clean package -DskipTests
   ```

2. 运行网关：
   ```bash
   java -jar gateway/target/gateway-*.jar
   ```

## 验证步骤

1. **检查编译**
   - 确认没有编译错误
   - 确认所有CORS相关类正常编译

2. **检查网关启动日志**
   - 确认没有CORS相关错误
   - 确认端口9027正常监听

3. **测试前端登录**
   - 打开浏览器开发者工具
   - 尝试登录操作
   - 检查Network标签页，确认：
     - OPTIONS预检请求返回200状态码
     - POST请求能正常发送
     - 响应头包含正确的CORS头信息

4. **检查响应头**
   应该包含以下CORS头：
   ```
   Access-Control-Allow-Origin: *
   Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
   Access-Control-Allow-Headers: *
   Access-Control-Allow-Credentials: false
   ```

## 配置说明

### CORS配置项解释
- `allowedOrigins: "*"`: 允许所有域名访问
- `allowedMethods`: 明确列出允许的HTTP方法
- `allowedHeaders: "*"`: 允许所有请求头
- `allowCredentials: false`: 不允许发送Cookie（使用*时的要求）
- `maxAge: 3600`: 预检请求缓存时间（1小时）

### 安全注意事项
在生产环境中，建议：
1. 将 `allowedOrigins` 设置为具体的前端域名
2. 限制 `allowedMethods` 为实际需要的HTTP方法
3. 根据需要限制 `allowedHeaders`
4. 如需发送Cookie，设置具体域名而非通配符

## 故障排除

### 编译错误

1. **找不到addAllowedOriginPattern方法**
   - 已修复：使用`addAllowedOrigin`方法
   - 确保Spring版本兼容性

2. **其他编译错误**
   ```bash
   # 清理并重新编译
   mvn clean compile
   ```

### 运行时错误

1. **检查网关是否正确重启**
   ```bash
   # 检查进程
   netstat -ano | findstr :9027
   ```

2. **检查配置是否生效**
   - 查看启动日志中是否有CORS相关配置加载信息
   - 使用curl测试OPTIONS请求：
     ```bash
     curl -X OPTIONS http://localhost:9027/api-service-user/api/app/user/login -H "Origin: http://localhost:63342" -v
     ```

3. **检查其他服务**
   - 确认用户服务正常运行
   - 确认Nacos注册中心正常

4. **浏览器缓存**
   - 清除浏览器缓存
   - 尝试无痕模式

## 测试命令

```bash
# 测试OPTIONS预检请求
curl -X OPTIONS \
  http://localhost:9027/api-service-user/api/app/user/login \
  -H "Origin: http://localhost:63342" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v

# 测试实际POST请求
curl -X POST \
  http://localhost:9027/api-service-user/api/app/user/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:63342" \
  -d '{"username":"test","password":"test123"}' \
  -v
```

## 版本升级建议

如果需要使用更新的CORS功能（如`addAllowedOriginPattern`），可以考虑升级Spring版本：

```xml
<!-- 升级到Spring Boot 2.4+版本 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
    <relativePath/>
</parent>
```

**注意：** 版本升级可能需要调整其他依赖和配置，请谨慎操作。

重启网关服务后，前端应该能够正常访问后端API，不再出现CORS跨域错误和编译错误。