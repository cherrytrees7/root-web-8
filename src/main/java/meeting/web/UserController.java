package meeting.web;


import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import meeting.Bean.User;
import meeting.Bean.UserUpload;
import meeting.VO.PasswordChangeRequest;
import meeting.VO.UpdateUsernameRequest;
import meeting.VO.UserVo;
import meeting.common.api.CommonResult;
import meeting.component.MinioService;
import meeting.convert.UserMapStructMapper;
import meeting.service.Impl.UserServiceImpl;
import meeting.service.Impl.UserUploadServiceImpl;
import meeting.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MinioService minioService;


    @Autowired
    private UserUploadServiceImpl userUploadService;


    private final UserMapStructMapper userMapStructMapper = UserMapStructMapper.INSTANCE;

    @PostMapping("/register")
    public CommonResult register(@Valid @RequestBody UserVo userVo) {
        String register = userService.register(userVo);
        return CommonResult.success(register);
    }

    @PostMapping("/login")
    public CommonResult Login(@RequestBody UserVo userVo) {


        String login = userService.login(userVo);
        return CommonResult.success(login);
    }



//    @PostMapping("/upload")
//    public CommonResult upload(@RequestParam("file") MultipartFile file,
//                               @RequestParam("type") String type) {
//        try {
//            // 生成文件的存储路径，这里我们使用文件类型和原始文件名作为路径
//            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            String filePath = type + "/" + fileName;
//
//            // 使用 MinioService 上传文件
//            minioService.uploadFile(filePath, file);
//
//            // 构建文件的访问URL
//            String fileUrl = minioService.getEndpoint() + "/" + minioService.getBucketName() + "/" + filePath;
//
//            String currentUsername = SecurityUtils.getCurrentUsername();
//            // 创建 UserUpload 实例并设置属性
//            UserUpload userUpload = new UserUpload();
//            userUpload.setUserName(currentUsername); // 设置为固定的用户名
//            userUpload.setMinioUrl(fileUrl); // fileUrl 从之前的上传逻辑中获得
//            userUpload.setCategory(type);
//            userUpload.setImageNumber("7"); // 设置为固定的图片编号
//            userUpload.setUploadTime(new Date()); // 设置当前时间为上传时间
//
//            // 调用 service 层方法保存上传信息
//            userUploadService.saveUploadInfo(userUpload);
//
//            // 返回成功响应，包含文件的访问URL
//            return CommonResult.success(fileUrl);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return CommonResult.failed("File upload and save info failed.");
//        }
//    }

    @PostMapping("/upload")
    public CommonResult upload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = type + "/" + fileName;

            // 使用 MinioService 上传文件，并直接获取文件URL
            String fileUrl = minioService.uploadFile(filePath, file);

            // 调用 Flask 应用进行图片处理
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("fileUrl", fileUrl);
            map.add("type", type);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            // Flask 应用的URL
            String flaskAppUrl = "http://localhost:8080/predict";
            ResponseEntity<String> response = restTemplate.postForEntity(flaskAppUrl, request, String.class);

            // 解析Flask应用返回的预测图片URL
            String responseBody = response.getBody();
            System.out.println("99999999999999999999");
            System.out.println(responseBody);
            cn.hutool.json.JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
            String predictedUrl = jsonResponse.getStr("predictedUrl");

            // 获取当前用户名（这里假设你已实现SecurityUtils.getCurrentUsername()）
            String currentUsername = SecurityUtils.getCurrentUsername();

            // 创建 UserUpload 实例并保存上传信息
            UserUpload userUpload = new UserUpload();
            userUpload.setUserName(currentUsername);
            userUpload.setMinioUrl(fileUrl);
            userUpload.setCategory(type);
            String uniqueID = UUID.randomUUID().toString();
            userUpload.setImageNumber(uniqueID);
            userUpload.setUploadTime(new Date());
            userUpload.setRecognizedUrl(predictedUrl);
            userUploadService.saveUploadInfo(userUpload);

            // 返回成功响应
            return CommonResult.success(predictedUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed("File upload and save info failed.");
        }
    }



    @GetMapping("/get-user")
    public CommonResult getUserUploads() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            return CommonResult.failed("User is not authenticated.");
        }

        List<UserUpload> uploads = userUploadService.findAllByUserName(currentUsername);
        if (uploads != null && !uploads.isEmpty()) {
            return CommonResult.success(uploads);
        } else {
            return CommonResult.failed("No uploads found for the current user.");
        }
    }

    @GetMapping("/info")
    public CommonResult getUserInfo() {
        try {
            // 获取当前登录的用户名
            String currentUsername = SecurityUtils.getCurrentUsername();
            if (currentUsername == null) {
                // 用户未登录或认证失败
                return CommonResult.failed("User is not authenticated.");
            }

            return CommonResult.success(currentUsername);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed("Failed to retrieve user information.");
        }
    }


    @PostMapping("/updateUsername")
    public CommonResult updateUsername(@Valid @RequestBody UpdateUsernameRequest request) {

        System.out.println(request);
        String currentUsername = SecurityUtils.getCurrentUsername();
        String newUsername = request.getUsername();
        // 调用Service层方法来更新用户名，假设方法返回boolean值表示成功与否
        String token = userService.updateUsername(currentUsername, newUsername);


        return CommonResult.success(token);
    }

    @PostMapping("/change-password")
    public CommonResult  changePassword( @Valid @RequestBody PasswordChangeRequest request) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        String token = userService.changePassword(currentUsername, request.getCurrentPassword(), request.getPassword());
        return CommonResult.success(token);
    }

}
