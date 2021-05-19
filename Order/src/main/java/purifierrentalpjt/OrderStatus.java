package purifierrentalpjt;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name="OrderStatus_table")
public class OrderStatus {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;

}
