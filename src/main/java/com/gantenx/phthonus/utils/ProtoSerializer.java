package com.gantenx.phthonus.utils;

import com.google.protobuf.GeneratedMessageV3;
import org.apache.kafka.common.serialization.Serializer;

public class ProtoSerializer<T extends GeneratedMessageV3> implements Serializer<T> {
    @Override
    public byte[] serialize(String topic, T data) {
        return data.toByteArray();
    }
}
