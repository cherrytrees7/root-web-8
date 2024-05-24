package meeting.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import meeting.Bean.User;
import meeting.Bean.UserUpload;
import meeting.mapper.UserMapper;
import meeting.mapper.UserUploadMappper;
import meeting.service.UserUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUploadServiceImpl extends ServiceImpl<UserUploadMappper, UserUpload> implements UserUploadService {



    @Autowired
    private UserUploadMappper userUploadMappper;

    @Override
    public void saveUploadInfo(UserUpload userUpload) {
        userUploadMappper.insert(userUpload);
    }

    @Override
    public List<UserUpload> findAllByUserName(String userName) {
        QueryWrapper<UserUpload> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", userName); // 假设数据库字段名称为userName，根据实际情况调整
        return list(queryWrapper);
    }
}
