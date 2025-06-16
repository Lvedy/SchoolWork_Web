package org.example.common.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 工具类
 */
public class JWTUtils {

    private static final String JWT_KEY = "test";

    /**
     * 生成JWT
     *
     * @param body      body体
     * @param role      角色
     * @param sessionId 会话id
     * @param type      类型
     * @return 生成的JWT
     */
    public static JWTToken getJWT(String body, String sessionId, String role, String type) {
        Long expires_in = 1000 * 60 * 60 * 72L; //3天
        long time = System.currentTimeMillis();
        time = time + expires_in;
        JwtBuilder builder = Jwts.builder()
                .setId(sessionId) //设置唯一ID
                .setSubject(body) //设置内容，这里用JSON包含帐号信息
                .setIssuedAt(new Date()) //签发时间
                .setExpiration(new Date(time)) //过期时间
                .claim("roles", role) //设置角色
                .claim("type", type) //设置token类型
                .signWith(SignatureAlgorithm.HS256, generalKey()) //设置签名 使用HS256算法，并设置密钥
                ;
        String code = builder.compact();
        JWTToken token = JWTToken.builder()
                .access_token(code)
                .expires_in(expires_in / 1000)
                .token_type("JWT")
                .build();
        return token;
    }

    public static JWTToken getJWT(String body, String sessionId, String role, String type, Long expiresTime) {
//        Long expires_in = 1000 * 60 * 60 * 24L; //一天
        long time = System.currentTimeMillis();
        time = time + expiresTime;
        JwtBuilder builder = Jwts.builder()
                .setId(sessionId) //设置唯一ID
                .setSubject(body) //设置内容，这里用JSON包含帐号信息
                .setIssuedAt(new Date()) //签发时间
                .setExpiration(new Date(time)) //过期时间
                .claim("roles", role) //设置角色
                .claim("type", type) //设置token类型
                .signWith(SignatureAlgorithm.HS256, generalKey()) //设置签名 使用HS256算法，并设置密钥
                ;
        String code = builder.compact();
        JWTToken token = JWTToken.builder()
                .access_token(code)
                .expires_in(expiresTime / 1000)
                .token_type("JWT")
                .build();
        return token;
    }

    /**
     * 解析JWT
     *
     * @param jwt jwt
     * @return 解析结果
     */
    public static Claims parseJWT(String jwt) {
        Claims body = Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(jwt).getBody();
        return body;
    }

    /**
     * 刷新JWT
     *
     * @param jwt       jwt
     * @param sessionId 会话id
     * @return 刷新的结果 {@link JWTToken}
     */
    public static JWTToken refreshJWT(String jwt, String sessionId) {
        Claims claims = parseJWT(jwt);
        String body = claims.getSubject();
        String role = claims.get("roles").toString();
        String type = claims.get("type").toString();
        return getJWT(body, sessionId, role, type);
    }

    /**
     * 获取JWT信息
     *
     * @param jwt jwt
     * @return jwt信息
     */
    public static Claims infoJWT(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims;
    }

    /**
     * 验证JWT
     *
     * @param jwt jwt
     * @return 返回是否验证成功
     */
    public static boolean checkJWT(String jwt) {
        try {
            Claims body = Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(jwt).getBody();
            if (body != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return 生成加密后的秘钥 secretKey
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    public static void main(String[] args) {
//        JWTToken jwt = getJWT("{\"address\":\"\",\"alias\":\"\",\"avatar\":\"http://wework.qpic.cn/bizmail/fKsMCpWtG16LvfvdgAIicq28r2qU1pA00AqzK2Ct8mfUHY7cooG96wQ/0\",\"corpId\":\"ww2ff39dc714467941\",\"corpRoleId\":0,\"corporationId\":3,\"createTime\":1621825522,\"department\":\"[1]\",\"departmentIds\":\"1\",\"email\":\"\",\"extattr\":\"{\\\"attrs\\\":[]}\",\"externalPosition\":\"\",\"externalProfile\":\"{\\\"external_attr\\\":[],\\\"external_corp_name\\\":\\\"\\\"}\",\"followStatus\":1,\"gender\":1,\"id\":15,\"isDel\":0,\"isDisabled\":0,\"isLeaderInDept\":\"[0]\",\"mainDepartment\":1,\"mobile\":\"15626150762\",\"name\":\"潘泽优\",\"openUserid\":\"woSlQICwAAEjClhJAXAKfm-AxZe2j-aQ\",\"permissionsDepartmentIds\":\"\",\"position\":\"\",\"qrCode\":\"https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=vc1005f3a360a96930\",\"status\":1,\"telephone\":\"\",\"thumbAvatar\":\"http://wework.qpic.cn/bizmail/fKsMCpWtG16LvfvdgAIicq28r2qU1pA00AqzK2Ct8mfUHY7cooG96wQ/100\",\"updateTime\":0,\"userId\":\"30b7ecc49b374730945c8a745094e831\"}", "sadasdasd", "admin");
//
//        System.out.println(JSON.toJSONString(jwt));
//        System.out.println(jwt.getAccess_token());
//
        JSONObject object = new JSONObject();
        object.put("id", "1");
        object.put("username", "哈哈");

        JWTToken jwt = getJWT(object.toJSONString(), "111", "user", "app");

        String jwtStr = JSON.toJSONString(jwt);
        String jwtTokenStr = jwt.getAccess_token();


        System.out.println("jwtStr:" + jwtStr);
        System.out.println("jwtTokenStr:" + jwtTokenStr);

        System.out.println(infoJWT(jwtTokenStr));

    }
}
