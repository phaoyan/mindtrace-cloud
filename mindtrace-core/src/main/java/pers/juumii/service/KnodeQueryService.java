package pers.juumii.service;

import pers.juumii.data.Knode;

import java.util.List;

public interface KnodeQueryService {

    Knode check(Long knodeId);

    List<Knode> checkByTitle(Long userId, String title);

    List<Knode> branches(Long knodeId);

    List<Knode> offsprings(Long knodeId);

    Knode stem(Long knodeId);

    List<Knode> ancestors(Long knodeId);

    Knode findRoot(Long knodeId);
}
