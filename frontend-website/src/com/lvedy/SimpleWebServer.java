package com.lvedy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SimpleWebServer {
    private static final int PORT = 8080;
    private static final String WEB_ROOT = "src/com/lvedy";
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // 设置根路径处理器
        server.createContext("/", new RootHandler());
        // 设置静态文件处理器
        server.createContext("/html", new StaticFileHandler("html"));
        server.createContext("/CSS", new StaticFileHandler("CSS"));
        server.createContext("/JavaScript", new StaticFileHandler("JavaScript"));
        server.createContext("/image", new StaticFileHandler("image"));
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("==================================================");
        System.out.println("🐷 全网最猛贩猪人网站服务器已启动！");
        System.out.println("🌐 访问地址: http://localhost:" + PORT);
        System.out.println("📁 Web根目录: " + new File(WEB_ROOT).getAbsolutePath());
        System.out.println("🚀 服务器运行在端口: " + PORT);
        System.out.println("⭐ 主页面: http://localhost:" + PORT + "/html/first.html");
        System.out.println("🔐 登录页面: http://localhost:" + PORT + "/html/login.html");
        System.out.println("📝 注册页面: http://localhost:" + PORT + "/html/register.html");
        System.out.println("📊 控制台: http://localhost:" + PORT + "/html/dashboard.html");
        System.out.println("==================================================");
        System.out.println("按 Ctrl+C 停止服务器");
    }
    
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // 根路径重定向到首页
            if ("/".equals(path)) {
                String response = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>重定向中...</title></head><body><script>window.location.href='/html/first.html';</script><p>正在跳转到首页...</p></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes("UTF-8"));
                os.close();
                return;
            }
            
            // 404页面
            String notFoundResponse = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>404 - 页面未找到</title></head><body><h1>404 - 页面未找到</h1><p>请访问 <a href='/html/first.html'>首页</a></p></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, notFoundResponse.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(notFoundResponse.getBytes("UTF-8"));
            os.close();
        }
    }
    
    static class StaticFileHandler implements HttpHandler {
        private final String directory;
        
        public StaticFileHandler(String directory) {
            this.directory = directory;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            
            // 构建文件路径
            Path filePath = Paths.get(WEB_ROOT, directory, fileName);
            File file = filePath.toFile();
            
            if (!file.exists() || file.isDirectory()) {
                // 文件不存在，返回404
                String notFoundResponse = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>404 - 文件未找到</title></head><body><h1>404 - 文件未找到</h1><p>文件 " + fileName + " 不存在</p></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFoundResponse.getBytes("UTF-8").length);
                OutputStream os = exchange.getResponseBody();
                os.write(notFoundResponse.getBytes("UTF-8"));
                os.close();
                return;
            }
            
            // 设置Content-Type
            String contentType = getContentType(fileName);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            
            // 读取并发送文件
            byte[] fileBytes = Files.readAllBytes(filePath);
            exchange.sendResponseHeaders(200, fileBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        }
        
        private String getContentType(String fileName) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            Map<String, String> mimeTypes = new HashMap<>();
            mimeTypes.put("html", "text/html; charset=UTF-8");
            mimeTypes.put("css", "text/css; charset=UTF-8");
            mimeTypes.put("js", "application/javascript; charset=UTF-8");
            mimeTypes.put("png", "image/png");
            mimeTypes.put("jpg", "image/jpeg");
            mimeTypes.put("jpeg", "image/jpeg");
            mimeTypes.put("gif", "image/gif");
            mimeTypes.put("ico", "image/x-icon");
            mimeTypes.put("svg", "image/svg+xml");
            
            return mimeTypes.getOrDefault(extension, "application/octet-stream");
        }
    }
}