package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.juumii.data.Resource;

import java.util.List;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

}
