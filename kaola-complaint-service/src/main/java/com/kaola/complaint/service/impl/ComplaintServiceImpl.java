package com.kaola.complaint.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaola.complaint.model.dto.ComplaintDTO;
import com.kaola.complaint.model.entity.Complaint;
import com.kaola.complaint.model.vo.ComplaintVO;
import com.kaola.complaint.mapper.ComplaintMapper;
import com.kaola.complaint.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 投诉服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintMapper complaintRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createComplaint(Long userId, ComplaintDTO dto) {
        log.info("创建投诉, userId: {}, orderId: {}", userId, dto.getOrderId());

        Complaint complaint = new Complaint();
        complaint.setUserId(userId);
        complaint.setOrderId(dto.getOrderId());
        complaint.setType(dto.getType());
        complaint.setContent(dto.getContent());
        complaint.setStatus(0); // 待处理

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                complaint.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } catch (Exception e) {
                log.warn("序列化投诉图片失败", e);
            }
        }

        int rows = complaintRepository.insert(complaint);
        log.info("投诉创建成功, complaintId: {}", complaint.getId());
        return rows > 0;
    }

    @Override
    public List<ComplaintVO> getComplaintList(Long userId) {
        log.info("获取用户投诉列表, userId: {}", userId);

        List<Complaint> complaints = complaintRepository.selectList(
            new LambdaQueryWrapper<Complaint>()
                .eq(Complaint::getUserId, userId)
                .orderByDesc(Complaint::getCreateTime)
        );

        return complaints.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public ComplaintVO getComplaintDetail(Long complaintId) {
        log.info("获取投诉详情, complaintId: {}", complaintId);
        Complaint complaint = complaintRepository.selectById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉记录不存在");
        }
        return convertToVO(complaint);
    }

    private ComplaintVO convertToVO(Complaint complaint) {
        ComplaintVO vo = new ComplaintVO();
        vo.setId(complaint.getId());
        vo.setOrderId(complaint.getOrderId());
        vo.setType(complaint.getType());
        vo.setTypeText(getTypeText(complaint.getType()));
        vo.setContent(complaint.getContent());
        vo.setStatus(complaint.getStatus());
        vo.setStatusText(getStatusText(complaint.getStatus()));
        vo.setReply(complaint.getHandleResult());
        vo.setReplyTime(complaint.getHandleTime());
        vo.setCreateTime(complaint.getCreateTime());

        if (complaint.getImages() != null && !complaint.getImages().isEmpty()) {
            try {
                vo.setImages(objectMapper.readValue(complaint.getImages(),
                    new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                log.warn("反序列化投诉图片失败, complaintId: {}", complaint.getId(), e);
                vo.setImages(Collections.emptyList());
            }
        }

        return vo;
    }

    private String getTypeText(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "服务态度";
            case 2: return "服务质量";
            case 3: return "虚假宣传";
            case 4: return "收费问题";
            case 5: return "其他";
            default: return "未知";
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "已处理";
            case 3: return "已关闭";
            default: return "未知";
        }
    }
}
