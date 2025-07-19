package com.store.soattechchallenge.payment.usecases.commands;

public record RenderQrCodeCommand(String id, int width, int height) {
}
