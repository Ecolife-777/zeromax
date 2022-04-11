package uz.wite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Link {
    private String uzbName;
    private String rusName;
    private String engName;
    private String link;
}
