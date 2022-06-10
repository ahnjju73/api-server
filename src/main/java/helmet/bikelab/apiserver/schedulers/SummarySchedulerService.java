package helmet.bikelab.apiserver.schedulers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import helmet.bikelab.apiserver.schedulers.internal.WorkspaceQuartz;
import helmet.bikelab.apiserver.utils.PushComponent;
import helmet.bikelab.apiserver.utils.Senders;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class SummarySchedulerService extends WorkspaceQuartz {

    private final Senders senders;
    private final PushComponent pushComponent;

    @Value("${application.monitoring}")
    private Boolean isMonitoring;

    @Override
    @Transactional
    public void executeInternal(JobExecutionContext context) {
        doing();
    }

    public void doing() {
        Integer todayEstimateCount = (Integer)getItem("quartz.summary.getTodayEstimateSummary", null);
        Integer estimateSummaryCreatedAt = (Integer)getItem("quartz.summary.getTodayEstimateSummaryCreatedAt", null);
        Integer estimateSummaryCompletedAt = (Integer)getItem("quartz.summary.getTodayEstimateSummaryCompletedAt", null);
        Map paidMap = (Map)getItem("quartz.summary.getTodayEstimateSummaryPaidAt", null);
        SlackMessage slackMessage = new SlackMessage(todayEstimateCount, estimateSummaryCreatedAt, estimateSummaryCompletedAt, paidMap);
        send(slackMessage);
    }

    private void send(SlackMessage slackMessage){
        Map<String, Object> params = new HashMap<>();
        List blocks = new ArrayList<>();
        blocks.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "어제 총 [" + slackMessage.todayEstimateCount + "] 개의 견적서가 등록되었습니다.\n결제된 견적서의 총 금액은 *" + slackMessage.estimateTotalPricePaidAt + "원* 입니다."
                )
        ));
        List blockSecond = new ArrayList();
        blockSecond.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "* 생성:*\n" + slackMessage.estimateCountCreatedAt + "개"
                )
        ));
        blockSecond.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "* 결제완료:*\n" + slackMessage.estimateCountPaidAt + "개"
                )
        ));
        blockSecond.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "* 수리완료:*\n" + slackMessage.estimateCountCompletedAt + "개"
                )
        ));

        blockSecond.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "* Date:*\n" + LocalDate.now()
                )
        ));

        blockSecond.add(ImmutableMap.of(
                "type", "divider"
        ));

        params.put("blocks", blocks);
        params.put("attachments", Arrays.asList(ImmutableMap.of("blocks", blockSecond)));
//        params.put("blocks", ImmutableMap.of(
//                "type", "divider"
//        ));
        String body = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            body = mapper.writeValueAsString(params);
            if(body != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
                HttpEntity entity = new HttpEntity(body, headers);
                if(isMonitoring){
                    restTemplate.postForEntity("https://hooks.slack.com/services/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", entity, String.class);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class SlackMessage {

        public SlackMessage(Integer todayEstimateCount, Integer estimateSummaryCreatedAt, Integer estimateSummaryCompletedAt, Map paidMap) {
            this.todayEstimateCount = todayEstimateCount;
            this.estimateCountCreatedAt = estimateSummaryCreatedAt;
            this.estimateCountCompletedAt = estimateSummaryCompletedAt;
            if(bePresent(paidMap)){
                this.estimateCountPaidAt = (Long)paidMap.get("paid_at");
                this.estimateTotalPricePaidAt = (Integer)paidMap.get("total_price");
            }
            if(!bePresent(this.todayEstimateCount)) this.todayEstimateCount = 0;
            if(!bePresent(this.estimateCountCreatedAt)) this.estimateCountCreatedAt = 0;
            if(!bePresent(this.estimateCountCompletedAt)) this.estimateCountCompletedAt = 0;
            if(!bePresent(this.estimateCountPaidAt)) this.estimateCountPaidAt = 0l;
            if(!bePresent(this.estimateTotalPricePaidAt)) this.estimateTotalPricePaidAt = 0;
        }

        private Integer todayEstimateCount;
        private Integer estimateCountCreatedAt;
        private Integer estimateCountCompletedAt;
        private Long estimateCountPaidAt;
        private Integer estimateTotalPricePaidAt;
    }


}
