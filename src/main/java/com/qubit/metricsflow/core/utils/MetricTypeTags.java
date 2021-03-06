package com.qubit.metricsflow.core.utils;

import com.qubit.metricsflow.core.types.MetricUpdateKey;

import com.google.cloud.dataflow.sdk.values.KV;
import com.google.cloud.dataflow.sdk.values.TupleTag;
import com.google.cloud.dataflow.sdk.values.TupleTagList;

import java.util.Arrays;
import java.util.List;

public class MetricTypeTags {
    public static final TupleTag<KV<MetricUpdateKey, Double>> MAX = new TupleTag<KV<MetricUpdateKey, Double>>() {};
    public static final TupleTag<KV<MetricUpdateKey, Double>> MIN = new TupleTag<KV<MetricUpdateKey, Double>>() {};
    public static final TupleTag<KV<MetricUpdateKey, Double>> MEAN = new TupleTag<KV<MetricUpdateKey, Double>>() {};
    public static final TupleTag<KV<MetricUpdateKey, Double>> SUM = new TupleTag<KV<MetricUpdateKey, Double>>() {};

    public static TupleTagList getTupleTagList() {
        return TupleTagList.of(Arrays.asList(MAX, MIN, MEAN, SUM));
    }

    public static List<TupleTag<KV<MetricUpdateKey, Double>>> getAll() {
        return Arrays.asList(MAX, MIN, MEAN, SUM);
    }

}
