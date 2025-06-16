package org.example.clouddemo.service.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.clouddemo.bean.common.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.clouddemo.bean.common.param.LoginParam;
import org.example.clouddemo.bean.common.param.RegisterParam;
import org.example.clouddemo.bean.common.vo.LoginResultVo;
import org.example.clouddemo.bean.common.vo.UserAppVo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hzg
 * @since 2025-06-06
 */

public interface UserService extends IService<User> {
    IPage selectPageVo(Page<User> page, User user);

    User selectOne(User user);

    int create(User user);

    int update(User user);

    int delete(String id);

    /**
     * @param param
     * @return 返回新增用户id
     */
    Long register(RegisterParam param);

    LoginResultVo login(LoginParam param);

    UserAppVo getInfo(Long userId);
}
