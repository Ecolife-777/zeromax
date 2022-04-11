package uz.wite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String username;
    private String phoneNumber;
    private String screenId;
    private String adName;
    private String fileId;
    private String description;

}
