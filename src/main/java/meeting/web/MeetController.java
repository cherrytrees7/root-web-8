package meeting.web;


import meeting.common.api.CommonResult;
import meeting.component.MinioService;
import meeting.service.MeetService;
import meeting.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MeetController {

    @Autowired
    private MeetService meetService;

    @Autowired
    private MinioService minioService;

    @PostMapping("/login")
    public CommonResult login(@RequestBody Map<String, Object> requestData) {

        String username = (String) requestData.get("username");
        String password = (String) requestData.get("password");

        String token = meetService.login(username, password);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("name", username);
        System.out.println(token);

        return CommonResult.success(tokenMap);
    }

    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        } else {
            return principal.toString();
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {

        // Check file size, 5MB = 5 * 1024 * 1024
        if (file.getSize() > 5 * 1024 * 1024) {
            // File size exceeded the limit of 5MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("文件大小超过5MB，请选择一个小于5MB的文件！");
        }

        String username = getCurrentUsername();
        String path = type + "/" + username + "/" + file.getOriginalFilename();

        minioService.uploadFile(path, file);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/checkToken")
    public ResponseEntity checkToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        // If the Authorization header is null, return a 401 status code immediately.
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        token = token.replace("Bearer ", "");

        // Use JwtUtils to verify the JWT.
        if (JwtUtils.verifyToken(token)) {
            // If the JWT is valid, return a 200 status code.
            return ResponseEntity.ok().build();
        } else {
            // If the JWT is invalid, return a 401 status code.
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public CommonResult register(@RequestBody Map<String, Object> requestData) {
        String username = (String) requestData.get("username");
        String password = (String) requestData.get("password");
        String email = (String) requestData.get("email");
        String phone = (String) requestData.getOrDefault("phone", requestData.get("telePhone"));

        String token = meetService.register(username, password, email, phone);

        // 这里你可能想要基于meetService.login()的响应做进一步处理
        // 例如，你可能想要判断是否登陆成功，并基于此返回不同的HTTP响应

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("name", username);
        System.out.println(token);

        return CommonResult.success(tokenMap);
    }


}
