package com.qubit.metricsflow.core.fn;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.qubit.metricsflow.core.TestUtils;
import com.qubit.metricsflow.core.types.MetricUpdateKey;
import com.qubit.metricsflow.core.types.MetricUpdateValue;
import com.qubit.metricsflow.core.utils.MetricTypeTags;
import com.qubit.metricsflow.metrics.core.types.MetricAggregationType;
import com.qubit.metricsflow.metrics.core.types.MetricWindowType;

import com.google.cloud.dataflow.sdk.transforms.DoFnTester;
import com.google.cloud.dataflow.sdk.values.KV;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class BranchByAggregationTypeTest {
    @Test
    public void fixedWindowMetrics_shouldAllWork() {
        testAllMetricTypesFor(MetricWindowType.Fixed);
    }

    @Test
    public void slidingWindowMetrics_shouldAllWork() {
        testAllMetricTypesFor(MetricWindowType.Sliding);
    }

    @Test(expected = RuntimeException.class)
    public void  doFn_shouldRaiseAnException_ifNoMetricTypesArePresent_FixedWindow() {
        testNoMetricTypesPresentFor(MetricWindowType.Fixed);
    }

    @Test(expected = RuntimeException.class)
    public void  doFn_shouldRaiseAnException_ifNoMetricTypesArePresent_SlidingWindow() {
        testNoMetricTypesPresentFor(MetricWindowType.Sliding);
    }

    private static void testNoMetricTypesPresentFor(MetricWindowType windowType) {
        DoFnTester<KV<MetricUpdateKey, MetricUpdateValue>, Void> fnTester = createFnTester(windowType);
        KV<MetricUpdateKey, MetricUpdateValue> event = makeKV(":(", 6.65, false);

        List<Void> output = fnTester.processBatch(event);
        TestUtils.assertThatListIsEmpty(output);
    }

    private static void testAllMetricTypesFor(MetricWindowType windowType) {
        DoFnTester<KV<MetricUpdateKey, MetricUpdateValue>, Void> fnTester = createFnTester(windowType);
        KV<MetricUpdateKey, MetricUpdateValue> event = makeKV("-_-", 6.66, true);

        List<Void> output = fnTester.processBatch(event);
        TestUtils.assertThatListIsEmpty(output);

        MetricTypeTags.getAll().forEach(tag -> {
            List<KV<MetricUpdateKey, Double>> out = fnTester.takeSideOutputElements(tag);
            TestUtils.assertThatListIsNotEmpty(out);

            KV<MetricUpdateKey, Double> value = out.get(0);
            assertThat(value, is(KV.of(event.getKey(), event.getValue().getValue())));
        });
    }

    private static KV<MetricUpdateKey, MetricUpdateValue> makeKV(String name, Double value, boolean fillAggrTypes) {
        MetricUpdateKey key = MetricUpdateKey.of(name, Collections.emptyList());
        MetricUpdateValue val = mock(MetricUpdateValue.class);
        if (fillAggrTypes) {
            when(val.getFixedWindowAggregations()).thenReturn(EnumSet.allOf(MetricAggregationType.class));
            when(val.getSlidingWindowAggregations()).thenReturn(EnumSet.allOf(MetricAggregationType.class));
        } else {
            when(val.getFixedWindowAggregations()).thenReturn(EnumSet.noneOf(MetricAggregationType.class));
            when(val.getSlidingWindowAggregations()).thenReturn(EnumSet.noneOf(MetricAggregationType.class));
        }
        when(val.getValue()).thenReturn(value);

        return KV.of(key, val);
    }

    private static DoFnTester<KV<MetricUpdateKey, MetricUpdateValue>, Void> createFnTester(MetricWindowType windowType) {
        return DoFnTester.of(new BranchByAggregationType(windowType));
    }
}