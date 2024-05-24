package meeting.Bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`root-user`")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("userName")
    private String userName;

    private String password;


}
