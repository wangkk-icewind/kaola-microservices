package com.kaola.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaola.store.mapper.MasseurMapper;
import com.kaola.store.model.entity.Masseur;
import com.kaola.store.service.MasseurService;
import org.springframework.stereotype.Service;

/**
 * 技师服务实现
 *
 * @author Kaola Team
 */
@Service
public class MasseurServiceImpl extends ServiceImpl<MasseurMapper, Masseur> implements MasseurService {
}
