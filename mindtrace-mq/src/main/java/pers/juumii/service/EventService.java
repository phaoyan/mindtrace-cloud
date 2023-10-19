package pers.juumii.service;


import pers.juumii.data.MessageListener;

import java.util.List;
import java.util.Map;

public interface EventService {
    void emit(String event, String data);
    void addListener(String event, String callback);
    void removeListener(Long id);
    Map<String, List<MessageListener>> getEventMap();

}
