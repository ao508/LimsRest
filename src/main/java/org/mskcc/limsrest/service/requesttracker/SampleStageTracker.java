package org.mskcc.limsrest.service.requesttracker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Map;

/**
 * Represents a Tracked Stage in IGO's project tracker
 * Notes:
 *  - Stages are initialized as complete
 *  - Failed WorkflowSamples are considered to have "completed" that stage and do not set a stage to incomplete
 *
 *  @author David Streid
 */
public class SampleStageTracker extends StatusTracker {
    private static Log log = LogFactory.getLog(SampleStageTracker.class);

    private Integer endingSamples;      // Number of samples that have completed this stage and moved on to the next
    private Integer failedSamples;      // Number of failed samples at this stage (considered incomplete)

    public SampleStageTracker(String stage, Integer size, Integer endingSamples, Long startTime, Long updateTime) {
        this.complete = Boolean.TRUE;   // Stages default to complete. Only an update can set to incomplete
        this.endingSamples = endingSamples;
        this.failedSamples = 0;
        this.startTime = startTime;
        this.updateTime = updateTime;

        setStage(stage);
        setSize(size);
    }

    public Integer getEndingSamples() {
        return endingSamples;
    }

    public String getStage() {
        return stage;
    }

    public void addStartingSample(Integer count) {
        if (count == null) {
            log.error("Failed to add a null-count size to the startingSamples of Stage");
            return;
        }
        super.size += count;
    }

    public void addEndingSample(Integer count) {
        if (count == null) {
            log.error("Failed to add a null-count size to the endingSample of Stage");
            return;
        }
        this.endingSamples += count;
    }

    /**
     * Increments the count of failed samples
     */
    public void addFailedSample() {
        this.failedSamples += 1;
    }

    /**
     * Returns whether the current stage has any failed samples
     * @return
     */
    public Integer getFailedSamplesCount() {
        return this.failedSamples;
    }

    /**
     * Updates Sample times when merging with another sample
     *
     * @param statusTracker
     */
    public void updateStageTimes(StatusTracker statusTracker) {
        Long startTime = statusTracker.getStartTime();
        Long updateTime = statusTracker.getUpdateTime();
        if (this.startTime > startTime) {
            setStartTime(startTime);
        }
        if (this.updateTime < updateTime) {
            setUpdateTime(updateTime);
        }
    }

    public Map<String, Object> toApiResponse() {
        Map<String, Object> apiMap = super.toApiResponse();
        apiMap.put("completedSamples", this.endingSamples);
        apiMap.put("failedSamples", this.failedSamples);

        return apiMap;
    }
}
