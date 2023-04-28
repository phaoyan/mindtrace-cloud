package pers.juumii.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceType {
    String value();

    String QUIZCARD = "quizcard";
    String MARKDOWN = "markdown";
    // 外链：支持一切网上url，根据网站类型的不同，在后端mindtrace-spider-python模块会有不同的解析器，是一个比较综合的结构
    String LINKOUT = "linkout";
    // 完形填空
    String CLOZE = "cloze";
    // 该资源收集一个Knode的所有子节点的Quizcard
    // 应当支持一定的配置以供筛选quiz
    String QUIZCARD_COLLECTION = "quizcard collection";
    // 利用GPT学习需要prompts，GPT_PROMPTS存储这些prompts
    String GPT_PROMPTS = "gpt prompts";
    // 音频学习资料（如口语练习，听力练习资料）
    String AUDIO = "audio";
    // 视频学习资料
    String VIDEO = "video";
    // 项目式学习需要一些“里程碑”，PROJECT文件记录这些里程碑数据
    String PROJECT = "project";
    // 一些资源仅靠url无法访问（如本地应用程序内的资源），
    // LINK_SCRIPT尝试用一系列脚本实现这类资源的一键访问
    String LINK_SCRIPT = "link script";
    // 一些例如B站视频合集的url资源，分p之间具有关联性，这些资源如果每一个resource都把整个url存起来会很麻烦
    // ASSOCIATED_RESOURCE用于将这些url整合起来便于访问
    String ASSOCIATED_RESOURCE = "associated resource";
    // 将整个Ktree中部分节点的子节点隐藏起来，让用户回忆一个节点包含什么节点，这也是一种学习方式，
    // HIDDEN_KNODE_SET负责存储这些隐藏的节点
    String HIDDEN_KNODE_SET = "hidden knode set";
    String STROKE = "stroke";

}
