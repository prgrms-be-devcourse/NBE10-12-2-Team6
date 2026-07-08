package csh.back.domain.vote.vote.jobs;

import com.back.domain.order.entity.Order;
import com.back.domain.order.enums.OrderStatus;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaCursorItemReader;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Configuration
public class VoteBatchConfig {
    private final int CHUNK_SIZE = 100;

    // ---------------------------------------------------------
    //  1. Reader: DB에서 데이터 읽어오기 (JpaPagingItemReader)
    // ---------------------------------------------------------
    @StepScope
    @Bean
    public JpaCursorItemReader<Order> myReader(EntityManagerFactory entityManagerFactory) {
        // 시작 시간: 어제 14:00:00
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(14, 0));

        // 종료 시간: 오늘 14:00:00 (초를 빼지 않음)
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0));
        OrderStatus status = OrderStatus.PAYMENT_COMPLETE;
        return new JpaCursorItemReaderBuilder<Order>()
                .name("OrderReader")
                .entityManagerFactory(entityManagerFactory)
                // 실행할 JPQL 쿼리 (결제완료이면서 어제 14시 이후 오늘 13시59분59초 이전 상태)
                .queryString("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt >= :startDate AND o.createdAt < :endDate")
                .parameterValues(Map.of(
                        "status", status,
                        "startDate", startDate,
                        "endDate", endDate
                )).build();

    }

    // ---------------------------------------------------------
    //  2. Processor: 데이터 가공하기
    // ---------------------------------------------------------
    @Bean
    public ItemProcessor<Order, Order> myProcessor() {
        return order -> {
            // 배송상태 변경(결제 완료 -> 상품 준비 중)
            order.advanceToNextStatus();
//            if(order.getStatus() == DELIVERED) return null;
            return order; // 가공된 데이터를 Writer로 넘김
        };
    }

    // ---------------------------------------------------------
    //  3. Writer: 처리된 데이터 DB에 반영하기 (JpaItemWriter)
    // ---------------------------------------------------------
    @Bean
    public JpaItemWriter<Order> myWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Order>()
                .entityManagerFactory(entityManagerFactory)
                // JpaItemWriter는 내부적으로 EntityManager.merge()를 사용해서
                // 변경된 엔티티를 DB에 알아서 Update 해줌
                .build();
    }

    // ---------------------------------------------------------
    //  Step 조립하기 (위에서 만든 3가지를 합침)
    // ---------------------------------------------------------
    @Bean
    public Step orderDeliveryStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  JpaCursorItemReader<Order> myReader,
                                  ItemProcessor<Order, Order> myProcessor,
                                  JpaItemWriter<Order> myWriter) {

        return new StepBuilder("orderDeliveryStep", jobRepository)
                .<Order, Order>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(myReader)
                .processor(myProcessor)
                .writer(myWriter)
                .build();
    }


    // GlobalConfig에서 정의한 리스너와 ID 생성기를 주입받아 조립
    @Bean
    public Job orderDeliveryJob(JobRepository jobRepository,
                                Step orderDeliveryStep,
                                JobExecutionListener globalJobListener, // 👈 공통 리스너 가져옴
                                RunIdIncrementer globalRunIdIncrementer) { // 👈 공통 생성기 가져옴

        return new JobBuilder("orderDeliveryJob", jobRepository)
                .incrementer(globalRunIdIncrementer) // 매번 강제 실행되도록 파라미터 ID 증가
                .listener(globalJobListener)         // 실패하면 슬랙 알람 오도록 부착
                .start(orderDeliveryStep)
                .build();
    }
    
}