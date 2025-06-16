package org.example.common.constants;

/**
 * @Author: hzg
 * @Date: 2021/8/9 10:03 上午
 */
public class RedisKey {


    public static final String SMS_CODE = "app-SMS_CODE:";

    public static final String IMG_CODE = "admin-IMG_CODE:"; //管理后台图片验证码
    public static final String IMG_CODE_APP = "app-IMG_CODE:"; //app图片验证码


    public static final String DOWNLOAD_KEY = "DOWNLOAD_KEY:"; //数据下载key

    public static final String WX_CALLBACK_FAIL = "wx_callback_fail"; //微信支付回调处理错误的内容 队列
    public static final String WX_CALLBACK_ALL = "wx_callback_all"; //微信支付回调 的内容 队列


    public static final String WX_ACCESS_TOKEN = "wx_accessToken";

    //邮箱注册验证码
    public static final String EMAIL_REGISTER_CODE = "email_register_code:";

    //手机注册验证码
    public static final String PHONE_REGISTER_CODE = "phone_register_code:";


    public static String TOKEN(String type, String uuid) {
        return type + "_TOKEN_" + ":" + uuid;
    }
    public static String USER_TOKEN_LIST(String type, String userId) {
        return type + "_USER_TOKEN_LIST" + ":" + userId;
    }



    public static String SMS_CODE(String type, String phone) {
        return "app-SMS_CODE:" + type + ":" + phone;
    }

    public static String SMS_CODE(String type, String countryCode, String phone) {
        return "app-SMS_CODE:" + type + ":" + countryCode + ":" + phone;
    }

    public static String EMAIL_CODE(String type, String email) {
        return "app-EMAIL_CODE:" + type + ":" + email;
    }


}
