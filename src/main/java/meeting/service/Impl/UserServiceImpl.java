package meeting.service.Impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import meeting.Bean.Meet;
import meeting.Bean.User;
import meeting.VO.UserVo;
import meeting.common.utils.JwtTokenUtil;
import meeting.convert.UserMapStructMapper;
import meeting.domain.AdminUserDetails;
import meeting.exception.InvalidPasswordException;
import meeting.exception.UserAlreadyExistsException;
import meeting.exception.UserNotFoundException;
import meeting.mapper.UserMapper;
import meeting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private final UserMapStructMapper userMapStructMapper = UserMapStructMapper.INSTANCE;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    public String register(UserVo userVo) {
        // 将UserVo转换为User实体
        User user = userMapStructMapper.voToEntity(userVo);

        // 检查用户是否已经存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserName, user.getUserName()); // 根据User实体的字段进行调整
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new UserAlreadyExistsException("用户已经存在");
        }


        userMapper.insert(user);

        return "注册成功";
    }


    @Override
    public String login(UserVo userVo) {
        // 使用用户名查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserName, userVo.getUserName());
        User user = userMapper.selectOne(queryWrapper);

        // 检查用户是否存在
        if (user == null) {
            throw new UserNotFoundException("用户不存在");
        }

        // 检查密码是否匹配
        if (!user.getPassword().equals(userVo.getPassword())) {
            throw new InvalidPasswordException("密码错误");
        }

        AdminUserDetails userDetails = AdminUserDetails.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorityList(CollUtil.toList("ROLE_USER"))  // 假设每个登录用户都获得ROLE_USER权限
                .build();

        // 登录成功，进行后续操作，例如生成令牌等
        // 这里返回成功消息，具体实现取决于你的应用需求
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public AdminUserDetails getAdminByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", username);  // Assuming username is represented by mail field in Meet class
        User meet = userMapper.selectOne(queryWrapper);
        if (meet != null) {
            return AdminUserDetails.builder()
                    .username(meet.getUserName())
                    .password(meet.getPassword())
                    .authorityList(CollUtil.toList("ROLE_USER"))  // You can customize the authority here
                    .build();
        }
        return null;
    }


    @Override
    public String updateUsername(String currentUsername, String newUsername) {
        // 首先，检查新的用户名是否已经被占用
        QueryWrapper<User> queryWrapperNewUsername = new QueryWrapper<>();
        queryWrapperNewUsername.lambda().eq(User::getUserName, newUsername);
        int count = this.count(queryWrapperNewUsername);
        if (count > 0) {
            // 如果新的用户名已经存在，则返回false表示更新失败
            throw new UserAlreadyExistsException("新的用户名已经存在");
        }

        // 然后，查找当前用户名对应的用户
        QueryWrapper<User> queryWrapperCurrentUsername = new QueryWrapper<>();
        queryWrapperCurrentUsername.lambda().eq(User::getUserName, currentUsername);
        User user = this.getOne(queryWrapperCurrentUsername);

        if (user == null) {
            // 如果找不到用户，则返回false表示更新失败
            throw new UserNotFoundException("当前用户名不存在");
        }

        // 更新用户名
        user.setUserName(newUsername);

        this.updateById(user);

        AdminUserDetails userDetails = AdminUserDetails.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorityList(CollUtil.toList("ROLE_USER"))  // 假设每个登录用户都获得ROLE_USER权限
                .build();

        return jwtTokenUtil.generateToken(userDetails);
    }



    public String changePassword(String username, String currentPassword, String newPassword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new UserNotFoundException("用户不存在");
        }

        // 使用 MD5 对当前密码进行加密并比对
        String encryptedCurrentPassword = DigestUtils.md5DigestAsHex(currentPassword.getBytes());
        if (!user.getPassword().equals(encryptedCurrentPassword)) {
            throw new InvalidPasswordException("当前密码错误");
        }

        // 对新密码使用 MD5 进行加密
        String encryptedNewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());

        System.out.println(encryptedNewPassword);

        // 更新用户密码
        user.setPassword(encryptedNewPassword);
//        userMapper.updateById(user);

        // 生成新的 JWT 令牌或进行其他必要的操作
        AdminUserDetails userDetails = AdminUserDetails.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorityList(CollUtil.toList("ROLE_USER")) // 假设每个用户都有ROLE_USER权限
                .build();

        return jwtTokenUtil.generateToken(userDetails);
    }




}
