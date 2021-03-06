package com.qubit.metricsflow.metrics.transform;

import com.qubit.metricsflow.core.fn.ConvertMetricUpdateEventToJson;
import com.qubit.metricsflow.metrics.core.event.MetricUpdateEvent;

import com.google.cloud.dataflow.sdk.io.PubsubIO;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.PDone;

public class DumpMetricsToPubSub extends DumpMetricsTransform {
    private String destTopicName;

    public DumpMetricsToPubSub(String destTopicName) {
        this.destTopicName = destTopicName;
    }

    @Override
    public PDone apply(PCollection<MetricUpdateEvent> input) {
        return input
            .apply(ParDo.of(new ConvertMetricUpdateEventToJson()))
            .apply(PubsubIO.Write.topic(destTopicName));
    }
}
