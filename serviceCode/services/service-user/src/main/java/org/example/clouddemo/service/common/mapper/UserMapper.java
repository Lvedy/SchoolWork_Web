package org.example.clouddemo.service.common.mapper;

import org.example.clouddemo.bean.common.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* <p>
* 用户表 Mapper 接口
* </p>
*
* @author hzg
* @since 2025-06-06
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
