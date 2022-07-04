package com.tim.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tim.common.utils.PageUtils;
import com.tim.common.utils.Query;
import com.tim.gulimall.product.dao.CategoryDao;
import com.tim.gulimall.product.entity.CategoryEntity;
import com.tim.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
//      查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
//      创建树形结构
        return entities.stream().filter(categoryEntity ->
                        categoryEntity.getParentCid() == 0
//      找到子菜单
        ).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, entities));
            return categoryEntity;
//      菜单排序
        }).sorted(Comparator.comparingInt(
                menu -> menu.getSort() == null ? 0 : menu.getSort()
        )).collect(Collectors.toList());
    }

    //  递归找到所有菜单子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream().filter(categoryEntity ->
                categoryEntity.getParentCid().equals(root.getCatId())
        ).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted(Comparator.comparingInt(
                menu -> menu.getSort() == null ? 0 : menu.getSort()
        )).collect(Collectors.toList());
    }

}