package com.bayu.thread_spring_boot.service.impl;

import com.bayu.thread_spring_boot.dto.CreatePlacementRequest;
import com.bayu.thread_spring_boot.dto.ErrorMessageDTO;
import com.bayu.thread_spring_boot.dto.ResultResponse;
import com.bayu.thread_spring_boot.exception.TooManyRequestException;
import com.bayu.thread_spring_boot.model.PlacementDeposit;
import com.bayu.thread_spring_boot.repository.PlacementDepositRepository;
import com.bayu.thread_spring_boot.service.PlacementDepositService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Setter
@Service
@RequiredArgsConstructor
public class PlacementDepositServiceImpl implements PlacementDepositService {

    private final PlacementDepositRepository placementDepositRepository;
    private long delayInMillis = 10000; // default 10 detik
    private final Semaphore semaphore = new Semaphore(1, true);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public CompletableFuture<ResultResponse> create(List<CreatePlacementRequest> createPlacementRequestList) {
        if (!semaphore.tryAcquire()) {
            CompletableFuture<ResultResponse> failed = new CompletableFuture<>();
            failed.completeExceptionally(new TooManyRequestException("Service sedang menangani request lain, mohon tunggu beberapa saat"));
            return failed;
        }

        return CompletableFuture.supplyAsync(() -> {
            ResultResponse resultResponse = new ResultResponse();
            int totalSuccess = 0;
            int totalFailed = 0;
            List<ErrorMessageDTO> errorMessages = new ArrayList<>();

            try {
                for (CreatePlacementRequest request : createPlacementRequestList) {
                    try {
                        LocalDate requestDate = LocalDate.parse(request.getDate());
                        if (!requestDate.isEqual(LocalDate.now())) {
                            totalFailed++;
                            errorMessages.add(new ErrorMessageDTO(request.getFundCode(), Collections.singletonList("Date tidak sesuai dengan current date")));
                            continue;
                        }

                        PlacementDeposit deposit = PlacementDeposit.builder()
                                .fundCode(request.getFundCode())
                                .placementBankCashAccountNo(request.getPlacementBankCashAccountNo())
                                .accountDebitNo(request.getAccountDebitNo())
                                .date(requestDate)
                                .build();

                        placementDepositRepository.save(deposit);
                        totalSuccess++;

                    } catch (DateTimeParseException e) {
                        totalFailed++;
                        errorMessages.add(new ErrorMessageDTO(request.getFundCode(), Collections.singletonList("Format tanggal tidak valid: " + request.getDate())));
                    } catch (Exception e) {
                        totalFailed++;
                        errorMessages.add(new ErrorMessageDTO(request.getFundCode(), Collections.singletonList("Gagal menyimpan data: " + e.getMessage())));
                    }
                }

                resultResponse.setTotalSuccess(totalSuccess);
                resultResponse.setTotalFailed(totalFailed);
                resultResponse.setErrorMessages(errorMessages);
                return resultResponse;

            } finally {
                semaphore.release(); // ensure always released
            }

        }, executor);
    }

}
