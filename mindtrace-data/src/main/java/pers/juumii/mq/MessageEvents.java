package pers.juumii.mq;

import org.springframework.cloud.client.ServiceInstance;

public class MessageEvents {
    public static final String ADD_KNODE = "add knode";
    public static final String UPDATE_KNODE = "update knode";
    public static final String REMOVE_KNODE = "remove knode";
    public static final String ADD_ENHANCER = "add enhancer";
    public static final String REMOVE_ENHANCER = "remove enhancer";
    public static final String ADD_RESOURCE = "add resource";
    public static final String REMOVE_RESOURCE = "remove resource";
    public static final String ADD_DATA_TO_RESOURCE = "add data to resource";

    public static String buildUrl(ServiceInstance self, String postfix){
        return self.getUri().toString() + postfix;
    }
}
