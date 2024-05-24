package meeting.Bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user_uploads")
public class UserUpload {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("userName")
    private String userName;

    @TableField("minioUrl")
    private String minioUrl;

    private String category;

    @TableField("imageNumber")
    private String imageNumber;

    @TableField("uploadTime")
    private Date uploadTime;

    @TableField("recognizedUrl")
    private String recognizedUrl;

}
