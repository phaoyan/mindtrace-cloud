package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.Label;

import java.util.List;

@Mapper
public interface LabelMapper extends BaseMapper<Label> {

}
