package com.fancydsp.data.dao.job;

import com.fancydsp.data.domain.Job.OfflineSqlTask;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;
import java.util.Map;

public interface JobMapper {

    @Select("${sql}")
    List<Map<String,Object>> queryBySql(@Param("sql") String sql);


    @Results({
            @Result(
                    column = "id"
                    ,property = "rules"
                    ,many = @Many(select = "com.fancydsp.data.dao.job.JobMapper.fetchSqlJobRules" ,fetchType = FetchType.EAGER))
    })
    @Select("SELECT id,script,name,fields FROM fancy_report_task.report_sql WHERE id=#{id} ")
    OfflineSqlTask fetchSqlJobTask(@Param("id") long id);

    @Results({

            @Result(column = "sql_placeholder" ,property = "sqlPlaceholder")
            , @Result(column = "replace_value",property = "replaceValue")
            , @Result(column = "is_optional",property = "isOptional")
    }
    )
    @Select("SELECT id,sql_placeholder,replace_value,is_optional,param_type FROM fancy_report_task.sql_params WHERE sql_id=#{id}")
    List<OfflineSqlTask.Rule> fetchSqlJobRules(@Param("id") long id);



}
