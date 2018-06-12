package com.fancydsp.data.dao.report;

import com.fancydsp.data.domain.User;
import org.apache.ibatis.annotations.Select;


public interface UserMapper {
    @Select("select 'test' name,'test' password ")
    User getUserById(int userId);

}
