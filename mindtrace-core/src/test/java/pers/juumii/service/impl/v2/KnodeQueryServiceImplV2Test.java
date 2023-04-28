package pers.juumii.service.impl.v2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.juumii.data.Knode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KnodeQueryServiceImplV2Test {

    @Autowired
    private KnodeQueryServiceImplV2 impl;

    @Test
    void check() {
        Knode check = impl.check(1645264741514162176L);
    }

    @Test
    void branches(){
        List<Knode> branches = impl.branches(1645260983757516800L);
    }

    @Test
    void offsprings(){
        List<Knode> offsprings = impl.offsprings(1645239592404447232L);
    }

    @Test
    void leaves(){
        List<Knode> leaves = impl.leaves(1645239592404447232L);
    }

    @Test
    void ancestors(){
        List<Knode> ancestors = impl.ancestors(1645256790988632064L);
    }

    @Test
    void knodeChain(){
        List<Knode> knodes = impl.knodeChain(1645256790988632064L);
    }

    @Test
    void findRoot(){
        Knode root = impl.findRoot(1645256790988632064L);
    }

    @Test
    void chainStyleTitle(){
        List<String> titles = impl.chainStyleTitle(1645256790988632064L);
    }

    @Test
    void checkAll(){
        List<Knode> knodes = impl.checkAll(0L);

    }
}