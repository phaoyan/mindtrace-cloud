package pers.juumii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.persistent.StudyTimeDistribution;

@Mapper
public interface StudyTimeDistributionMapper extends BaseMapper<StudyTimeDistribution> {
}
