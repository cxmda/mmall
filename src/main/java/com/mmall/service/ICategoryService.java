package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @author chenqiang
 * @create 2020-06-29 17:27
 */
public interface ICategoryService {

    ServerResponse<String> addCategory(String categoryName,Integer parentId);

    ServerResponse<String> setCategoryName(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
