package com.solid.subscribe.web.service;

import com.solid.subscribe.web.dao.CountryDao;
import com.solid.subscribe.web.vo.CountryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanyongju
 * @Title: CountryService
 * @date 2018/12/6 15:02
 */
@Service
public class CountryService {
    @Autowired
    private CountryDao countryDao;

    public List<CountryVO> selectAllCountry(){
        return countryDao.selectAll();
    }

}
