package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.persistent.Milestone;

@Mapper
public interface MilestoneMapper extends BaseMapper<Milestone> {
}
