package pers.juumii.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.juumii.data.Permission;
import pers.juumii.data.Role;
import pers.juumii.data.User;

import java.util.List;

@Mapper
public interface UserMapper extends MPJBaseMapper<User> {
    List<Permission> getPermissions(Long id);
    List<Role> getRoles(Long id);
}
