package pers.juumii.data;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;
import pers.juumii.dto.KnodeDTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Node
@Getter
@Setter
public class Knode{

    @Id
    private Long id;
    @Property("index")
    private Integer index;
    @Property("title")
    private String title;
    @Property("createBy")
    private Long createBy;
    @Property("createTime")
    private LocalDateTime createTime;
    @Relationship(type = "TAG")
    private List<Label> labels;
    @Relationship(type = "STEM_FROM")
    private Knode stem;
    @JsonBackReference
    @Relationship(type = "BRANCH_TO")
    private List<Knode> branches;
    @Relationship(type = "CONNECT_TO")
    private List<Knode> connections;


    @Override
    public boolean equals(Object obj) {
        return obj instanceof Knode && ((Knode) obj).getId().equals(id);
    }

    public static Knode prototype(String title){
        Knode res = new Knode();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTitle(title);
        res.setLabels(new ArrayList<>());
        res.setCreateTime(LocalDateTime.now());
        res.setBranches(new ArrayList<>());
        res.setConnections(new ArrayList<>());
        return res;
    }

    public static Knode prototype(String title, Knode stem, Long userId){
        Knode res = prototype(title);
        res.setStem(stem);
        res.setCreateBy(userId);
        return res;
    }

    public static Knode sudoPrototype(String title, Long stemId, Long userId){
        Knode sudoStem = new Knode();
        sudoStem.setId(stemId);
        return prototype(title, sudoStem, userId);
    }

    public static Knode prototype(Map<String, Object> entity){
        Knode res = new Knode();
        res.setId(Convert.toLong(entity.get("id")));
        res.setIndex(Convert.toInt(entity.get("index")));
        res.setTitle(Convert.toStr(entity.get("title")));
        res.setCreateBy(Convert.toLong(entity.get("createBy")));
        res.setCreateTime(Convert.convert(LocalDateTime.class, entity.get("createTime")));
        return res;
    }

    public static KnodeDTO transfer(Knode knode) {
        if(knode == null || knode.getId() == null) return null;
        KnodeDTO res = new KnodeDTO();
        res.setId(knode.getId().toString());
        res.setTitle(knode.getTitle());
        res.setLabels(knode.getLabels().stream().map(Label::getName).toList());
        res.setCreateBy(knode.getCreateBy().toString());
        res.setCreateTime(knode.getCreateTime());
        res.setIndex(knode.getIndex());
        Opt.ifPresent(knode.getStem(), stem->res.setStemId(stem.getId().toString()));
        knode.setBranches(new ArrayList<>(knode.getBranches()));
        correctIndex(knode.getBranches());
        res.setBranchIds(knode.getBranches().stream()
                .sorted(Comparator.comparingInt(Knode::getIndex))
                .map(_knode->_knode.getId().toString())
                .collect(Collectors.toList()));
        res.setConnectionIds(knode.getConnections()
                .stream().map(_knode->_knode.getId().toString())
                .collect(Collectors.toList()));
        return res;
    }

    private static void correctIndex(List<Knode> branches) {
        branches.sort(Comparator.comparingInt(Knode::getIndex));
        for(int i = 0; i < branches.size(); i ++)
            branches.get(i).setIndex(i);
    }

    public static List<KnodeDTO> transfer(List<Knode> knodeList){
        return knodeList.stream().map(Knode::transfer).toList();
    }

}
