package com.solid.subscribe.web.dao;

import com.solid.subscribe.web.vo.CountryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author fanyongju
 * @Title: CountryDao
 * @date 2018/12/6 15:02
 */
@Mapper
public interface CountryDao {
    @Select("select code, name from t_country")
    List<CountryVO> selectAll();
}
