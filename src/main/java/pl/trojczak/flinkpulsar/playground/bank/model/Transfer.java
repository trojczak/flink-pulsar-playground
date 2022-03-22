package pl.trojczak.flinkpulsar.playground.bank.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    private String source;
    private String target;
    private int amount;
    private Date date;
}