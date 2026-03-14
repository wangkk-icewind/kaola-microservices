package com.kaola.product.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应VO
 *
 * @author Kaola Team
 */
@Data
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    public PageVO() {
    }

    public PageVO(Long current, Long size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
        this.pages = (total + size - 1) / size;
        this.hasPrevious = current > 1;
        this.hasNext = current < pages;
    }

    /**
     * 从MyBatis Plus的Page对象构建PageVO
     */
    public static <T> PageVO<T> of(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setCurrent(page.getCurrent());
        pageVO.setSize(page.getSize());
        pageVO.setTotal(page.getTotal());
        pageVO.setPages(page.getPages());
        pageVO.setRecords(page.getRecords());
        pageVO.setHasPrevious(page.hasPrevious());
        pageVO.setHasNext(page.hasNext());
        return pageVO;
    }

    /**
     * 从MyBatis Plus的IPage对象构建PageVO
     */
    public static <T> PageVO<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setCurrent(page.getCurrent());
        pageVO.setSize(page.getSize());
        pageVO.setTotal(page.getTotal());
        pageVO.setPages(page.getPages());
        pageVO.setRecords(page.getRecords());
        pageVO.setHasPrevious(page.getCurrent() > 1);
        pageVO.setHasNext(page.getCurrent() < page.getPages());
        // 兼容list字段
        pageVO.setList(page.getRecords());
        return pageVO;
    }

    /**
     * 兼容旧版本的list字段
     */
    private List<T> list;

    public List<T> getList() {
        return list != null ? list : records;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
