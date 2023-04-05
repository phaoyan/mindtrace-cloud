package pers.juumii.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceType {
    String value();

    String QUIZCARD = "QUIZCARD";
    String MARKDOWN = "MARKDOWN";
    // 利用GPT学习需要prompts，GPT_PROMPTS存储这些prompts
    String GPT_PROMPTS = "GPT_PROMPTS";
    // 音频学习资料（如口语练习，听力练习资料）
    String AUDIO = "AUDIO";
    // 视频学习资料
    String VIDEO = "VIDEO";
    // 项目式学习需要一些“里程碑”，PROJECT文件记录这些里程碑数据
    String PROJECT = "PROJECT";
    // 一些资源仅靠url无法访问（如本地应用程序内的资源），
    // LINK_SCRIPT尝试用一系列脚本实现这类资源的一键访问
    String LINK_SCRIPT = "LINK_SCRIPT";
    // 一些例如B站视频合集的url资源，分p之间具有关联性，这些资源如果每一个resource都把整个url存起来会很麻烦
    // ASSOCIATED_RESOURCE用于将这些url整合起来便于访问
    String ASSOCIATED_RESOURCE = "ASSOCIATED_RESOURCE";
    // 将整个Ktree中部分节点的子节点隐藏起来，让用户回忆一个节点包含什么节点，这也是一种学习方式，
    // HIDDEN_KNODE_SET负责存储这些隐藏的节点
    String HIDDEN_KNODE_SET = "HIDDEN_KNODE_SET";

}
