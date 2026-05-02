package com.kaola.marketing.service;

import com.kaola.marketing.model.dto.DispatchRequest;
import com.kaola.marketing.model.entity.CouponDispatch;
import com.kaola.marketing.model.vo.DispatchPreviewVO;
import java.util.List;

public interface CouponDispatchService {
    DispatchPreviewVO preview(DispatchRequest request);
    Long execute(DispatchRequest request, String operator);
    List<CouponDispatch> listByPage(int page, int size);
}
