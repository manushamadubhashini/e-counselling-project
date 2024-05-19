package lk.ijse.eCounselling.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Session {
    private String id;
    private String type;
    private Date date;
    private int duration;
    private String eid;
    private String pid;
}
