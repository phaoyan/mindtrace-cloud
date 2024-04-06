package pers.juumii.service.impl.exam.strategy.v2;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Service;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.data.temp.QuizResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamStrategyUtils {

    public List<QuizResult> extract(ExamSession session) {
        JSONObject cache = session.cache();
        JSONArray corrects = cache.getJSONArray("corrects");
        JSONArray mistakes = cache.getJSONArray("mistakes");
        List<QuizResult> res = new ArrayList<>();
        res.addAll(corrects.stream().map(knodeIdStr->{
            Long knodeId = Convert.toLong(knodeIdStr);
            return QuizResult.prototype(knodeId, -1L, null, 1.0);
        }).toList());
        res.addAll(mistakes.stream().map(knodeIdStr->{
            Long knodeId = Convert.toLong(knodeIdStr);
            return QuizResult.prototype(knodeId, -1L, null, 0.0);
        }).toList());
        return res;
    }
}
