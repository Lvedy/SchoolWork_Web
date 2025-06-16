package org.example.common.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * MyBatis-Plus 代码生成器（优化版）
 */
public class CodeGenerator3 {

    // ====================== 数据库配置 ======================
    public static final String DATA_URL = "jdbc:sqlserver://localhost:1433;databaseName=lvedy;encrypt=false;trustServerCertificate=true";
    public static final String USER_NAME = "sa";
    public static final String PASSWORD = "lxlx.826";

    // ====================== 项目基础配置 ======================
    public static final String PARENT_PACKAGE = "org.example.clouddemo"; // 父包名
    public static final String PROJECT_PATH = System.getProperty("user.dir"); // 项目根路径
    public static final String FILE_SEPARATOR = File.separator; // 系统文件分隔符

    // ====================== 模块配置（可动态修改） ======================
    public static final String MODULE_NAME = "service-product"; // 模块名（如：system、order）
    public static final String PACKAGE_MODULE_NAME = "common"; // 包内模块
    public static final String MODULE_DIR = "services" + FILE_SEPARATOR + MODULE_NAME; // 模块路径（相对于项目根目录）

    // ====================== 代码生成路径配置 ======================
    private static final String ENTITY_PACKAGE = "bean." + PACKAGE_MODULE_NAME + ".entity";
    private static final String MAPPER_PACKAGE = "service." + PACKAGE_MODULE_NAME + ".mapper";
    private static final String SERVICE_PACKAGE = "service." + PACKAGE_MODULE_NAME;
    private static final String SERVICE_IMPL_PACKAGE = "service." + PACKAGE_MODULE_NAME + ".impl";
    private static final String CONTROLLER_PACKAGE = "controller";

    private static final String BASE_OUTPUT_DIR = PROJECT_PATH + FILE_SEPARATOR + MODULE_DIR + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "java"+ FILE_SEPARATOR+toPackagePath(PARENT_PACKAGE);
    private static final String MAPPER_XML_OUTPUT_DIR = PROJECT_PATH + FILE_SEPARATOR + MODULE_DIR + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "resources" + FILE_SEPARATOR + "mapper" + FILE_SEPARATOR + PACKAGE_MODULE_NAME;

    /**
     * 读取控制台输入
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.nextLine();
            if (StrUtil.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 初始化路径（自动创建不存在的目录）
        initOutputDirs();

        //TODO: 需要生成的表名
        String[] includes = new String[]{"t_product"};

       // String[] includes =scanner("需要生成的表名（逗号分隔）").split(",");

        // 生成器配置
        FastAutoGenerator.create(DATA_URL, USER_NAME, PASSWORD)
                .globalConfig(builder -> {
                    builder.author("hzg") // 作者
                            .enableSwagger() // 开启Swagger注解
                            .outputDir(BASE_OUTPUT_DIR) // 主代码输出目录
                            .disableOpenDir(); // 禁止生成后打开目录
                })
                .dataSourceConfig(builder -> builder
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            // 自定义类型转换（如小整数转Integer）
                            if (metaInfo.getJdbcType().TYPE_CODE == Types.SMALLINT) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder -> {
                    Map<OutputFile, String> pathInfo = new HashMap<>();
                    pathInfo.put(OutputFile.entity, BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(ENTITY_PACKAGE));
                    pathInfo.put(OutputFile.mapper, BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(MAPPER_PACKAGE));
                    pathInfo.put(OutputFile.service, BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(SERVICE_PACKAGE));
                    pathInfo.put(OutputFile.serviceImpl, BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(SERVICE_IMPL_PACKAGE));
                    pathInfo.put(OutputFile.controller, BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(CONTROLLER_PACKAGE));
                    pathInfo.put(OutputFile.xml, MAPPER_XML_OUTPUT_DIR); // Mapper XML单独存放

                    builder.parent(PARENT_PACKAGE) // 父包
                            .entity(ENTITY_PACKAGE) // Entity包路径
                            .mapper(MAPPER_PACKAGE) // Mapper包路径
                            .service(SERVICE_PACKAGE) // Service接口包路径
                            .serviceImpl(SERVICE_IMPL_PACKAGE) // Service实现包路径
                            .controller(CONTROLLER_PACKAGE) // Controller包路径
                            .pathInfo(pathInfo);
                })
                .strategyConfig(builder -> {
                    builder.addInclude(includes) // 动态输入表名
                            .addTablePrefix("t_", "c_") // 过滤表前缀
                            .entityBuilder()
                            .naming(NamingStrategy.underline_to_camel) // 表名转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel) // 字段名转驼峰
                            .enableLombok() // 启用Lombok
                            .enableTableFieldAnnotation() // 生成字段注解
                            .mapperBuilder()
                            .enableBaseResultMap() // 生成通用ResultMap
                            .enableBaseColumnList() // 生成通用ColumnList
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // 服务名格式：UserService
                            .controllerBuilder()
                            .enableRestStyle() // 开启Restful风格
                            .enableHyphenStyle(); // 驼峰转连字符（如：user-info -> UserInfoController）
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker模板
                .execute();
    }

    /**
     * 初始化输出目录（自动创建）
     */
    private static void initOutputDirs() {
        String[] dirs = {
                BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(ENTITY_PACKAGE),
                BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(MAPPER_PACKAGE),
                BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(SERVICE_PACKAGE),
                BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(SERVICE_IMPL_PACKAGE),
                BASE_OUTPUT_DIR + FILE_SEPARATOR + toPackagePath(CONTROLLER_PACKAGE),
                MAPPER_XML_OUTPUT_DIR
        };
        for (String dir : dirs) {
            FileSystemUtils.deleteRecursively(new File(dir)); // 先删除旧目录（避免残留）
            new File(dir).mkdirs(); // 创建新目录
        }
    }

    /**
     * 包名转路径（如：org.example.entity -> com/gemo/entity）
     */
    private static String toPackagePath(String packageName) {
        return packageName.replace(".", FILE_SEPARATOR);
    }
}