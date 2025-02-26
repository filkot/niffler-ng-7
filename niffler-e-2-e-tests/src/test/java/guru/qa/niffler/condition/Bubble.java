package guru.qa.niffler.condition;

public record Bubble(Color color, String text) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bubble bubble = (Bubble) obj;
        return color.rgb.equals(bubble.color.rgb) && text.equals(bubble.text);
    }

    @Override
    public String toString() {
        return String.format("Bubble(color=%s, text=%s)", color, text);
    }
}
