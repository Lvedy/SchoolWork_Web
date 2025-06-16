package org.example.common.utils;

import org.springframework.util.AntPathMatcher;

public class AntPathMatcherUtil {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 将 {xxx} 占位符转换为 Ant 风格的 *
    public static String convertPlaceholderToAntPattern(String path) {
        return path.replaceAll("\\{[^}]+}", "*");
    }

    // 检查路径是否匹配
    public static boolean matchPath(String pattern, String path) {
        String antPattern = convertPlaceholderToAntPattern(pattern);
        return PATH_MATCHER.match(antPattern, path);
    }
}