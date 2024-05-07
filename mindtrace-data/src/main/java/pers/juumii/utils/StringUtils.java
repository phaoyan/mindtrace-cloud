package pers.juumii.utils;

import java.util.List;

public class StringUtils {

    public static String spacedTexts(List<String> texts){
        StringBuilder res = new StringBuilder();
        texts.forEach(text->res.append(text).append(" "));
        return res.toString();
    }
}
