package pers.juumii.service.impl.quiz.generation;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.persistent.QuizStrategy;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.mapper.QuizStrategyMapper;
import pers.juumii.service.QuizGenerationService;
import pers.juumii.service.QuizStrategyService;
import pers.juumii.service.impl.quiz.strategy.NullQuizStrategy;
import pers.juumii.service.impl.quiz.strategy.QuizStrategyData;
import pers.juumii.utils.DesignPatternUtils;
import pers.juumii.utils.SpringUtils;

import java.util.List;
import java.util.Map;

@Service
public class QuizGenerationServiceImpl implements QuizGenerationService {

    private final QuizStrategyMapper quizStrategyMapper;
    private final EnhancerClient enhancerClient;

    @Autowired
    public QuizGenerationServiceImpl(
            QuizStrategyMapper quizStrategyMapper,
            EnhancerClient enhancerClient) {
        this.quizStrategyMapper = quizStrategyMapper;
        this.enhancerClient = enhancerClient;
    }

    @Override
    public List<Long> getQuiz(Long knodeId) {
        LambdaQueryWrapper<QuizStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuizStrategy::getKnodeId, knodeId);
        QuizStrategy strategy = quizStrategyMapper.selectOne(wrapper);
        List<Long> quizList;
        try{
            quizList = DesignPatternUtils.route(
                    QuizStrategyService.class,
                    st -> st.canHandle(strategy))
                    .getQuiz(strategy);
        }catch (NullPointerException e){
            QuizStrategy prototype = QuizStrategy.prototype(knodeId, null);
            quizList = SpringUtils.getBean(NullQuizStrategy.class).getQuiz(prototype);
        }
        return quizList;
    }

    // 指定strategy以覆盖原有的strategy，例如原先已经指定了binding strategy，可以替换为online random
    @Override
    public List<Long> getQuiz(Long knodeId, String _strategy) {
        LambdaQueryWrapper<QuizStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuizStrategy::getKnodeId, knodeId);
        QuizStrategy strategy = quizStrategyMapper.selectOne(wrapper);
        // 实际覆盖发生在这里
        strategy.setQuizStrategy(_strategy);
        return DesignPatternUtils.route(
                QuizStrategyService.class,
                st -> st.canHandle(strategy))
                .getQuiz(strategy);
    }

    @Override
    public QuizStrategyData getQuizStrategy(Long knodeId) {
        LambdaQueryWrapper<QuizStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuizStrategy::getKnodeId, knodeId);
        return QuizStrategyData.data(quizStrategyMapper.selectOne(wrapper).getQuizStrategy());
    }

    @Override
    @Transactional
    public void setQuizStrategy(Long knodeId, String strategy) {
        LambdaUpdateWrapper<QuizStrategy> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(QuizStrategy::getKnodeId, knodeId);
        quizStrategyMapper.delete(wrapper);
        quizStrategyMapper.insert(QuizStrategy.prototype(knodeId, strategy));
    }
}
