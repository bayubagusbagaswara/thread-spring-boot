package com.bayu.thread_spring_boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlacementRequest {

    private String fundCode;
    private String date;
    private String placementBankCashAccountNo;
    private String accountDebitNo;

}
