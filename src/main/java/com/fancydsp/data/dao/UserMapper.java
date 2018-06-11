package com.fancydsp.data.dao;

import com.fancydsp.data.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    @Select("select 'test' name,'test' password ")
    User getUserById(int userId);

}
