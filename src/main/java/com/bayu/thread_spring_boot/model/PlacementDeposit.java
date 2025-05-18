package com.bayu.thread_spring_boot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "placement_deposit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fundCode;

    private String placementBankCashAccountNo;

    private String accountDebitNo;

    private LocalDate date;
}
