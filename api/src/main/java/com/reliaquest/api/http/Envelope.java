package com.reliaquest.api.http;

public record Envelope<T>(T data, String status) {}
