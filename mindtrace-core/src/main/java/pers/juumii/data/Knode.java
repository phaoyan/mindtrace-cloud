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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Property("privacy")
    private String privacy;
    // 此处的isLeaf并不一定是说它为叶子节点，而是说其承载了具体的知识
    // 只有非叶子节点这个属性才可能生效
    @Property("isLeaf")
    private Boolean isLeaf;
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
        res.setPrivacy("private");
        res.setIsLeaf(false);
        res.setBranches(new ArrayList<>());
        res.setConnections(new ArrayList<>());
        return res;
    }

    public static Knode prototype(String title, Knode stem){
        Knode res = prototype(title);
        res.setStem(stem);
        return res;
    }

    public static Knode prototype(Map<String, Object> entity){
        Knode res = new Knode();
        res.setId(Convert.toLong(entity.get("id")));
        res.setIndex(Convert.toInt(entity.get("index")));
        res.setTitle(Convert.toStr(entity.get("title")));
        res.setPrivacy(Convert.toStr(entity.get("privacy")));
        res.setCreateTime(Convert.convert(LocalDateTime.class, entity.get("createTime")));
        res.setIsLeaf(Convert.toBool(entity.get("deleted")));
        return res;
    }

    public static KnodeDTO transfer(Knode knode) {
        KnodeDTO res = new KnodeDTO();
        res.setId(knode.getId().toString());
        res.setTitle(knode.getTitle());
        res.setLabels(knode.getLabels());
        res.setCreateTime(knode.getCreateTime());
        res.setIsLeaf(knode.getIsLeaf());
        res.setIndex(knode.index);
        Opt.ifPresent(knode.getStem(), stem->res.setStemId(stem.getId().toString()));
        res.setBranchIds(knode.getBranches()
                .stream().map(_knode->_knode.getId().toString())
                .collect(Collectors.toList()));
        res.setConnectionIds(knode.getConnections()
                .stream().map(_knode->_knode.getId().toString())
                .collect(Collectors.toList()));
        return res;
    }

    public static List<KnodeDTO> transfer(List<Knode> knodeList){
        return knodeList.stream().map(Knode::transfer).toList();
    }

}
