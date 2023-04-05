package pers.juumii.service;

import pers.juumii.data.Knode;
import pers.juumii.data.Label;

import java.util.List;

public interface KnodeQueryService {

    Knode check(Long knodeId);

    List<Knode> checkByLabel(String labelName);

    // 返回包括完整子节点信息的knode
    Knode checkFully(Long knodeId);

    List<Knode> checkByTitle(Long userId, String title);

    List<Knode> branches(Long knodeId);

    List<Knode> offsprings(Long knodeId);

    // 返回一个节点下的所有叶子节点
    List<Knode> leaves(Long knodeId);

    Knode stem(Long knodeId);

    List<Knode> ancestors(Long knodeId);

    List<Knode> knodeChain(Long knodeId);

    Knode findRoot(Long knodeId);

    String chainStyleTitle(Long knodeId);

    List<Knode> checkAll(Long userId);

    List<Knode> checkAllIncludingDeleted(Long userId);
}
