package pers.juumii.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pers.juumii.data.persistent.MilestoneTraceRel;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.mapper.MilestoneTraceRelMapper;
import pers.juumii.mapper.StudyTraceMapper;

import java.util.List;

@Component
public class StartingScripts implements ApplicationRunner {

    private final MilestoneTraceRelMapper relMapper;
    private final StudyTraceMapper studyTraceMapper;

    public StartingScripts(MilestoneTraceRelMapper mapper, StudyTraceMapper studyTraceMapper) {
        this.relMapper = mapper;
        this.studyTraceMapper = studyTraceMapper;
    }


    @Override
    public void run(ApplicationArguments args) {
        List<MilestoneTraceRel> rels = relMapper.selectList(null);
        for(MilestoneTraceRel rel : rels){
            StudyTrace trace = studyTraceMapper.selectById(rel.getTraceId());
            if(trace == null) continue;
            trace.setMilestoneId(rel.getMilestoneId());
            studyTraceMapper.updateById(trace);
        }
    }
}
