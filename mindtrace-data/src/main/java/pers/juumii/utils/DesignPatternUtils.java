package pers.juumii.utils;

import java.util.List;
import java.util.function.Function;

public class DesignPatternUtils {

    public static  <ImplType, Rule extends Function<ImplType, Boolean>> ImplType route(Class<ImplType> cl, Rule rule){
        List<ImplType> impls = SpringUtils.getBeans(cl);
        return DataUtils.getIf(impls, rule);
    }
}
