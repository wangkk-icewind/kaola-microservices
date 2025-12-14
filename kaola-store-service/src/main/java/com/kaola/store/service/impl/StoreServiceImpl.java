package com.kaola.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaola.store.mapper.StoreMapper;
import com.kaola.store.model.entity.Store;
import com.kaola.store.model.vo.StoreVO;
import com.kaola.store.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 门店服务实现类 - 从数据库读取真实数据
 *
 * @author Kaola Team
 */
@Slf4j
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    @Override
    public List<StoreVO> getNearbyStores(BigDecimal lat, BigDecimal lng, String city) {
        log.info("获取附近门店, lat: {}, lng: {}, city: {}", lat, lng, city);

        // 如果有城市参数，优先按城市查询
        if (city != null && !city.isEmpty()) {
            return getStoresByCity(city);
        }

        // 查询所有营业中的门店
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getStatus, 1)
                    .eq(Store::getDeleted, 0);

        List<Store> stores = baseMapper.selectList(queryWrapper);
        return stores.stream().map(s -> convertToVO(s, lat, lng)).collect(Collectors.toList());
    }

    @Override
    public StoreVO getStoreDetail(Long storeId) {
        log.info("获取门店详情, storeId: {}", storeId);

        Store store = baseMapper.selectById(storeId);
        if (store == null) {
            return null;
        }
        return convertToVO(store, null, null);
    }

    @Override
    public List<StoreVO> getStoresByCity(String city) {
        log.info("按城市获取门店, city: {}", city);

        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Store::getCity, city)
                    .eq(Store::getStatus, 1)
                    .eq(Store::getDeleted, 0);

        List<Store> stores = baseMapper.selectList(queryWrapper);
        return stores.stream().map(s -> convertToVO(s, null, null)).collect(Collectors.toList());
    }

    /**
     * 将实体转换为VO
     */
    private StoreVO convertToVO(Store store, BigDecimal lat, BigDecimal lng) {
        StoreVO vo = new StoreVO();
        vo.setId(store.getId());
        vo.setName(store.getName());
        vo.setAddress(store.getAddress());
        vo.setPhone(store.getPhone());
        vo.setRating(new BigDecimal("4.8")); // 默认评分
        vo.setBusinessHours(store.getOpenTime() + "-" + store.getCloseTime());
        vo.setIsOpen(store.getStatus() == 1);
        vo.setMasseurCount(5); // 默认技师数量

        // 解析图片JSON获取第一张
        String images = store.getImages();
        if (images != null && images.startsWith("[") && images.contains("\"")) {
            String firstImage = images.split("\"")[1];
            vo.setImage(firstImage);
        }

        // 计算距离
        if (lat != null && lng != null && store.getLatitude() != null && store.getLongitude() != null) {
            double distance = calculateDistance(
                lat.doubleValue(), lng.doubleValue(),
                store.getLatitude().doubleValue(), store.getLongitude().doubleValue()
            );
            vo.setDistance(distance);
            vo.setDistanceText(formatDistance(distance));
        } else {
            vo.setDistance(0.0);
            vo.setDistanceText("--");
        }

        return vo;
    }

    /**
     * 计算两点之间的距离（单位：米）
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; // 地球半径，单位米
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    /**
     * 格式化距离显示
     */
    private String formatDistance(double distance) {
        if (distance < 1000) {
            return (int) distance + "m";
        } else {
            return String.format("%.1fkm", distance / 1000);
        }
    }
}
