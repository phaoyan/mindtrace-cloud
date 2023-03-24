package pers.juumii.data;


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
import java.util.stream.Collectors;

@Node
@Getter
@Setter
public class Knode{

    @Id
    private Long id;
    @Property("title")
    private String title;
    @Property("privacy")
    private String privacy;
    @Property("deleted")
    private Boolean deleted;
    @Property("createTime")
    private LocalDateTime createTime;
    @Relationship(type = "TAG")
    private List<Label> labels;
    @JsonBackReference
    @Relationship(type = "STEM_FROM")
    private Knode stem;
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
        res.setDeleted(false);
        res.setBranches(new ArrayList<>());
        res.setConnections(new ArrayList<>());
        return res;
    }

    public static Knode prototype(String title, Knode stem){
        Knode res = prototype(title);
        res.setStem(stem);
        return res;
    }

    public static KnodeDTO transfer(Knode knode) {
        KnodeDTO res = new KnodeDTO();
        res.setId(knode.getId());
        res.setTitle(knode.getTitle());
        res.setLabels(knode.getLabels()
                .stream().map(Label::transfer)
                .collect(Collectors.toList()));
        res.setCreateTime(knode.getCreateTime());
        res.setDeleted(knode.getDeleted());
        Opt.ifPresent(knode.getStem(), stem->res.setStemId(stem.getId()));
        res.setBranchIds(knode.getBranches()
                .stream().map(Knode::getId)
                .collect(Collectors.toList()));
        res.setConnectionIds(knode.getConnections()
                .stream().map(Knode::getId)
                .collect(Collectors.toList()));
        return res;
    }
}
