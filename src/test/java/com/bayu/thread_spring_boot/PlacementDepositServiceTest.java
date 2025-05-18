package com.bayu.thread_spring_boot;

import com.bayu.thread_spring_boot.dto.CreatePlacementRequest;
import com.bayu.thread_spring_boot.dto.ResultResponse;
import com.bayu.thread_spring_boot.exception.TooManyRequestException;
import com.bayu.thread_spring_boot.service.impl.PlacementDepositServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlacementDepositServiceTest {

    @Autowired
    private PlacementDepositServiceImpl service;

    @Test
    void testRejectConcurrentRequest() throws Exception {
        // Simulasikan delay pendek saat test
        service.setDelayInMillis(2000); // 2 detik untuk kecepatan test

        List<CreatePlacementRequest> requestList = List.of(
                new CreatePlacementRequest("FUND1", LocalDate.now().toString(), "123", "456")
        );

        // Request pertama - akan delay 2 detik
        CompletableFuture<ResultResponse> future1 = service.create(requestList);

        // Tunggu 100ms agar future1 sempat acquire semaphore
        Thread.sleep(100);

        // Request kedua - harus ditolak
        CompletableFuture<ResultResponse> future2 = service.create(requestList);

        assertTrue(future2.isCompletedExceptionally());

        // Pastikan exception jenisnya benar
        future2.exceptionally(ex -> {
            assertInstanceOf(TooManyRequestException.class, ex.getCause());
            assertEquals("Service sedang menangani request lain, mohon tunggu beberapa saat", ex.getCause().getMessage());
            return null;
        });

        // Tunggu request pertama selesai agar tidak mempengaruhi test lain
        future1.get();
    }
}
