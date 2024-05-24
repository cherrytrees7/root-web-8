package meeting.service.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import meeting.Bean.Meet;
import meeting.common.api.ResultCode;
import meeting.common.utils.JwtTokenUtil;
import meeting.component.MinioService;
import meeting.domain.AdminUserDetails;
import meeting.exception.BusinessException;
import meeting.mapper.MeetMapper;
import meeting.service.MeetService;
import meeting.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;


@Service
public class MeetServiceImpl extends ServiceImpl<MeetMapper, Meet> implements MeetService {







    @Autowired
    private MeetMapper mapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//    @Override
//    public ResponseEntity login(Map map) {
//        Map<String, Object> data = (Map<String, Object>) map.get("data");
//
//        String name = (String) data.get("name");
//        String mail = (String) data.get("mail");
//        String telePhone = (String) data.get("telePhone");
//        System.out.println(name + mail + telePhone);
//        QueryWrapper<Meet> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("name", name)
//                .eq("mail", mail)
//                .eq("telePhone", telePhone);
//        Meet user = mapper.selectOne(queryWrapper);
//        System.out.println(user);
//        if (user != null) {
//            Map tokenMap = new HashMap();
//            tokenMap.put("mail", mail);
//            tokenMap.put("telePhone", telePhone);
//            String token = JwtUtils.getToken(tokenMap);
//            System.out.println(token+"----------------");
//            //6、构造返回值
//            Map retMap = new HashMap();
//            retMap.put("token", token);
//            return ResponseEntity.ok(token);
//        }
//
//
//        return ResponseEntity.ok("200");
//    }

    @Override
    public Meet check(String mail, String telePhone) {
        QueryWrapper<Meet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mail", mail)
                .eq("telePhone", telePhone);
        Meet user = mapper.selectOne(queryWrapper);

        return user;
    }


    @Override
    public AdminUserDetails getAdminByUsername(String username) {
        QueryWrapper<Meet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);  // Assuming username is represented by mail field in Meet class
        Meet meet = mapper.selectOne(queryWrapper);
        if (meet != null) {
            return AdminUserDetails.builder()
                    .username(meet.getName())
                    .password(meet.getPassword())
                    .authorityList(CollUtil.toList("ROLE_USER"))  // You can customize the authority here
                    .build();
        }
        return null;
    }

    @Override
    public String register(String username, String password, String email, String telePhone) {
        // 验证用户名是否已存在

        String token = null;

        QueryWrapper<Meet> nameCheckWrapper = new QueryWrapper<>();
        nameCheckWrapper.eq("name", username);
        if (mapper.selectCount(nameCheckWrapper) > 0) {
            throw new BusinessException(ResultCode.REPEATNAME);
        }

        // 创建并设置新的Meet对象
        Meet newUser = new Meet();
        newUser.setName(username);
        newUser.setPassword(password);  // 实际情况下，请确保密码被适当地加密
        newUser.setMail(email);
        newUser.setTelePhone(telePhone);

        mapper.insert(newUser);

        AdminUserDetails userDetails = AdminUserDetails.builder()
                .username(username)
                .password(password)
                .authorityList(CollUtil.toList("ROLE_ADMIN"))
                .build();

        // 使用新的 userDetails 对象创建身份验证令牌
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 使用新的 userDetails 对象生成令牌
        token = jwtTokenUtil.generateToken(userDetails);

        // 返回新用户的ID
        return token;


    }

    @Override
    public String login(String username, String password) {
        // 通过用户名（这里实际上是电子邮件）检查用户是否存在
        QueryWrapper<Meet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        Meet meet = mapper.selectOne(queryWrapper);

        // 如果用户不存在
        if (meet == null) {
            throw new BusinessException(ResultCode.NOUSER);
        }

        // 检查密码是否匹配。这里假设Meet中的密码是明文存储的。
        // 重要：在现实世界中，你永远不应该以明文形式存储密码。
        // 它们应该使用安全的哈希算法进行哈希，然后在认证过程中比较哈希值。
        if (!meet.getPassword().equals(password)) {
            throw new BusinessException(ResultCode.FAILED);
        }

        // 如果一切验证都通过，那么生成JWT令牌
        AdminUserDetails userDetails = AdminUserDetails.builder()
                .username(meet.getName())
                .password(meet.getPassword())
                .authorityList(CollUtil.toList("ROLE_USER"))  // 假设每个登录用户都获得ROLE_USER权限
                .build();

        return jwtTokenUtil.generateToken(userDetails);
    }

}
