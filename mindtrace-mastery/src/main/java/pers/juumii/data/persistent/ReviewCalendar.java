package pers.juumii.data.persistent;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReviewCalendar {

    private Long userId;
    private Map<String, List<Long>> calendar;

}
