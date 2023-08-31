package pers.juumii.service.impl.review;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.config.COSConfig;
import pers.juumii.data.persistent.ReviewCalendar;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ReviewService;
import pers.juumii.utils.TimeUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final COSClient cosClient;
    private final COSConfig cosConfig;
    private final CoreClient coreClient;

    @Autowired
    public ReviewServiceImpl(COSClient cosClient, COSConfig cosConfig, CoreClient coreClient) {
        this.cosClient = cosClient;
        this.cosConfig = cosConfig;
        this.coreClient = coreClient;
    }


    private ReviewCalendar getReviewCalendar(Long userId){
        try {
            String bucket = cosConfig.getReviewCalendarBucketName();
            String objKey = userId + "/calendar.json";
            COSObjectInputStream in = cosClient.getObject(bucket, objKey).getObjectContent();
            String utf8 = IoUtil.readUtf8(in);
            return JSONUtil.toBean(utf8, ReviewCalendar.class);
        }catch (CosClientException e){
            ReviewCalendar calendar = new ReviewCalendar();
            calendar.setCalendar(new HashMap<>());
            calendar.setUserId(userId);
            updateReviewCalendar(calendar);
            return calendar;
        }
    }

    private void updateReviewCalendar(ReviewCalendar calendar) {
        String bucket = cosConfig.getReviewCalendarBucketName();
        String objKey = calendar.getUserId() + "/calendar.json";
        // 去掉过去的日期
        Map<String, List<Long>> cleared = calendar.getCalendar().entrySet().stream()
                .filter(entry -> TimeUtils.parseToDate(entry.getKey()).isAfter(LocalDate.now()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        calendar.setCalendar(cleared);
        cosClient.putObject(
                bucket, objKey,
                IoUtil.toStream(JSONUtil.toJsonStr(calendar), StandardCharsets.UTF_8),
                new ObjectMetadata());
    }

    @Override
    public void addReviewSchedule(Long knodeId, Long next) {
        KnodeDTO knode = coreClient.check(knodeId);
        Long userId = Convert.toLong(knode.getCreateBy());
        ReviewCalendar calendar = getReviewCalendar(userId);
        LocalDate date = LocalDate.now().plus(next, ChronoUnit.DAYS);
        String dateStr = TimeUtils.format(date);
        if(!calendar.getCalendar().containsKey(dateStr))
            calendar.getCalendar().put(dateStr, new ArrayList<>());
        calendar.getCalendar().get(dateStr).add(knodeId);
        updateReviewCalendar(calendar);
    }



    @Override
    public void removeReviewSchedule(Long knodeId, String date) {
        KnodeDTO knode = coreClient.check(knodeId);
        Long userId = Convert.toLong(knode.getCreateBy());
        ReviewCalendar calendar = getReviewCalendar(userId);
        if(calendar.getCalendar().containsKey(date))
            calendar.getCalendar().get(date).remove(knodeId);
        updateReviewCalendar(calendar);
    }

    @Override
    public List<String> getReviewKnodeIds(Long rootId, String date) {
        KnodeDTO knode = coreClient.check(rootId);
        Long userId = Convert.toLong(knode.getCreateBy());
        ReviewCalendar calendar = getReviewCalendar(userId);
        List<Long> selected = calendar.getCalendar().getOrDefault(date, new ArrayList<>());
        List<String> res = new ArrayList<>();
        for(Long knodeId: selected)
            if(coreClient.isOffspring(knodeId, rootId))
                res.add(knodeId.toString());
        return res;
    }

    @Override
    public void ackReview(Long knodeId, Long next) {
        KnodeDTO knode = coreClient.check(knodeId);
        if(!knode.getBranchIds().isEmpty()) return;
        removeReviewSchedule(knodeId, TimeUtils.format(LocalDate.now()));
        addReviewSchedule(knodeId, next);
    }

    @Override
    public void startReviewMonitor(Long rootId) {
        KnodeDTO knode = coreClient.check(rootId);
        Long userId = Convert.toLong(knode.getCreateBy());
        String bucket = cosConfig.getReviewCalendarBucketName();
        String objKey = userId + "/monitor.json";
        List<Long> monitorList;
        try {
            String utf8 = IoUtil.readUtf8(cosClient.getObject(bucket, objKey).getObjectContent());
            monitorList = JSONUtil.toList(utf8, Long.class);
        }catch (CosClientException e){
            monitorList = new ArrayList<>();
        }
        if(monitorList.contains(rootId)) return;
        monitorList.add(rootId);
        InputStream in = IoUtil.toStream(JSONUtil.toJsonStr(monitorList), StandardCharsets.UTF_8);
        cosClient.putObject(bucket, objKey, in, new ObjectMetadata());
    }

    @Override
    public void finishReviewMonitor(Long rootId) {
        KnodeDTO knode = coreClient.check(rootId);
        Long userId = Convert.toLong(knode.getCreateBy());
        String bucket = cosConfig.getReviewCalendarBucketName();
        String objKey = userId + "/monitor.json";
        String utf8 = IoUtil.readUtf8(cosClient.getObject(bucket, objKey).getObjectContent());
        List<Long> monitorList = JSONUtil.toList(utf8, Long.class);
        monitorList.remove(rootId);
        InputStream in = IoUtil.toStream(JSONUtil.toJsonStr(monitorList), StandardCharsets.UTF_8);
        cosClient.putObject(bucket, objKey, in, new ObjectMetadata());
    }

    @Override
    public List<Long> getReviewMonitorList(Long userId) {
        String bucket = cosConfig.getReviewCalendarBucketName();
        String objKey = userId + "/monitor.json";
        List<Long> monitorList;
        try {
            String utf8 = IoUtil.readUtf8(cosClient.getObject(bucket, objKey).getObjectContent());
            monitorList = JSONUtil.toList(utf8, Long.class);
        }catch (CosClientException e){
            monitorList = new ArrayList<>();
        }
        return monitorList;
    }

    @Override
    public Boolean isKnodeMonitored(Long knodeId) {
        KnodeDTO knode = coreClient.check(knodeId);
        if(knode == null) return false;
        Long userId = Convert.toLong(knode.getCreateBy());
        return getReviewMonitorList(userId).contains(knodeId);
    }
}
