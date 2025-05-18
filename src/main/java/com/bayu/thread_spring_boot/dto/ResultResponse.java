package com.bayu.thread_spring_boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {

    private Integer totalSuccess;

    private Integer totalFailed;

    private List<ErrorMessageDTO> errorMessages;

}
