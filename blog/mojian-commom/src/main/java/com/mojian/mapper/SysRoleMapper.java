package com.mojian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mojian.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<Integer> getRoleMenus(Integer id);

    void deleteMenuByRoleId(@Param("ids") List<Integer> ids);

    void insertRoleMenus(@Param("id") Integer id, @Param("menuIds") List<Integer> menuIds);

    List<String> selectRolesByUserId(Object userId);

    List<String> selectRolesCodeByUserId(Object loginId);

    void deleteRoleByUserId(@Param("userIds") List<Integer> userId);

    void addRoleUser(@Param("userId") Integer userId, @Param("roleIds") List<Integer> roleIds);

}