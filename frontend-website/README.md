# 🐷 全网最猛贩猪人网站

一个基于Java的静态Web服务器，用于部署前端网站项目。

## 📋 项目结构

```
frontend-website/
├── src/com/lvedy/
│   ├── SimpleWebServer.java    # Java Web服务器源码
│   ├── html/                   # HTML页面文件
│   │   ├── first.html         # 首页
│   │   ├── login.html         # 登录页
│   │   ├── register.html      # 注册页
│   │   └── dashboard.html     # 控制台页面
│   ├── CSS/                   # 样式文件
│   ├── JavaScript/            # JS脚本文件
│   └── image/                 # 图片资源
├── compile_and_run.bat        # 编译并直接运行
├── build_jar.bat             # 打包成JAR文件
├── run_jar.bat               # 运行JAR文件
└── README.md                 # 说明文档
```

## 🚀 快速启动

### 方法一：直接编译运行

1. 双击运行 `compile_and_run.bat`
2. 等待编译完成后，服务器将自动启动
3. 在浏览器中访问 `http://localhost:8080`

### 方法二：打包成JAR文件运行

1. 双击运行 `build_jar.bat` 创建JAR包
2. 双击运行 `run_jar.bat` 启动服务器
3. 在浏览器中访问 `http://localhost:8080`

## 🌐 访问地址

- **服务器地址**: `http://localhost:8080`
- **主页**: `http://localhost:8080/html/first.html`
- **登录页**: `http://localhost:8080/html/login.html`
- **注册页**: `http://localhost:8080/html/register.html`
- **控制台**: `http://localhost:8080/html/dashboard.html`

## 📦 JAR包部署

### 创建JAR包

```bash
# 运行打包脚本
build_jar.bat
```

这将创建一个名为 `pig-website.jar` 的可执行JAR文件，包含所有必要的资源。

### 运行JAR包

```bash
# 方法1：使用脚本
run_jar.bat

# 方法2：直接命令行
java -jar pig-website.jar
```

## ⚙️ 系统要求

- **Java**: JDK 8 或更高版本
- **操作系统**: Windows (脚本为Windows批处理文件)
- **端口**: 8080 (确保端口未被占用)

## 🔧 配置说明

### 端口配置

如需修改端口，请编辑 `SimpleWebServer.java` 文件中的 `PORT` 常量：

```java
private static final int PORT = 8080; // 修改为你需要的端口
```

### 文件路径配置

如需修改Web根目录，请编辑 `WEB_ROOT` 常量：

```java
private static final String WEB_ROOT = "src/com/lvedy";
```

## 🎯 功能特性

- ✅ 静态文件服务 (HTML, CSS, JS, 图片)
- ✅ 自动MIME类型识别
- ✅ UTF-8编码支持
- ✅ 404错误页面
- ✅ 根路径自动重定向
- ✅ 支持GIF动图显示
- ✅ 一键打包部署

## 🐛 故障排除

### 编译失败
- 确保已安装JDK并配置了JAVA_HOME环境变量
- 检查PATH环境变量是否包含Java bin目录

### 端口占用
- 检查8080端口是否被其他程序占用
- 使用 `netstat -ano | findstr :8080` 查看端口占用情况

### 文件访问问题
- 确保所有文件路径正确
- 检查文件权限设置

## 📞 联系方式

如有问题，请联系全网最猛贩猪人！🐷

---

**享受你的贩猪之旅！** 🚀🐷