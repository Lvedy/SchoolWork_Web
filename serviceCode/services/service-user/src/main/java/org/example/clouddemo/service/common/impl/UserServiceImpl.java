package org.example.clouddemo.service.common.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.clouddemo.bean.common.entity.User;
import org.example.clouddemo.bean.common.param.LoginParam;
import org.example.clouddemo.bean.common.param.RegisterParam;
import org.example.clouddemo.bean.common.vo.LoginResultVo;
import org.example.clouddemo.bean.common.vo.UserAppVo;
import org.example.clouddemo.service.common.UserService;
import org.example.clouddemo.service.common.mapper.UserMapper;
import org.example.common.auth.JWTToken;
import org.example.common.auth.JWTUtils;
import org.example.common.auth.TokenService;
import org.example.common.bean.LoginUser;
import org.example.common.constants.RedisKey;
import org.example.common.exception.AppRunTimeException;
import org.example.common.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hzg
 * @since 2025-06-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private TokenService tokenService;

    @Override
    public IPage<User> selectPageVo(Page<User> page, User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
        queryWrapper.orderByDesc("create_time");
        return userMapper.selectPage(page, queryWrapper);
    }


    @Override
    public User selectOne(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public int create(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public int delete(String id) {
        return userMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterParam param) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, param.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user != null) {
            throw new AppRunTimeException("已存在用户");
        }

        User addUser = new User();
        addUser.setId(IdUtil.getSnowflake().nextId());
        addUser.setUsername(param.getUsername());
        //密码加密
        addUser.setPassword(passwordEncoder.encode(param.getPassword()));

        //新建用户
        int i = userMapper.insert(addUser);
        return addUser.getId();
    }

    @Override
    public LoginResultVo login(LoginParam param) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, param.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new AppRunTimeException("用户不存在!");
        }

        //对比密码
        // 第一个参数 原密码   ,第二个参数  加密保存的密码  , 如果匹配, 结果返回true
        boolean isMatch = passwordEncoder.matches(param.getPassword(), user.getPassword());

        if (!isMatch) {
            throw new AppRunTimeException("密码错误!");
        }


        LoginResultVo loginResultVo = new LoginResultVo();


        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setUserName(user.getUsername());
        loginUser.setUserType(LoginUser.USER_TYPE_APP);
        loginUser.setLoginType(LoginUser.LOGIN_TYPE_PWD);

        String uuid = tokenService.createToken(loginUser);

        String loginUserJson = JSON.toJSONString(loginUser);

        //可以把 放在jwt token中
        JWTToken jwtToken = JWTUtils.getJWT(loginUserJson, uuid, "user", "app");
        loginResultVo.setToken(jwtToken.getAccess_token());


        //把用户信息 放在 redis上
        String key = RedisKey.TOKEN("app", uuid);
        //保存7天
        RedisUtil.set(key, loginUserJson, 7 * RedisUtil.TIME_DAYS_S);

        return loginResultVo;
    }

    @Override
    public UserAppVo getInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new AppRunTimeException("找不到数据!");
        }
        UserAppVo userAppVo = BeanUtil.copyProperties(user, UserAppVo.class);

        return userAppVo;
    }
}
