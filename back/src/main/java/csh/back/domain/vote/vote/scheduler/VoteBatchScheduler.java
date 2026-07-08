package csh.back.domain.vote.vote.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoteBatchScheduler {

    private final JobOperator jobOperator;
    private final Job orderDeliveryJob;

    @Scheduled(cron = "0 0 14 * * *")
    public void runOrderDeliveryBatch() {
        log.info("스케줄러 기동 - orderDeliveryJob 실행 요청");
        try {
            JobExecution execution = jobOperator.startNextInstance(orderDeliveryJob);
            log.info("배치 시작됨 - executionId={}, status={}",
                    execution.getId(), execution.getStatus());

        } catch (Exception e) {
            // incrementer 미설정 포함, 기타 예상치 못한 오류
            log.error("배치 실행 중 오류", e);
        }
    }
}