package com.example.rubbishclassifywork.HelperClass;

public class result {
    private String name;
    private String kind;
    private float confidence;

    public void setName(String name) {
        this.name = name;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public result(String name, String kind, float confidence) {
        this.name = name;
        this.kind = kind;
        this.confidence = confidence;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public float getConfidence() {
        return confidence;
    }
}
