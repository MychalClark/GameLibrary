package edu.ranken.mychal_clark.gamelibrary.ui;

import androidx.annotation.NonNull;

public class SpinnerOption<T> {
    private final String text;
    private final T value;

    public SpinnerOption(@NonNull String text, T value) {
        this.text = text;
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public T getValue() {
        return value;
    }
}
