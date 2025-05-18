package com.bayu.thread_spring_boot.service;

import com.bayu.thread_spring_boot.dto.CreatePlacementRequest;
import com.bayu.thread_spring_boot.dto.ResultResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlacementDepositService {

    CompletableFuture<ResultResponse> create(List<CreatePlacementRequest> createPlacementRequestList);

}
